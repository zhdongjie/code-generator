package com.original.generator.core.utils;

import com.original.generator.core.domain.bo.BackendProjectBo;
import com.original.generator.core.domain.bo.BusinessModuleBo;
import com.original.generator.core.domain.bo.FieldBo;
import com.original.generator.core.domain.bo.GenerateProjectBo;
import com.original.generator.core.domain.dto.GenerateProjectDto;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;


public class JavaClassGeneratorUtils {

    private static final Map<String, Set<Map<String, String>>> templateGroupMap = Map.of(
            "mybatis-plus", Set.of(
                    Map.of(
                            "templateName", "#Entity.java.vm",
                            "templatePackagePath", "domain.entity"
                    ),
                    Map.of(
                            "templateName", "#Mapper.java.vm",
                            "templatePackagePath", "mapper"
                    ),
                    Map.of(
                            "templateName", "Insert#Dto.java.vm",
                            "templatePackagePath", "domain.dto"
                    ),
                    Map.of(
                            "templateName", "Update#Dto.java.vm",
                            "templatePackagePath", "domain.dto"
                    ),
                    Map.of(
                            "templateName", "#Vo.java.vm",
                            "templatePackagePath", "domain.vo"
                    ),
                    Map.of(
                            "templateName", "Select#Dto.java.vm",
                            "templatePackagePath", "domain.dto"
                    ),
                    Map.of(
                            "templateName", "#Service.java.vm",
                            "templatePackagePath", "service"
                    ),
                    Map.of(
                            "templateName", "#ServiceImpl.java.vm",
                            "templatePackagePath", "service.impl"
                    ),
                    Map.of(
                            "templateName", "#Controller.java.vm",
                            "templatePackagePath", "controller"
                    )
            )
    );

    private static VelocityEngine velocityEngine;

    /**
     * 单例模式 对外公开的方法 用于获取对象
     */
    public static VelocityEngine getInstance() {
        if (velocityEngine == null) {
            velocityEngine = new VelocityEngine();

            // 加载Velocity配置
            Properties properties = new Properties();
            properties.setProperty("resource.loader", "class");
            properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            properties.setProperty("input.encoding", "UTF-8");
            properties.setProperty("output.encoding", "UTF-8");
            velocityEngine.init(properties);
        }
        return velocityEngine;
    }

    public static void generate(String templateGroup, String outputPath, String packageName, GenerateProjectBo generateProject) {
        Set<Map<String, String>> templateSet = templateGroupMap.get(templateGroup);
        for (Map<String, String> templateMap : templateSet) {
            String templateName = templateMap.get("templateName");
            String packagePath = templateMap.get("packagePath");
        }
    }

    public static void generateJavaClass(String templateName, String templatePath, String outputPath, String packagePath, GenerateProjectBo generateProjectBo) {
        try {
            // 加载模板
            Template template = getInstance().getTemplate(templatePath + templateName);
            List<BusinessModuleBo> businessModuleList = generateProjectBo.getBusinessModuleList();
            BusinessModuleBo businessModuleBo = businessModuleList.get(0);
            String fileName = getTheVmFileName(templateName, businessModuleBo.getModuleName());
            if (fileName.isEmpty()) {
                return;
            }
            List<FieldBo> fields = businessModuleBo.getFieldList();
            Set<String> filedTypePathSet = fields.stream()
                    .map(FieldBo::getFieldTypePath)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toSet());
            // 创建VelocityContext并添加动态数据
            VelocityContext context = new VelocityContext();
            context.put("packagePath", packagePath);
            context.put("module", businessModuleBo);
            context.put("importList", filedTypePathSet);

            // 指定输出文件路径
            File outputFile = new File(outputPath + fileName);
            // 创建新目录
            Path newFilePath = outputFile.toPath();
            Path newDirPath = newFilePath.getParent();

            if (newDirPath != null) {
                // 创建目录
                Files.createDirectories(newDirPath);
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
                // 合并模板和数据
                template.merge(context, writer);
                System.out.println("文件生成成功：" + outputFile.getAbsolutePath());
            }
        } catch (ResourceNotFoundException e) {
            System.err.println("模板文件未找到，请检查路径是否正确：" + e.getMessage());
        } catch (ParseErrorException e) {
            System.err.println("模板解析错误，请检查模板语法：" + e.getMessage());
        } catch (Exception e) {
            System.err.println("文件生成失败：" + e.getMessage());
        }
    }

    public static String getTheVmFileName(String templateName, String moduleName) {
        if (templateName.isEmpty() || moduleName.isEmpty()) {
            return "";
        }
        int lastDotIndex = templateName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        String beforeReplacementFileName = templateName.substring(0, lastDotIndex);
        if (beforeReplacementFileName.isEmpty()) {
            return "";
        }

        return beforeReplacementFileName.replace("#", moduleName);
    }

    public static void main(String[] args) {
        String templateGroup = "mybatis-plus";
        Set<Map<String, String>> templateSet = templateGroupMap.get(templateGroup);
        GenerateProjectDto generateProjectDto = GenerateProjectUtils.initGenerateProject();
        GenerateProjectBo generateProject = GenerateProjectUtils.buildGenerateProject(generateProjectDto);

        BackendProjectBo backendProject = generateProject.getBackendProject();
        String packagePath = backendProject.getPackagePath();
        String backendProjectPath = generateProject.getFilePath() + File.separator + generateProject.getProjectName()
                + File.separator + backendProject.getName() + File.separator
                + "src\\main\\java\\" + packagePath.replace(".", "\\");

        for (Map<String, String> templateMap : templateSet) {
            String templateName = templateMap.get("templateName");
            String templatePackagePath = templateMap.get("templatePackagePath");
            String templatePath = "templates" + "/" + templateGroup + "/";
            String outputPath = backendProjectPath + "\\" + templatePackagePath.replace(".", "\\") + "\\";
            generateJavaClass(templateName, templatePath, outputPath, packagePath, generateProject);
        }
    }

}
