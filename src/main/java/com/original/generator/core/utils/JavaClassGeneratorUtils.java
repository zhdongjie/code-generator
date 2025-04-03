package com.original.generator.core.utils;

import com.original.generator.core.domain.bo.BusinessModuleBo;
import com.original.generator.core.domain.bo.FieldBo;
import com.original.generator.core.domain.bo.GenerateProjectBo;
import com.original.generator.core.domain.entity.VelocityTemplateEntity;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JavaClassGeneratorUtils {
    private static final Logger logger = LoggerFactory.getLogger(JavaClassGeneratorUtils.class);
    private final VelocityTemplateUtils velocityTemplateUtils;
    private static final Map<String, Set<VelocityTemplateEntity>> templateGroupMap = Map.of(
            "mybatis-plus", Set.of(
                    new VelocityTemplateEntity(
                            1L,
                            1L,
                            "package ${packagePath}.domain.entity;\n" +
                                    "\n" +
                                    "#foreach($import in $importList)\n" +
                                    "import $import;\n" +
                                    "#end\n" +
                                    "\n" +
                                    "import com.baomidou.mybatisplus.annotation.IdType;\n" +
                                    "import com.baomidou.mybatisplus.annotation.TableField;\n" +
                                    "import com.baomidou.mybatisplus.annotation.TableId;\n" +
                                    "import com.baomidou.mybatisplus.annotation.TableName;\n" +
                                    "import lombok.AllArgsConstructor;\n" +
                                    "import lombok.Data;\n" +
                                    "import lombok.NoArgsConstructor;\n" +
                                    "\n" +
                                    "@Data\n" +
                                    "@AllArgsConstructor\n" +
                                    "@NoArgsConstructor\n" +
                                    "@TableName(\"`$module.tableName`\")\n" +
                                    "public class ${module.moduleName}Entity {\n" +
                                    "\n" +
                                    "#foreach($field in $module.fieldList)\n" +
                                    "#if(${field.comment})\n" +
                                    "    /**\n" +
                                    "     * ${field.comment}\n" +
                                    "     */#end\n" +
                                    "\n" +
                                    "    #if($field.primaryKey)\n" +
                                    "    @TableId(type = IdType.ASSIGN_ID, value = \"`$field.columnName`\")\n" +
                                    "    #end\n" +
                                    "    #if(!$field.primaryKey)\n" +
                                    "    @TableField(value = \"`$field.columnName`\")\n" +
                                    "    #end\n" +
                                    "    private $field.fieldType $!{field.fieldName};\n" +
                                    "\n" +
                                    "#end\n" +
                                    "}",
                            new Date(),
                            "templates/mybatis-plus/",
                            "#Entity.java.vm"
                    )
            )
    );

    public JavaClassGeneratorUtils(VelocityTemplateUtils velocityTemplateUtils) {
        this.velocityTemplateUtils = velocityTemplateUtils;
    }

    /**
     * 生成Java类文件
     */
    public void generate(String templateGroup, String outputPath, GenerateProjectBo generateProject) {
        Set<VelocityTemplateEntity> templateSet = templateGroupMap.get(templateGroup);
        if (templateSet == null) {
            logger.error("Template group not found: {}", templateGroup);
            return;
        }

        for (VelocityTemplateEntity templateMap : templateSet) {
            generateJavaClass(templateMap.getTemplateName(), templateMap.getId(), outputPath, templateMap.getSavePath(), generateProject);
        }
    }

    /**
     * 生成单个Java类文件
     */
    public void generateJavaClass(String templateName, Long templateId, String outputPath, String packagePath, GenerateProjectBo generateProjectBo) {
        try {
            Template template = velocityTemplateUtils.getTemplate(String.valueOf(templateId));

            List<BusinessModuleBo> businessModuleList = generateProjectBo.getBusinessModuleList();
            if (businessModuleList.isEmpty()) {
                logger.warn("No business modules found in project");
                return;
            }
            for (BusinessModuleBo businessModuleBo : businessModuleList) {
                String fileName = getTheVmFileName(templateName, businessModuleBo.getModuleName());
                if (fileName.isEmpty()) {
                    logger.warn("Invalid file name generated for template: {}", fileName);
                    return;
                }

                List<FieldBo> fields = businessModuleBo.getFieldList();
                Set<String> filedTypePathSet = fields.stream()
                        .map(FieldBo::getFieldTypePath)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toSet());

                VelocityContext context = new VelocityContext();
                String filePath = outputPath + File.separator + generateProjectBo.getBackendProject().getName()
                        + File.separator + packagePath;
                context.put("packagePath", filePath);
                context.put("module", businessModuleBo);
                context.put("importList", filedTypePathSet);

                File outputFile = new File(outputPath + fileName);
                Path newFilePath = outputFile.toPath();
                Path newDirPath = newFilePath.getParent();

                if (newDirPath != null) {
                    Files.createDirectories(newDirPath);
                }

                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
                    template.merge(context, writer);
                    logger.info("File generated successfully: {}", outputFile.getAbsolutePath());
                }
            }
        } catch (ResourceNotFoundException e) {
            logger.error("Template file not found: {}", e.getMessage());
        } catch (ParseErrorException e) {
            logger.error("Template parsing error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("File generation failed: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取生成的文件名
     */
    public String getTheVmFileName(String templateName, String moduleName) {
        if (templateName == null || templateName.isEmpty() || moduleName == null || moduleName.isEmpty()) {
            logger.warn("Invalid template name or module name");
            return "";
        }

        int lastDotIndex = templateName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            logger.warn("Invalid template name format: {}", templateName);
            return "";
        }

        String beforeReplacementFileName = templateName.substring(0, lastDotIndex);
        if (beforeReplacementFileName.isEmpty()) {
            logger.warn("Empty file name before replacement");
            return "";
        }

        return beforeReplacementFileName.replace("#", moduleName);
    }

}
