package com.original.generator.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.original.generator.core.domain.dto.BackendProjectDto;
import com.original.generator.core.domain.dto.BusinessModuleDto;
import com.original.generator.core.domain.dto.GenerateProjectDto;
import com.original.generator.core.utils.GenerateProjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ProjectGeneratorTest {

    private static final Logger logger = LoggerFactory.getLogger(ProjectGeneratorTest.class);

    @Autowired
    private GenerateProjectUtils generateProjectUtils;

    @Autowired
    private LocalValidatorFactoryBean validator;


    Path tempDir = Path.of("target");

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