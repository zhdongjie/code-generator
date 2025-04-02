package com.original.generator.core.utils;

import com.original.generator.core.domain.bo.*;
import com.original.generator.core.domain.dto.*;
import com.original.generator.core.domain.info.TemplateProjectInfo;
import org.apache.commons.lang3.StringUtils;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GenerateProjectUtils {

    private static void modifyFolder(Map<String, File> map, File folder, Set<String> ignoreNames) throws IOException {
        // 确保文件夹存在且是一个文件夹
        if (folder.exists() && folder.isDirectory()) {
            // 获取文件夹内的所有文件和文件夹
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
                        // 递归处理子文件夹
                        modifyFolder(map, file, ignoreNames);
                    }
                }
            }
        }
    }

    // 处理文件内容的替换
    private static String replaceFileContent(String fileContent, TemplateProjectInfo templateProjectInfo, GenerateProjectBo generateProject, String newKey) {
        BackendProjectBo backendProject = generateProject.getBackendProject();

        fileContent = fileContent.replace(templateProjectInfo.getPackagePath(), backendProject.getPackagePath());
        if (newKey.contains("pom.xml")) {
            fileContent = fileContent
                    .replace("<groupId>" + templateProjectInfo.getGroupId() + "</groupId>", "<groupId>" + backendProject.getGroupId() + "</groupId>")
                    .replace("<artifactId>" + templateProjectInfo.getArtifactId() + "</artifactId>", "<artifactId>" + backendProject.getArtifactId() + "</artifactId>")
                    .replace("<version>" + templateProjectInfo.getVersion() + "</version>", "<version>" + backendProject.getVersion() + "</version>")
                    .replace("<description>" + templateProjectInfo.getDescription() + "</description>", "<description>" + backendProject.getDescription() + "</description>")
            ;
        }

        if (newKey.contains("application.yml")) {
            fileContent = fileContent
                    .replace(templateProjectInfo.getPort(), backendProject.getPort())
                    .replace(templateProjectInfo.getDatabaseServer(), backendProject.getDatabaseServer())
                    .replace(templateProjectInfo.getDatabasePort(), backendProject.getDatabasePort())
                    .replace(templateProjectInfo.getDatabase(), backendProject.getDatabase())
                    .replace(templateProjectInfo.getDatabaseUser(), backendProject.getDatabaseUser())
                    .replace(templateProjectInfo.getDatabasePassword(), backendProject.getDatabasePassword())
            ;
        }

        if (StringUtils.isNotBlank(templateProjectInfo.getAuthor())) {
            fileContent = fileContent.replace("@author " + templateProjectInfo.getAuthor(), "@author " + backendProject.getAuthor());
        }

        return fileContent;
    }

    // 递归删除文件夹及其内容
    private static void deleteDirectory(Path path) throws IOException {
        // 使用 Files.walkFileTree 遍历文件夹
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 移除只读属性
                File currentFile = file.toFile();
                if (!currentFile.canWrite()) {
                    currentFile.setWritable(true);
                }
                // 删除文件
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    // 移除只读属性
                    File currentDir = dir.toFile();
                    if (!currentDir.canWrite()) {
                        currentDir.setWritable(true);
                    }
                    // 删除空目录
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    // 如果发生异常，抛出错误
                    throw exc;
                }
            }
        });
    }

    // 创建并处理文件（包括文本替换）
    private static void createAndModifyFiles(Map<String, File> map, TemplateProjectInfo templateProjectInfo, GenerateProjectBo generateProject, boolean cover) throws IOException {
        BackendProjectBo backendProject = generateProject.getBackendProject();
        for (Map.Entry<String, File> entry : map.entrySet()) {
            String newKey = entry.getKey()
                    .replace(templateProjectInfo.getName(), backendProject.getName())
                    .replace(templateProjectInfo.getPackagePath().replace(".", File.separator), backendProject.getPackagePath().replace(".", File.separator));

            // 创建新目录
            Path newFilePath = Paths.get(newKey);
            Path newDirPath = newFilePath.getParent();

            if (newDirPath != null) {
                // 创建目录
                Files.createDirectories(newDirPath);
            }

            // 检查文件是否已存在，且是否需要覆盖
            if (Files.exists(newFilePath) && !cover) {
                continue;
            }

            // 读取文件内容并进行文本替换
            String fileContent = Files.readString(entry.getValue().toPath(), StandardCharsets.UTF_8);
            fileContent = replaceFileContent(fileContent, templateProjectInfo, generateProject, newKey);

            // 将修改后的内容写入到目标文件
            Files.write(newFilePath, fileContent.getBytes(StandardCharsets.UTF_8));
        }
    }

    // 克隆 Git 仓库并加载配置
    private static TemplateProjectInfo cloneRepoAndLoadConfig(String backendGitPath, String backendTemplatePath) throws IOException {

        GitUtils.cloneRepository(backendGitPath, backendTemplatePath);

        return ConfigLoaderUtils.loadConfig(backendTemplatePath + File.separator + "generate-config.json");
    }

    // 构建字段、数据库映射关系
    private static FieldBo buildFields(FieldDto field) {
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

    public static GenerateProjectBo buildGenerateProject(GenerateProjectDto generateProjectDto) {
        GenerateProjectBo generateProjectBo = new GenerateProjectBo();
        generateProjectBo.setProjectName(generateProjectDto.getProjectName());
        generateProjectBo.setFilePath(generateProjectDto.getFilePath());
        generateProjectBo.setCover(generateProjectDto.isCover());
        generateProjectBo.setBackendProject(BackendProjectBo.of(generateProjectDto.getBackendProject()));
        List<BusinessModuleBo> businessModuleBoList = buildBusinessModuleList(generateProjectDto.getBusinessModuleList());
        generateProjectBo.setBusinessModuleList(businessModuleBoList);
        return generateProjectBo;
    }

    // 构建模块列表
    private static List<BusinessModuleBo> buildBusinessModuleList(List<BusinessModuleDto> businessModuleList) {
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

    // 生成数据库脚本
    private static void writeSqlScript(TemplateProjectInfo templateProjectInfo, GenerateProjectBo generateProject) throws IOException {
        String databaseSqlScript = templateProjectInfo.getDatabaseSqlScript();
        List<BusinessModuleBo> businessModules = generateProject.getBusinessModuleList();
        BackendProjectBo backendProject = generateProject.getBackendProject();

        String sqlScriptPath = generateProject.getFilePath() + File.separator + generateProject.getProjectName() + File.separator + backendProject.getName() + File.separator + "src\\main\\resources" + File.separator + databaseSqlScript;

        String templateSqlScript = Files.readString(new File(sqlScriptPath).toPath(), StandardCharsets.UTF_8);

        String sqlScript = GenerateSqlScriptUtils.generateSqlScript(generateProject);
        templateSqlScript += "\n" + sqlScript;

        Files.write(Path.of(sqlScriptPath), templateSqlScript.getBytes(StandardCharsets.UTF_8));
    }

    // generate
    private static void generate(GenerateProjectDto generateProject) throws IOException {
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

        // 修改文件内容
        modifyFolder(templateFileMap, new File(backendTemplatePath), new HashSet<>(ignoreNames));

        // 创建并修改文件
        createAndModifyFiles(templateFileMap, templateProjectInfo, generateProjectBo, cover);

        // 删除后台模板
        deleteDirectory(Paths.get(backendTemplatePath));

        writeSqlScript(templateProjectInfo, generateProjectBo);

        String generateTemplateGroup = "mybatis-plus";
        String outputPath = generateFilePath;
        String packageName = "";
        // 根据vm模板生成Java代码
        JavaClassGeneratorUtils.generate(generateTemplateGroup, outputPath, packageName, generateProjectBo);
    }

    public static void main(String[] args) throws IOException {
        GenerateProjectDto generateProject = initGenerateProject();
        // 调用方法处理
        generate(generateProject);
    }

    public static GenerateProjectDto initGenerateProject() {
        // 创建项目生成配置
        GenerateProjectDto generateProject = new GenerateProjectDto();
        BackendProjectDto backendProject = new BackendProjectDto();
        backendProject.setName("new-project");
        backendProject.setGroupId("cn.zhang");
        backendProject.setArtifactId("projcet");
        backendProject.setVersion("0.0.1");
        backendProject.setDescription("Demo 0.0.1");
        backendProject.setAuthor("zhang");
        backendProject.setPackagePath("cn.zhang.project.test");
        backendProject.setPort("9090");
        backendProject.setDatabaseServer("127.0.0.1");
        backendProject.setDatabasePort("3306");
        backendProject.setDatabase("demo-database");
        backendProject.setDatabaseUser("root");
        backendProject.setDatabasePassword("root");
        backendProject.setBackendGitPath("https://gitee.com/zhang-dong-jie_admin/spring3-jwt-mybatisplus-template.git");
        backendProject.setBackendTemplateName("spring3-jwt-mybatisplus-template");

        generateProject.setFilePath("E:\\Project\\Generate");
        generateProject.setProjectName("Demo");
        generateProject.setCover(true);
        generateProject.setBackendProject(backendProject);
        // 写入Sql脚本
        List<BusinessModuleDto> businessModules = initBusinessModules(2, "User");
        generateProject.setBusinessModuleList(businessModules);

        return generateProject;
    }

    private static List<BusinessModuleDto> initBusinessModules(int count, String templateModuleName) {
        return IntStream.range(0, count)
                .mapToObj(i -> initBusinessModule(templateModuleName + i))
                .collect(Collectors.toList());
    }

    private static BusinessModuleDto initBusinessModule(String moduleName) {
        BusinessModuleDto businessModule = new BusinessModuleDto();
        businessModule.setModuleName(org.springframework.util.StringUtils.hasText(moduleName) ? moduleName : "User");
        businessModule.setComment("用户");
        businessModule.setAuthentication(true);
        SwaggerConfigDto swaggerConfig = new SwaggerConfigDto();
        swaggerConfig.setEnable(true);
        swaggerConfig.setDescription("用户管理接口");
        businessModule.setSwaggerConfig(swaggerConfig);

        BusinessSupportConfigDto businessSupportConfig = new BusinessSupportConfigDto();
        businessSupportConfig.setSupportExport(true);
        businessSupportConfig.setSupportImport(true);
        businessSupportConfig.setSupportDeleteBatch(true);
        businessSupportConfig.setSupportLog(true);
        businessModule.setBusinessSupportConfig(businessSupportConfig);

        List<FieldDto> fields = initFields();
        businessModule.setFieldList(fields);

        return businessModule;
    }

    private static List<FieldDto> initFields() {
        // 姓名(name) varchar(20) minLength=4 maxLength=16 pattern="^[a-zA-Z0-9_-]{4,16}$"
        FieldDto nameField = new FieldDto();
        nameField.setName("name");
        nameField.setType("String");
        nameField.setLength(16);
        nameField.setComment("姓名");

        FieldRuleDto nameRule = new FieldRuleDto();
        nameRule.setRequired(true);
        nameRule.setPattern("^[a-zA-Z0-9_-]{4,16}$");
        nameField.setFieldRule(nameRule);

        FieldSupportConfigDto nameFieldSupport = new FieldSupportConfigDto();
        nameFieldSupport.setSupportSearch(true);
        nameFieldSupport.setSupportMaintenance(true);
        nameFieldSupport.setSupportView(true);
        nameFieldSupport.setSupportImport(true);
        nameFieldSupport.setSupportExport(true);
        nameField.setFieldSupportConfig(nameFieldSupport);

        // 备注(remark) longtext
        FieldDto remarkField = new FieldDto();
        remarkField.setName("remark");
        remarkField.setType("String");
        // 长度为-1 代表无限制
        remarkField.setLength(-1);
        remarkField.setComment("备注");

        FieldRuleDto remarkRule = new FieldRuleDto();
        remarkRule.setRequired(true);
        remarkField.setFieldRule(remarkRule);

        FieldSupportConfigDto remarkFieldSupport = new FieldSupportConfigDto();
        remarkFieldSupport.setSupportMaintenance(true);
        remarkFieldSupport.setSupportView(true);
        remarkFieldSupport.setSupportImport(true);
        remarkFieldSupport.setSupportExport(true);
        remarkField.setFieldSupportConfig(remarkFieldSupport);

        // 创建时间(createDate) datetime valueFormat="yyyy-MM-dd HH:mm:ss" dateFormat="datetime"
        FieldDto createDateField = new FieldDto();
        createDateField.setName("createDate");
        createDateField.setType("Date");
        createDateField.setComment("创建时间");
        createDateField.setFormat("yyyy-MM-dd HH:mm:ss");
        FieldRuleDto createDateRule = new FieldRuleDto();
        createDateRule.setRequired(true);
        createDateField.setFieldRule(createDateRule);

        FieldSupportConfigDto createDateFieldSupport = new FieldSupportConfigDto();
        createDateFieldSupport.setSupportView(true);
        createDateFieldSupport.setSupportImport(true);
        createDateFieldSupport.setSupportExport(true);
        createDateField.setFieldSupportConfig(createDateFieldSupport);

        // 删除标志(deleteFlag) tinyint(1) defaultValue=0
        FieldDto deleteFlagField = new FieldDto();
        deleteFlagField.setName("deleteFlag");
        deleteFlagField.setType("Boolean");
        deleteFlagField.setComment("删除标志");

        // 金额(money) decimal(18,5) minValue=0 maxLength=18 decimalPoint=5
        FieldDto moneyField = new FieldDto();
        moneyField.setName("money");
        moneyField.setType("BigDecimal");
        moneyField.setLength(18);
        moneyField.setDecimalPoint(5);
        moneyField.setComment("金额");
        FieldRuleDto moneyRule = new FieldRuleDto();
        moneyRule.setMinValue(BigDecimal.ZERO);
        moneyField.setFieldRule(moneyRule);

        FieldSupportConfigDto moneyFieldSupport = new FieldSupportConfigDto();
        moneyFieldSupport.setSupportSearch(true);
        moneyFieldSupport.setSupportSearchRange(true);
        moneyFieldSupport.setSupportMaintenance(true);
        moneyFieldSupport.setSupportView(true);
        moneyFieldSupport.setSupportImport(true);
        moneyFieldSupport.setSupportExport(true);
        moneyField.setFieldSupportConfig(moneyFieldSupport);

        // 年龄(age) int minValue=0 maxValue=200
        FieldDto ageField = new FieldDto();
        ageField.setName("age");
        ageField.setType("Integer");
        ageField.setLength(3);
        ageField.setComment("年龄");

        FieldRuleDto ageRule = new FieldRuleDto();
        ageRule.setMinValue(BigDecimal.ZERO);
        ageRule.setMaxValue(BigDecimal.valueOf(200));
        ageField.setFieldRule(ageRule);

        FieldSupportConfigDto ageFieldSupport = new FieldSupportConfigDto();
        ageFieldSupport.setSupportSearch(true);
        ageFieldSupport.setSupportSearchRange(true);
        ageFieldSupport.setSupportMaintenance(true);
        ageFieldSupport.setSupportView(true);
        ageFieldSupport.setSupportImport(true);
        ageFieldSupport.setSupportExport(true);
        ageField.setFieldSupportConfig(ageFieldSupport);

        // id(主键) int length=20 minValue=0
        FieldDto idField = new FieldDto();
        idField.setName("id");
        idField.setPrimaryKey(true);
        idField.setType("Integer");
        idField.setLength(20);
        idField.setComment("主键");
        FieldRuleDto idRule = new FieldRuleDto();
        idRule.setRequired(true);
        idRule.setMinValue(BigDecimal.ZERO);
        idField.setFieldRule(idRule);

        FieldSupportConfigDto idFieldSupport = new FieldSupportConfigDto();
        idFieldSupport.setSupportImport(true);
        idFieldSupport.setSupportExport(true);
        idField.setFieldSupportConfig(idFieldSupport);

        return Arrays.asList(
                nameField,
                remarkField,
                createDateField,
                deleteFlagField,
                moneyField,
                ageField,
                idField
        );
    }

}
