package com.original.generator.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.original.generator.core.domain.dto.*;
import com.original.generator.core.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectGeneratorTest {

    @Autowired
    private GenerateProjectUtils generateProjectUtils;

    @Autowired
    private ConfigLoaderUtils configLoaderUtils;

    @Autowired
    private GitUtils gitUtils;

    @Autowired
    private GenerateSqlScriptUtils generateSqlScriptUtils;

    @Autowired
    private JavaClassGeneratorUtils javaClassGeneratorUtils;

    @Autowired
    private VelocityTemplateUtils velocityTemplateUtils;

    @Autowired
    private LocalValidatorFactoryBean validator;


    @Autowired
    private MessageSource messageSource;

    Path tempDir = Path.of("target/test-project");

    private GenerateProjectDto testProject;

    @BeforeEach
    void setUp() throws IOException {

        // 加载测试配置
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File("src/main/resources/demo-project.json");
        DemoProjectConfig demoConfig = objectMapper.readValue(configFile, DemoProjectConfig.class);

        // 创建测试项目配置
        testProject = new GenerateProjectDto();
        testProject.setProjectName(demoConfig.getProjectName());
        testProject.setFilePath(tempDir.toString());
        testProject.setCover(true);
        testProject.setBackendProject(demoConfig.getBackendProject());
        testProject.setBusinessModuleList(demoConfig.getModules());
    }

    @Test
    void testProjectGeneration() {
        // 执行项目生成
        assertDoesNotThrow(() -> generateProjectUtils.generate(testProject));

        // 验证生成的文件结构
        Path projectPath = tempDir.resolve(testProject.getProjectName());
        assertTrue(projectPath.toFile().exists());
        assertTrue(projectPath.resolve(testProject.getBackendProject().getName()).toFile().exists());
        assertTrue(projectPath.resolve(testProject.getBackendProject().getName() + "/src/main/java").toFile().exists());
        assertTrue(projectPath.resolve(testProject.getBackendProject().getName() + "/src/main/resources").toFile().exists());

        // 验证生成的SQL脚本
        Path sqlScriptPath = projectPath.resolve(testProject.getBackendProject().getName() + "/src/main/resources/sql/init.sql");
        assertTrue(sqlScriptPath.toFile().exists());
    }

    @Test
    void testFieldValidation() {
        // 测试字段验证
        BusinessModuleDto module = testProject.getBusinessModuleList().get(0);

        // 验证必填字段
        FieldDto nameField = module.getFieldList().stream()
                .filter(f -> "name".equals(f.getName()))
                .findFirst()
                .orElseThrow();
        assertTrue(nameField.getFieldRule().isRequired());
        assertEquals("^[a-zA-Z0-9_-]{4,16}$", nameField.getFieldRule().getPattern());

        // 验证数值范围
        FieldDto ageField = module.getFieldList().stream()
                .filter(f -> "age".equals(f.getName()))
                .findFirst()
                .orElseThrow();
        assertEquals(0, ageField.getFieldRule().getMinValue().intValue());
        assertEquals(200, ageField.getFieldRule().getMaxValue().intValue());

        // 验证金额字段
        FieldDto moneyField = module.getFieldList().stream()
                .filter(f -> "money".equals(f.getName()))
                .findFirst()
                .orElseThrow();
        assertEquals(18, moneyField.getLength());
        assertEquals(5, moneyField.getDecimalPoint());
        assertEquals(0, moneyField.getFieldRule().getMinValue().intValue());
    }

    @Test
    void testModuleConfiguration() {
        // 测试模块配置
        BusinessModuleDto module = testProject.getBusinessModuleList().get(0);

        // 验证模块基本信息
        assertEquals("User", module.getModuleName());
        assertEquals("用户", module.getComment());
        assertTrue(module.isAuthentication());

        // 验证Swagger配置
        SwaggerConfigDto swaggerConfig = module.getSwaggerConfig();
        assertTrue(swaggerConfig.isEnabled());
        assertEquals("User API", swaggerConfig.getTitle());
        assertEquals("用户管理接口", swaggerConfig.getDescription());

        // 验证业务支持配置
        BusinessSupportConfigDto businessConfig = module.getBusinessSupportConfig();
        assertTrue(businessConfig.isSupportExport());
        assertTrue(businessConfig.isSupportImport());
        assertTrue(businessConfig.isSupportDeleteBatch());
        assertTrue(businessConfig.isSupportLog());
    }

    @Test
    void testBackendConfiguration() {
        // 测试后端配置
        BackendProjectDto backendProject = testProject.getBackendProject();

        // 验证基本信息
        assertEquals("new-project", backendProject.getName());
        assertEquals("cn.zhang", backendProject.getGroupId());
        assertEquals("new-project", backendProject.getArtifactId());
        assertEquals("0.0.1", backendProject.getVersion());
        assertEquals("Demo 0.0.1", backendProject.getDescription());
        assertEquals("zhang", backendProject.getAuthor());
        assertEquals("cn.zhang.project.test", backendProject.getPackagePath());

        // 验证数据库配置
        assertEquals("127.0.0.1", backendProject.getDatabaseServer());
        assertEquals("3306", backendProject.getDatabasePort());
        assertEquals("demo-database", backendProject.getDatabase());
        assertEquals("root", backendProject.getDatabaseUser());
        assertEquals("root", backendProject.getDatabasePassword());

        // 验证Git配置
        assertEquals("https://gitee.com/zhang-dong-jie_admin/spring3-jwt-mybatisplus-template.git",
                backendProject.getBackendGitPath());
        assertEquals("spring3-jwt-mybatisplus-template", backendProject.getBackendTemplateName());
    }

    @Test
    void testInvalidConfigurations() {
        // 测试无效的项目名称
        testProject.setProjectName(null);
        Set<ConstraintViolation<GenerateProjectDto>> violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("项目名称不能为空")));
        testProject.setProjectName("Demo");

        // 测试无效的文件路径
        testProject.setFilePath(null);
        violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("生成项目路径不能为空")));
        testProject.setFilePath(tempDir.toString());

        // 测试无效的后端项目名称
        testProject.getBackendProject().setName(null);
        violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("后端项目名称不能为空")));
        testProject.getBackendProject().setName("new-project");

        // 测试无效的包路径
        testProject.getBackendProject().setPackagePath(null);
        violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("包路径不能为空")));
        testProject.getBackendProject().setPackagePath("cn.zhang.project.test");

        // 测试无效的模块名称
        testProject.getBusinessModuleList().get(0).setModuleName(null);
        violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("业务模块名称不能为空")));
        testProject.getBusinessModuleList().get(0).setModuleName("User");

        // 测试无效的字段列表
        testProject.getBusinessModuleList().get(0).setFieldList(null);
        violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("字段列表不能为空")));
    }

    @Test
    void testFieldValidationRules() {
        // 测试字段验证规则
        FieldDto field = new FieldDto();

        // 测试空字段名
        Set<ConstraintViolation<FieldDto>> violations = validator.validate(field);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("字段名不能为空")));

        // 测试无效的字段名格式
        field.setName("invalid-field-name!");
        violations = validator.validate(field);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("字段名只能包含字母、数字和下划线")));

        // 测试无效的数据类型
        field.setName("validField");
        field.setType("InvalidType");
        violations = validator.validate(field);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("数据类型必须是String、Integer、Long、BigDecimal、Date或Boolean")));

        // 测试无效的长度
        field.setType("String");
        field.setLength(-1);
        violations = validator.validate(field);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("长度不能小于0")));
    }

    @Test
    void testBackendProjectValidation() {
        // 测试后端项目验证
        BackendProjectDto backendProject = new BackendProjectDto();

        // 测试空项目名
        Set<ConstraintViolation<BackendProjectDto>> violations = validator.validate(backendProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("后端项目名称不能为空")));

        // 测试无效的项目名格式
        backendProject.setName("invalid project name!");
        violations = validator.validate(backendProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("后端项目名称只能包含字母、数字、下划线和连字符")));

        // 测试无效的版本号格式
        backendProject.setName("valid-project");
        backendProject.setVersion("1.0");
        violations = validator.validate(backendProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("版本号格式不正确，应为x.y.z格式")));

        // 测试无效的端口号
        backendProject.setVersion("1.0.0");
        backendProject.setPort("invalid-port");
        violations = validator.validate(backendProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("端口号必须是1-5位数字")));
    }

    // 用于解析demo-project.json的配置类
    private static class DemoProjectConfig {
        private String projectName;
        private String description;
        private List<BusinessModuleDto> modules;
        private BackendProjectDto backendProject;

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<BusinessModuleDto> getModules() {
            return modules;
        }

        public void setModules(List<BusinessModuleDto> modules) {
            this.modules = modules;
        }

        public BackendProjectDto getBackendProject() {
            return backendProject;
        }

        public void setBackendProject(BackendProjectDto backendProject) {
            this.backendProject = backendProject;
        }
    }
}