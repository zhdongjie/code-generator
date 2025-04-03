package com.original.generator.core.utils;

import com.original.generator.core.domain.bo.*;
import com.original.generator.core.domain.dto.BusinessModuleDto;
import com.original.generator.core.domain.dto.FieldDto;
import com.original.generator.core.domain.dto.GenerateProjectDto;
import com.original.generator.core.domain.info.TemplateProjectInfo;
import com.original.generator.core.exception.FileOperationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GenerateProjectUtils {
    private final JavaClassGeneratorUtils javaClassGeneratorUtils;
    private final VelocityTemplateUtils velocityTemplateUtils;
    private final GitUtils gitUtils;
    private final ConfigLoaderUtils configLoaderUtils;
    private final GenerateSqlScriptUtils generateSqlScriptUtils;

    public GenerateProjectUtils(
            JavaClassGeneratorUtils javaClassGeneratorUtils,
            VelocityTemplateUtils velocityTemplateUtils,
            GitUtils gitUtils,
            ConfigLoaderUtils configLoaderUtils,
            GenerateSqlScriptUtils generateSqlScriptUtils) {
        this.javaClassGeneratorUtils = javaClassGeneratorUtils;
        this.velocityTemplateUtils = velocityTemplateUtils;
        this.gitUtils = gitUtils;
        this.configLoaderUtils = configLoaderUtils;
        this.generateSqlScriptUtils = generateSqlScriptUtils;
    }

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

    private TemplateProjectInfo cloneRepoAndLoadConfig(String backendGitPath, String backendTemplatePath) {
        try {
            gitUtils.cloneRepository(backendGitPath, backendTemplatePath);
            return configLoaderUtils.loadConfig(backendTemplatePath + File.separator + "generate-config.json");
        } catch (Exception e) {
            throw new FileOperationException("Failed to clone repository or load config", e);
        }
    }

    private FieldBo buildFields(FieldDto field) {
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

    public GenerateProjectBo buildGenerateProject(GenerateProjectDto generateProjectDto) {
        if (generateProjectDto == null) {
            throw new FileOperationException("Generate project DTO cannot be null");
        }

        GenerateProjectBo generateProjectBo = new GenerateProjectBo();
        generateProjectBo.setProjectName(generateProjectDto.getProjectName());
        generateProjectBo.setFilePath(generateProjectDto.getFilePath());
        generateProjectBo.setCover(generateProjectDto.isCover());
        generateProjectBo.setBackendProject(BackendProjectBo.of(generateProjectDto.getBackendProject()));
        List<BusinessModuleBo> businessModuleBoList = buildBusinessModuleList(generateProjectDto.getBusinessModuleList());
        generateProjectBo.setBusinessModuleList(businessModuleBoList);
        return generateProjectBo;
    }

    private List<BusinessModuleBo> buildBusinessModuleList(List<BusinessModuleDto> businessModuleList) {
        if (businessModuleList == null) {
            throw new FileOperationException("Business module list cannot be null");
        }

        return businessModuleList.stream()
                .map(businessModule -> {
                    BusinessModuleBo businessModuleBo = new BusinessModuleBo();
                    businessModuleBo.setModuleName(businessModule.getModuleName());
                    businessModuleBo.setFieldList(businessModule.getFieldList().stream()
                            .map(this::buildFields)
                            .collect(Collectors.toList()));
                    return businessModuleBo;
                })
                .collect(Collectors.toList());
    }

    private void writeSqlScript(TemplateProjectInfo templateProjectInfo, GenerateProjectBo generateProject) {
        try {
            String databaseSqlScript = templateProjectInfo.getDatabaseSqlScript();
            List<BusinessModuleBo> businessModules = generateProject.getBusinessModuleList();
            BackendProjectBo backendProject = generateProject.getBackendProject();

            String sqlScriptPath = generateProject.getFilePath() + File.separator + generateProject.getProjectName() + File.separator + backendProject.getName() + File.separator + "src\\main\\resources" + File.separator + databaseSqlScript;

            String templateSqlScript = Files.readString(new File(sqlScriptPath).toPath(), StandardCharsets.UTF_8);
            String sqlScript = generateSqlScriptUtils.generateSqlScript(generateProject);
            templateSqlScript += "\n" + sqlScript;
            Files.write(Path.of(sqlScriptPath), templateSqlScript.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new FileOperationException("Failed to write SQL script", e);
        }
    }

    public void generate(GenerateProjectDto generateProject) {
        try {
            GenerateProjectBo generateProjectBo = buildGenerateProject(generateProject);
            BackendProjectBo backendProject = generateProjectBo.getBackendProject();
            String backendGitPath = backendProject.getBackendGitPath();
            String generateFilePath = generateProjectBo.getFilePath() + File.separator + generateProjectBo.getProjectName();
            String backendTemplateName = backendProject.getBackendTemplateName();

            String backendTemplatePath = generateFilePath + File.separator + backendTemplateName;
            boolean cover = generateProjectBo.isCover();

            TemplateProjectInfo templateProjectInfo = cloneRepoAndLoadConfig(backendGitPath, backendTemplatePath);

            List<String> ignoreNames = Arrays.asList(".git", "generate-config.json");
            Map<String, File> templateFileMap = new HashMap<>();

            modifyFolder(templateFileMap, new File(backendTemplatePath), new HashSet<>(ignoreNames));
            createAndModifyFiles(templateFileMap, templateProjectInfo, generateProjectBo, cover);
            deleteDirectory(Paths.get(backendTemplatePath));
            writeSqlScript(templateProjectInfo, generateProjectBo);

            String generateTemplateGroup = "mybatis-plus";
            javaClassGeneratorUtils.generate(generateTemplateGroup, generateFilePath, generateProjectBo);
        } catch (Exception e) {
            throw new FileOperationException("Failed to generate project", e);
        }
    }

}
