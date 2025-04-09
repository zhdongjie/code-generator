package com.original.generator.core.utils;

import com.original.generator.core.domain.bo.BusinessModuleBo;
import com.original.generator.core.domain.bo.FieldBo;
import com.original.generator.core.domain.bo.GenerateProjectBo;
import com.original.generator.core.domain.entity.VelocityGroupEntity;
import com.original.generator.core.domain.entity.VelocityTemplateEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 代码生成工具类
 * 负责根据模板生成项目代码文件，包括：
 * 1. 后端Java项目代码
 * 2. 前端Vue项目代码
 * 3. 其他类型的项目代码
 * <p>
 * 该类是代码生成器的核心组件，通过解析模板和业务模块信息，
 * 生成符合项目规范的各种代码文件。
 *
 * @author 代码生成器团队
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CodeGeneratorUtils {

    /**
     * Velocity模板工具类
     * 用于渲染模板内容
     */
    private final VelocityTemplateUtils velocityTemplateUtils;

    /**
     * 生成代码文件
     * 根据模板组和业务模块信息，生成对应的代码文件
     * <p>
     * 步骤：
     * 1. 获取模板组
     * 2. 遍历业务模块
     * 3. 生成对应的代码文件
     *
     * @param templateGroup     模板组，包含模板列表和类型信息
     * @param targetPath        目标路径，生成的代码文件将保存在此路径下
     * @param generateProjectBo 项目生成信息，包含项目配置和业务模块列表
     */
    public void generate(VelocityGroupEntity templateGroup, String targetPath, GenerateProjectBo generateProjectBo) {
        // 步骤1: 获取模板组
        if (templateGroup == null) {
            log.error("Template group is null");
            return;
        }
        String groupType = templateGroup.getGroupType();

        // 步骤2: 获取该组下的所有模板
        List<VelocityTemplateEntity> templates = templateGroup.getTemplateList();
        if (templates.isEmpty()) {
            log.error("No templates found in group: {}", templateGroup.getGroupName());
            return;
        }

        // 步骤3: 遍历业务模块
        generateProjectBo.getBusinessModuleList().forEach(module -> {
            // 步骤4: 为每个模块生成对应的代码文件
            templates.forEach(template -> {
                try {
                    generateCodeFile(groupType, template, targetPath, generateProjectBo, module);
                } catch (Exception e) {
                    log.error("Failed to generate code file for module: {}, template: {}",
                            module.getModuleName(), template.getTemplateName(), e);
                }
            });
        });
    }

    /**
     * 生成单个代码文件
     * 根据模板和业务模块信息，生成单个代码文件
     * <p>
     * 步骤：
     * 1. 准备文件路径
     * 2. 创建变量映射
     * 3. 注入变量
     * 4. 渲染模板
     * 5. 写入文件
     *
     * @param groupType         模板分类，如"BACKEND"或"FRONTEND"
     * @param template          模板实体，包含模板内容和保存路径
     * @param targetPath        目标路径，生成的代码文件将保存在此路径下
     * @param generateProjectBo 项目生成信息，包含项目配置
     * @param module            业务模块信息，包含模块名称和字段列表
     * @throws IOException 如果文件操作失败
     */
    private void generateCodeFile(
            String groupType,
            VelocityTemplateEntity template,
            String targetPath,
            GenerateProjectBo generateProjectBo,
            BusinessModuleBo module
    ) throws IOException {
        // 步骤1: 准备文件路径
        String filePath = buildFilePath(groupType, template, targetPath, generateProjectBo, module);
        Path path = Paths.get(filePath);

        // 确保目录存在
        Files.createDirectories(path.getParent());

        // 步骤2: 创建变量映射
        Map<String, Object> context = new HashMap<>();

        // 步骤3: 注入变量
        injectVariables(context, generateProjectBo, module);

        // 步骤4: 渲染模板
        String content = velocityTemplateUtils.render(String.valueOf(template.getId()), context);

        // 步骤5: 写入文件
        try (Writer writer = new FileWriter(filePath)) {
            writer.write(content);
            log.info("Generated code file: {}", filePath);
        }
    }

    /**
     * 构建文件路径
     * 根据模板和业务模块信息，构建生成文件的完整路径
     * <p>
     * 步骤：
     * 1. 获取模板保存路径
     * 2. 替换路径中的变量
     * 3. 构建完整路径
     *
     * @param groupType         模板分类，如"BACKEND"或"FRONTEND"
     * @param template          模板实体，包含模板内容和保存路径
     * @param targetPath        目标路径，生成的代码文件将保存在此路径下
     * @param generateProjectBo 项目生成信息，包含项目配置
     * @param module            业务模块信息，包含模块名称
     * @return 生成文件的完整路径
     */
    private String buildFilePath(
            String groupType,
            VelocityTemplateEntity template,
            String targetPath,
            GenerateProjectBo generateProjectBo,
            BusinessModuleBo module
    ) {
        String savePath = template.getSavePath();
        String templateName = template.getTemplateName();
        if (groupType.equals("BACKEND")) {
            // 后端项目
            String packagePath = generateProjectBo.getBackendProject()
                    .getPackagePath()
                    .replace(".", File.separator);
            // 替换包名
            savePath = savePath.replace("${packagePath}", packagePath);
            // 替换模块名
            savePath = savePath.replace("${moduleName}", StringConverterUtil.underscoreToCamelCase(module.getModuleName()));
            // 替换类名
            savePath = savePath.replace("${className}", module.getModuleName());
            // 替换文件名
            templateName = templateName.replace("${moduleName}", module.getModuleName());
        } else {
            // 前端项目
            // 替换模块名
            savePath = savePath.replace("${moduleName}", StringConverterUtil.toLowerCaseKebabCase(module.getModuleName()));
        }
        return (targetPath + File.separator + savePath + File.separator + templateName).replace("/", File.separator);
    }

    /**
     * 注入变量到上下文
     * 将项目信息、模块信息和字段信息注入到模板上下文中
     * <p>
     * 步骤：
     * 1. 注入项目基本信息
     * 2. 注入模块信息
     * 3. 注入字段信息
     *
     * @param context           模板上下文，用于存储变量
     * @param generateProjectBo 项目生成信息，包含项目配置
     * @param module            业务模块信息，包含模块名称和字段列表
     */
    private void injectVariables(Map<String, Object> context, GenerateProjectBo generateProjectBo,
                                 BusinessModuleBo module) {
        // 步骤1: 注入项目基本信息
        context.put("packagePath", generateProjectBo.getBackendProject().getPackagePath() + "." + StringConverterUtil.underscoreToCamelCase(module.getModuleName()));
        context.put("author", generateProjectBo.getBackendProject().getAuthor());
        // 步骤2: 注入模块信息
        context.put("module", module);
        // 步骤3: 注入需要导的包
        Set<String> filedTypePathSet = module.getFieldList().stream()
                .map(FieldBo::getFieldTypePath)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        context.put("importList", filedTypePathSet);
    }
} 