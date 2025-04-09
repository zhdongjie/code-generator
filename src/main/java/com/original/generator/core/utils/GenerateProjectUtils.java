package com.original.generator.core.utils;

import com.mybatisflex.core.query.QueryChain;
import com.original.generator.core.domain.bo.*;
import com.original.generator.core.domain.dto.BusinessModuleDto;
import com.original.generator.core.domain.dto.FieldDto;
import com.original.generator.core.domain.dto.GenerateProjectDto;
import com.original.generator.core.domain.entity.VelocityGroupEntity;
import com.original.generator.core.domain.info.TemplateProjectInfo;
import com.original.generator.core.exception.FileOperationException;
import com.original.generator.core.mapper.VelocityGroupMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

import static com.original.generator.core.domain.entity.table.VelocityGroupTableDef.velocity_group;

/**
 * 项目生成工具类
 * 负责根据模板生成完整的项目结构，包括：
 * 1. 克隆模板仓库
 * 2. 修改项目配置
 * 3. 生成数据库脚本
 * 4. 生成代码文件
 * <p>
 * 该类是代码生成器的核心组件，通过解析模板项目和业务模块信息，
 * 生成符合项目规范的各种代码文件和配置文件。
 *
 * @author 代码生成器团队
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class GenerateProjectUtils {
    /**
     * 代码生成工具类
     * 用于生成具体的代码文件
     */
    private final CodeGeneratorUtils codeGeneratorUtils;

    /**
     * 模板组数据访问对象
     * 用于查询模板组信息
     */
    private final VelocityGroupMapper velocityGroupMapper;

    /**
     * 修改文件夹
     * 递归遍历文件夹，收集所有文件并建立映射关系
     * <p>
     * 步骤：
     * 1. 验证文件夹有效性
     * 2. 遍历文件夹内容
     * 3. 过滤忽略的文件
     * 4. 递归处理子文件夹
     *
     * @param map         文件映射，Key为文件路径，Value为文件对象
     * @param folder      要处理的文件夹
     * @param ignoreNames 要忽略的文件名集合
     * @throws FileOperationException 如果文件夹操作失败
     */
    private void modifyFolder(Map<String, File> map, File folder, Set<String> ignoreNames) {
        if (folder == null) {
            throw new FileOperationException("Folder cannot be null");
        }

        if (!folder.exists() || !folder.isDirectory()) {
            throw new FileOperationException("Invalid folder path: " + folder.getAbsolutePath());
        }

        try {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (ignoreNames.contains(file.getName())) {
                        continue;
                    }
                    if (file.isFile()) {
                        map.put(file.getAbsolutePath(), file);
                    }
                    if (file.isDirectory()) {
                        modifyFolder(map, file, ignoreNames);
                    }
                }
            }
        } catch (Exception e) {
            throw new FileOperationException("Failed to modify folder: " + folder.getAbsolutePath(), e);
        }
    }

    /**
     * 替换文件内容
     * 根据模板项目信息和生成项目信息，替换文件中的配置项
     * <p>
     * 步骤：
     * 1. 替换包路径
     * 2. 替换POM文件配置
     * 3. 替换应用配置文件
     * 4. 替换作者信息
     *
     * @param fileContent         文件内容
     * @param templateProjectInfo 模板项目信息
     * @param generateProject     生成项目信息
     * @param newKey              新文件路径
     * @return 替换后的文件内容
     * @throws FileOperationException 如果替换操作失败
     */
    private String replaceFileContent(String fileContent, TemplateProjectInfo templateProjectInfo, GenerateProjectBo generateProject, String newKey) {
        if (fileContent == null) {
            throw new FileOperationException("File content cannot be null");
        }

        BackendProjectBo backendProject = generateProject.getBackendProject();
        if (backendProject == null) {
            throw new FileOperationException("Backend project cannot be null");
        }

        fileContent = fileContent.replace(templateProjectInfo.getPackagePath(), backendProject.getPackagePath());
        if (newKey.contains("pom.xml")) {
            fileContent = fileContent
                    .replace("<groupId>" + templateProjectInfo.getGroupId() + "</groupId>", "<groupId>" + backendProject.getGroupId() + "</groupId>")
                    .replace("<artifactId>" + templateProjectInfo.getArtifactId() + "</artifactId>", "<artifactId>" + backendProject.getArtifactId() + "</artifactId>")
                    .replace("<version>" + templateProjectInfo.getVersion() + "</version>", "<version>" + backendProject.getVersion() + "</version>")
                    .replace("<description>" + templateProjectInfo.getDescription() + "</description>", "<description>" + backendProject.getDescription() + "</description>");
        }

        if (newKey.contains("application.yml")) {
            fileContent = fileContent
                    .replace(templateProjectInfo.getPort(), backendProject.getPort())
                    .replace(templateProjectInfo.getDatabaseServer(), backendProject.getDatabaseServer())
                    .replace(templateProjectInfo.getDatabasePort(), backendProject.getDatabasePort())
                    .replace(templateProjectInfo.getDatabase(), backendProject.getDatabase())
                    .replace(templateProjectInfo.getDatabaseUser(), backendProject.getDatabaseUser())
                    .replace(templateProjectInfo.getDatabasePassword(), backendProject.getDatabasePassword());
        }

        if (StringUtils.isNotBlank(templateProjectInfo.getAuthor())) {
            fileContent = fileContent.replace("@author " + templateProjectInfo.getAuthor(), "@author " + backendProject.getAuthor());
        }

        return fileContent;
    }

    /**
     * 删除目录
     * 递归删除指定路径下的所有文件和目录
     * <p>
     * 步骤：
     * 1. 遍历目录树
     * 2. 设置文件可写
     * 3. 删除文件
     * 4. 删除目录
     *
     * @param path 要删除的目录路径
     * @throws FileOperationException 如果删除操作失败
     */
    private void deleteDirectory(Path path) {
        if (path == null) {
            throw new FileOperationException("Path cannot be null");
        }

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File currentFile = file.toFile();
                    if (!currentFile.canWrite()) {
                        currentFile.setWritable(true);
                    }
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        File currentDir = dir.toFile();
                        if (!currentDir.canWrite()) {
                            currentDir.setWritable(true);
                        }
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        } catch (IOException e) {
            throw new FileOperationException("Failed to delete directory: " + path, e);
        }
    }

    /**
     * 创建和修改文件
     * 根据模板文件创建新文件，并替换文件内容
     * <p>
     * 步骤：
     * 1. 构建新文件路径
     * 2. 创建目录结构
     * 3. 读取文件内容
     * 4. 替换文件内容
     * 5. 写入新文件
     *
     * @param map                 文件映射
     * @param templateProjectInfo 模板项目信息
     * @param generateProject     生成项目信息
     * @param cover               是否覆盖已存在的文件
     * @throws FileOperationException 如果文件操作失败
     */
    private void createAndModifyFiles(Map<String, File> map, TemplateProjectInfo templateProjectInfo, GenerateProjectBo generateProject, boolean cover) {
        if (map == null || map.isEmpty()) {
            throw new FileOperationException("File map cannot be null or empty");
        }

        BackendProjectBo backendProject = generateProject.getBackendProject();
        if (backendProject == null) {
            throw new FileOperationException("Backend project cannot be null");
        }

        try {
            for (Map.Entry<String, File> entry : map.entrySet()) {
                String newKey = entry.getKey()
                        .replace(templateProjectInfo.getName(), backendProject.getName())
                        .replace(templateProjectInfo.getPackagePath().replace(".", File.separator), backendProject.getPackagePath().replace(".", File.separator));

                Path newFilePath = Paths.get(newKey);
                Path newDirPath = newFilePath.getParent();

                if (newDirPath != null) {
                    Files.createDirectories(newDirPath);
                }

                if (Files.exists(newFilePath) && !cover) {
                    continue;
                }

                String fileContent = Files.readString(entry.getValue().toPath(), StandardCharsets.UTF_8);
                fileContent = replaceFileContent(fileContent, templateProjectInfo, generateProject, newKey);
                Files.write(newFilePath, fileContent.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new FileOperationException("Failed to create or modify files", e);
        }
    }

    /**
     * 克隆仓库并加载配置
     * 克隆模板仓库并加载生成配置文件
     * <p>
     * 步骤：
     * 1. 克隆Git仓库
     * 2. 加载配置文件
     *
     * @param backendGitPath      Git仓库路径
     * @param backendTemplatePath 模板项目路径
     * @return 模板项目信息
     * @throws FileOperationException 如果克隆或加载配置失败
     */
    private TemplateProjectInfo cloneRepoAndLoadConfig(String backendGitPath, String backendTemplatePath) {
        try {
            GitUtils.cloneRepository(backendGitPath, backendTemplatePath);
            return ConfigLoaderUtils.loadConfig(backendTemplatePath + File.separator + "generate-config.json");
        } catch (Exception e) {
            throw new FileOperationException("Failed to clone repository or load config", e);
        }
    }

    /**
     * 构建字段对象
     * 根据字段DTO构建字段BO对象
     * <p>
     * 步骤：
     * 1. 设置基本字段信息
     * 2. 根据字段类型设置数据库列类型
     * 3. 设置字段类型路径
     *
     * @param field 字段DTO
     * @return 字段BO对象
     * @throws FileOperationException 如果字段构建失败
     */
    private static FieldBo buildFields(FieldDto field) {
        if (field == null) {
            throw new FileOperationException("Field cannot be null");
        }

        String type = field.getType();
        Integer length = field.getLength();
        Integer decimalPoint = field.getDecimalPoint();

        FieldBo fieldBo = new FieldBo();
        fieldBo.setFieldName(field.getName());
        fieldBo.setFieldType(type);
        fieldBo.setPrimaryKey(field.isPrimaryKey());
        String columnName = StringConverterUtil.camelToLowerCaseUnderscore(field.getName());
        fieldBo.setColumnName(columnName);
        fieldBo.setComment(field.getComment());
        fieldBo.setFormat(field.getFormat());
        fieldBo.setFieldSupportConfig(FieldSupportConfigBo.of(field.getFieldSupportConfig()));

        switch (type) {
            case "Integer":
                if (length == null || length < 10) {
                    fieldBo.setColumnType("int");
                } else {
                    fieldBo.setColumnType("bigint");
                }
                break;
            case "BigDecimal":
                fieldBo.setColumnType("decimal");
                fieldBo.setFieldTypePath("java.math.BigDecimal");
                fieldBo.setColumnLength(length);
                fieldBo.setColumnDecimalPoint(decimalPoint);
                break;
            case "String":
                if (length == null || (length < 128 && length != -1)) {
                    fieldBo.setColumnType("varchar");
                    fieldBo.setColumnLength(length != null ? length : 255);
                } else {
                    fieldBo.setColumnType("longtext");
                }
                break;
            case "Date":
                fieldBo.setColumnType("datetime");
                fieldBo.setFieldTypePath("java.util.Date");
                break;
            case "Boolean":
                fieldBo.setColumnType("tinyint");
                break;
            default:
                fieldBo.setFieldType("String");
                fieldBo.setColumnType("varchar");
                fieldBo.setColumnLength(length);
                break;
        }
        return fieldBo;
    }

    /**
     * 构建生成项目对象
     * 根据生成项目DTO构建生成项目BO对象
     * <p>
     * 步骤：
     * 1. 设置基本项目信息
     * 2. 设置后端项目信息
     * 3. 构建业务模块列表
     *
     * @param generateProjectDto 生成项目DTO
     * @return 生成项目BO对象
     * @throws FileOperationException 如果项目构建失败
     */
    public GenerateProjectBo buildGenerateProject(GenerateProjectDto generateProjectDto) {
        if (generateProjectDto == null) {
            throw new FileOperationException("Generate project DTO cannot be null");
        }

        GenerateProjectBo generateProjectBo = new GenerateProjectBo();
        generateProjectBo.setProjectName(generateProjectDto.getProjectName());
        generateProjectBo.setFilePath(generateProjectDto.getFilePath());
        generateProjectBo.setCover(generateProjectDto.isCover());
        if (generateProjectDto.getBackendProject() != null) {
            generateProjectBo.setBackendProject(BackendProjectBo.of(generateProjectDto.getBackendProject()));
        }

        List<BusinessModuleBo> businessModuleBoList = buildBusinessModuleList(generateProjectDto.getBusinessModuleList());
        generateProjectBo.setBusinessModuleList(businessModuleBoList);
        return generateProjectBo;
    }

    /**
     * 构建业务模块列表
     * 根据业务模块DTO列表构建业务模块BO列表
     * <p>
     * 步骤：
     * 1. 设置模块基本信息
     * 2. 设置模块名称相关属性
     * 3. 设置模块配置信息
     * 4. 构建字段列表
     *
     * @param businessModuleList 业务模块DTO列表
     * @return 业务模块BO列表
     * @throws FileOperationException 如果模块列表构建失败
     */
    private static List<BusinessModuleBo> buildBusinessModuleList(List<BusinessModuleDto> businessModuleList) {
        if (businessModuleList == null) {
            throw new FileOperationException("Business module list cannot be null");
        }

        return businessModuleList.stream()
                .map(businessModule -> {
                    BusinessModuleBo businessModuleBo = new BusinessModuleBo();
                    String moduleName = businessModule.getModuleName();
                    businessModuleBo.setModuleName(moduleName);
                    String firstCharLowerCase = Character.toLowerCase(moduleName.charAt(0)) + moduleName.substring(1);
                    businessModuleBo.setModuleNameFirstLetterLower(firstCharLowerCase);
                    String tableName = StringConverterUtil.camelToLowerCaseUnderscore(businessModule.getModuleName());
                    businessModuleBo.setTableName(tableName);
                    businessModuleBo.setComment(businessModule.getComment());
                    businessModuleBo.setAuthentication(businessModule.isAuthentication());

                    businessModuleBo.setSwaggerConfig(SwaggerConfigBo.of(businessModule.getSwaggerConfig()));
                    businessModuleBo.setBusinessSupportConfig(BusinessSupportConfigBo.of(businessModule.getBusinessSupportConfig()));

                    List<FieldDto> fieldList = businessModule.getFieldList();
                    List<FieldBo> fieldBoList = fieldList.stream()
                            .map(GenerateProjectUtils::buildFields)
                            .collect(Collectors.toList());
                    businessModuleBo.setFieldList(fieldBoList);
                    return businessModuleBo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 写入SQL脚本
     * 生成数据库表创建脚本并写入文件
     * <p>
     * 步骤：
     * 1. 读取模板SQL脚本
     * 2. 生成业务模块SQL脚本
     * 3. 合并SQL脚本
     * 4. 写入文件
     *
     * @param templateProjectInfo 模板项目信息
     * @param generateProject     生成项目信息
     * @throws FileOperationException 如果SQL脚本写入失败
     */
    private void writeSqlScript(TemplateProjectInfo templateProjectInfo, GenerateProjectBo generateProject) {
        try {
            String databaseSqlScript = templateProjectInfo.getDatabaseSqlScript();
            List<BusinessModuleBo> businessModules = generateProject.getBusinessModuleList();
            BackendProjectBo backendProject = generateProject.getBackendProject();

            String sqlScriptPath = generateProject.getFilePath() + File.separator + generateProject.getProjectName() + File.separator + backendProject.getName() + File.separator + "src\\main\\resources" + File.separator + databaseSqlScript;

            String templateSqlScript = Files.readString(new File(sqlScriptPath).toPath(), StandardCharsets.UTF_8);
            String sqlScript = GenerateSqlScriptUtils.generateSqlScript(generateProject);
            templateSqlScript += "\n" + sqlScript;
            Files.write(Path.of(sqlScriptPath), templateSqlScript.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new FileOperationException("Failed to write SQL script", e);
        }
    }

    /**
     * 生成项目
     * 根据生成项目DTO生成完整的项目
     * <p>
     * 步骤：
     * 1. 构建生成项目对象
     * 2. 克隆模板仓库
     * 3. 修改项目文件
     * 4. 生成SQL脚本
     * 5. 生成代码文件
     *
     * @param generateProject 生成项目DTO
     * @throws FileOperationException 如果项目生成失败
     */
    public void generate(GenerateProjectDto generateProject) {
        try {
            GenerateProjectBo generateProjectBo = buildGenerateProject(generateProject);

            boolean cover = generateProjectBo.isCover();
            List<String> ignoreNames = Arrays.asList(".git", "generate-config.json");

            // generate backend project
            if (generateProjectBo.getBackendProject() != null) {
                BackendProjectBo backendProject = generateProjectBo.getBackendProject();
                String backendTemplateName = backendProject.getBackendTemplateName();

                VelocityGroupEntity velocityGroupEntity = velocityGroupMapper.selectOneWithRelationsByQuery(
                        QueryChain.of(velocityGroupMapper)
                                .select(velocity_group.all_columns)
                                .from(velocity_group)
                                .where(velocity_group.group_name.eq(backendTemplateName))
                );
                String backendGitPath = velocityGroupEntity.getGitRepositoryPath();
                String generateFilePath = generateProjectBo.getFilePath() + File.separator + generateProjectBo.getProjectName();

                String backendTemplatePath = generateFilePath + File.separator + backendTemplateName;

                TemplateProjectInfo templateProjectInfo = cloneRepoAndLoadConfig(backendGitPath, backendTemplatePath);
                Map<String, File> templateFileMap = new HashMap<>();

                modifyFolder(templateFileMap, new File(backendTemplatePath), new HashSet<>(ignoreNames));

                createAndModifyFiles(templateFileMap, templateProjectInfo, generateProjectBo, cover);

                deleteDirectory(Paths.get(backendTemplatePath));

                writeSqlScript(templateProjectInfo, generateProjectBo);

                codeGeneratorUtils.generate(
                        velocityGroupEntity,
                        generateFilePath + File.separator + backendProject.getName(),
                        generateProjectBo
                );
            }
        } catch (Exception e) {
            throw new FileOperationException("Failed to generate project", e);
        }
    }

}
