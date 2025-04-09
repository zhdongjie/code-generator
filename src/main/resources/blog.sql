/*
 Navicat Premium Data Transfer

 Source Server         : Localhost-Docker
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:13306
 Source Schema         : blog

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 03/04/2025 18:09:12
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_velocity_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_velocity_group`;
CREATE TABLE `sys_velocity_group`
(
    `id`                  bigint NOT NULL COMMENT '主键ID',
    `git_repository_path` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Git仓库地址',
    `group_name`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板分类名称',
    `group_type`          varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板分类类别',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_velocity_group
-- ----------------------------
INSERT INTO `sys_velocity_group`
VALUES (1, 'https://gitee.com/zhang-dong-jie_admin/spring3-jwt-mybatisplus-template.git',
        'spring3-jwt-mybatisplus-template', 'BACKEND');
INSERT INTO `sys_velocity_group`
VALUES (2, 'https://gitee.com/zhang-dong-jie/base-authorize-ui.git', 'base-authorize-ui', 'FRONTEND');

-- ----------------------------
-- Table structure for sys_velocity_template
-- ----------------------------
DROP TABLE IF EXISTS `sys_velocity_template`;
CREATE TABLE `sys_velocity_template`
(
    `id`            bigint NOT NULL COMMENT '主键ID',
    `group_id`      bigint NULL DEFAULT NULL COMMENT '模板组Id',
    `template`      longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板内容',
    `update_date`   datetime NULL DEFAULT NULL COMMENT '最后修改时间',
    `save_path`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件保存路径',
    `template_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板名称',
    `template_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板类型',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_velocity_template
-- ----------------------------
INSERT INTO `sys_velocity_template`
VALUES (1, 1,
        'package ${packagePath}.domain.entity;\r\n\r\n    #foreach($import in $importList)\r\n    import $import;\r\n    #end\r\n\r\nimport com.baomidou.mybatisplus.annotation.IdType;\r\nimport com.baomidou.mybatisplus.annotation.TableField;\r\nimport com.baomidou.mybatisplus.annotation.TableId;\r\nimport com.baomidou.mybatisplus.annotation.TableName;\r\nimport lombok.AllArgsConstructor;\r\nimport lombok.Data;\r\nimport lombok.NoArgsConstructor;\r\n\r\n@Data\r\n@AllArgsConstructor\r\n@NoArgsConstructor\r\n@TableName(\"`$module.tableName`\")\r\npublic class ${module.moduleName}Entity {\r\n\r\n    #foreach($field in $module.fieldList)\r\n        #if(${field.comment})\r\n            /**\r\n             * ${field.comment}\r\n             */#end\r\n\r\n        #if($field.primaryKey)\r\n        @TableId(type = IdType.ASSIGN_ID, value = \"`$field.columnName`\")\r\n        #end\r\n        #if(!$field.primaryKey)\r\n        @TableField(value = \"`$field.columnName`\")\r\n        #end\r\n    private $field.fieldType $!{field.fieldName};\r\n\r\n    #end\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/domain/entity', '${moduleName}Entity.java',
        'BACKEND_ENTITY');
INSERT INTO `sys_velocity_template`
VALUES (2, 1,
        'package ${packagePath}.mapper;\r\n\r\nimport org.apache.ibatis.annotations.Mapper;\r\nimport com.baomidou.mybatisplus.core.mapper.BaseMapper;\r\nimport ${packagePath}.domain.entity.${module.moduleName}Entity;\r\n\r\n@Mapper\r\npublic interface ${module.moduleName}Mapper extends BaseMapper<${module.moduleName}Entity> {\r\n\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/mapper', '${moduleName}Mapper.java',
        'BACKEND_MAPPER_JAVA');
INSERT INTO `sys_velocity_template`
VALUES (3, 1,
        '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\r\n<mapper namespace=\"${packagePath}.mapper.${module.moduleName}Mapper\">\r\n\r\n    <resultMap type=\"${packagePath}.domain.entity.${module.moduleName}Entity\" id=\"${module.moduleName}Map\">\r\n#foreach($field in $module.fieldList)\r\n        <result property=\"$field.columnName\" column=\"$!{field.fieldName}\" jdbcType=\"$!{field.columnType}\"/>\r\n#end\r\n    </resultMap>\r\n\r\n</mapper>\r\n',
        '2025-04-03 13:13:09', 'src/main/resources/mapper/${moduleName}', '${moduleName}Mapper.xml',
        'BACKEND_MAPPER_XML');
INSERT INTO `sys_velocity_template`
VALUES (4, 1, 'package ${packagePath}.service;\r\n\r\npublic interface ${module.moduleName}Service {\r\n\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/service', '${moduleName}Service.java',
        'BACKEND_SERVICE');
INSERT INTO `sys_velocity_template`
VALUES (5, 1,
        'package ${packagePath}.service.impl;\r\n\r\nimport ${packagePath}.service.${module.moduleName}Service;\r\nimport ${packagePath}.mapper.${module.moduleName}Mapper;\r\nimport org.springframework.stereotype.Service;\r\nimport org.springframework.transaction.annotation.Transactional;\r\n\r\nimport javax.annotation.Resource;\r\n\r\n@Service\r\npublic class ${module.moduleName}ServiceImpl implements ${module.moduleName}Service {\r\n\r\n    @Resource\r\n    private ${module.moduleName}Mapper ${module.moduleNameFirstLetterLower}Mapper;\r\n\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/service/impl',
        '${moduleName}ServiceImpl.java', 'BACKEND_SERVICE_IMPL');
INSERT INTO `sys_velocity_template`
VALUES (6, 1,
        'package ${packagePath}.controller;\r\n\r\nimport ${packagePath}.service.${module.moduleName}Service;\r\nimport org.springframework.web.bind.annotation.RequestMapping;\r\nimport org.springframework.web.bind.annotation.RestController;\r\n\r\nimport javax.annotation.Resource;\r\n\r\n@RestController\r\n@RequestMapping(\"/system/${module.moduleNameFirstLetterLower}\")\r\npublic class ${module.moduleName}Controller {\r\n\r\n    @Resource\r\n    private ${module.moduleName}Service ${module.moduleNameFirstLetterLower}Service;\r\n\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/controller', '${moduleName}Controller.java',
        'BACKEND_CONTROLLER');
INSERT INTO `sys_velocity_template`
VALUES (7, 1,
        'package ${packagePath}.domain.dto;\r\n\r\n    #foreach($import in $importList)\r\n    import $import;\r\n    #end\r\n\r\nimport lombok.AllArgsConstructor;\r\nimport lombok.Data;\r\nimport lombok.NoArgsConstructor;\r\n\r\n@Data\r\n@AllArgsConstructor\r\n@NoArgsConstructor\r\npublic class Insert${module.moduleName}Dto {\r\n\r\n    #foreach($field in $module.fieldList)\r\n        #if($!{field.fieldSupportConfig.supportMaintenance})\r\n            #if(${field.comment})\r\n                /**\r\n                 * ${field.comment}\r\n                 */#end\r\n\r\n            private $field.fieldType $!{field.fieldName};\r\n\r\n        #end\r\n    #end\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/domain/dto', 'Insert${moduleName}Dto.java',
        'BACKEND_DTO');
INSERT INTO `sys_velocity_template`
VALUES (8, 1,
        'package ${packagePath}.domain.dto;\r\n\r\n#foreach($field in $module.fieldList)\r\n#foreach($import in $importList)\r\n#if($field.fieldSupportConfig.supportSearch)\r\n#if($!import.contains(${field.fieldType}))\r\nimport $import;\r\n#break\r\n#end\r\n#end\r\n#end\r\n#end\r\nimport lombok.AllArgsConstructor;\r\nimport lombok.Data;\r\nimport lombok.NoArgsConstructor;\r\n\r\n@Data\r\n@AllArgsConstructor\r\n@NoArgsConstructor\r\npublic class Select${module.moduleName}Dto {\r\n\r\n    #foreach($field in $module.fieldList)\r\n	#if($!{field.fieldSupportConfig.supportSearch})\r\n	#if($!{field.fieldSupportConfig.supportSearchRange})\r\n	#if($!{field.comment})\r\n	/**\r\n	 * ${field.comment}起始\r\n	 */#end\r\n	\r\n	private ${field.fieldType} ${field.fieldName}Start;\r\n\r\n	#if($!{field.comment})\r\n	/**\r\n	 * ${field.comment}截止\r\n	 */#end\r\n	\r\n	private ${field.fieldType} ${field.fieldName}End;\r\n	#end\r\n	#if(!$!{field.fieldSupportConfig.supportSearchRange})\r\n	#if($!{field.comment})\r\n	/**\r\n	 * ${field.comment}\r\n	 */#end\r\n	\r\n	private ${field.fieldType} ${field.fieldName};\r\n	#end\r\n\r\n	#end\r\n    #end\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/domain/dto', 'Select${moduleName}Dto.java',
        'BACKEND_DTO');
INSERT INTO `sys_velocity_template`
VALUES (9, 1,
        'package ${packagePath}.domain.dto;\r\n\r\n    #foreach($import in $importList)\r\n    import $import;\r\n    #end\r\n\r\nimport lombok.AllArgsConstructor;\r\nimport lombok.Data;\r\nimport lombok.NoArgsConstructor;\r\n\r\n@Data\r\n@AllArgsConstructor\r\n@NoArgsConstructor\r\npublic class Update${module.moduleName}Dto {\r\n\r\n    #foreach($field in $module.fieldList)\r\n        #if($!{field.fieldSupportConfig.supportMaintenance} || ${field.primaryKey})\r\n            #if($!{field.comment})\r\n                /**\r\n                 * ${field.comment}\r\n                 */#end\r\n\r\n            private ${field.fieldType} ${field.fieldName};\r\n\r\n        #end\r\n    #end\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/domain/dto', 'Update${moduleName}Dto.java',
        'BACKEND_DTO');
INSERT INTO `sys_velocity_template`
VALUES (17, 1,
        'package ${packagePath}.domain.vo;\r\n\r\n    #foreach($import in $importList)\r\n    import $import;\r\n    #end\r\n\r\nimport com.fasterxml.jackson.annotation.JsonFormat;\r\nimport lombok.AllArgsConstructor;\r\nimport lombok.Data;\r\nimport lombok.NoArgsConstructor;\r\n\r\n@Data\r\n@AllArgsConstructor\r\n@NoArgsConstructor\r\npublic class ${module.moduleName}Vo {\r\n\r\n    #foreach($field in $module.fieldList)\r\n        #if($!{field.fieldSupportConfig.supportView})\r\n            #if($!{field.comment})\r\n                /**\r\n                 * ${field.comment}\r\n                 */#end\r\n\r\n            #if($!{field.format})\r\n            @JsonFormat(pattern = \"$!{field.format}\")\r\n            #end\r\n        private ${field.fieldType} ${field.fieldName};\r\n\r\n        #end\r\n    #end\r\n}',
        '2025-04-03 13:13:09', 'src/main/java/${packagePath}/${moduleName}/domain/vo', '${moduleName}Vo.java',
        'BACKEND_VO');
INSERT INTO `sys_velocity_template`
VALUES (19, 2, '// API模板内容', '2025-04-03 13:13:09', 'src/api/${moduleName}', '${moduleName}.js', 'FRONTEND_API');
INSERT INTO `sys_velocity_template`
VALUES (20, 2, '<!-- 视图模板内容 -->', '2025-04-03 13:13:09', 'src/views/${moduleName}', 'index.vue',
        'FRONTEND_VIEWS');
INSERT INTO `sys_velocity_template`
VALUES (21, 2, '// 类型定义模板内容', '2025-04-03 13:13:09', 'src/types/${moduleName}', '${moduleName}.types.ts',
        'FRONTEND_TYPES');
INSERT INTO `sys_velocity_template`
VALUES (22, 2, '// 表单配置模板内容', '2025-04-03 13:13:09', 'src/views/${moduleName}/form-config', 'form.config.json',
        'FRONTEND_FORM_CONFIG');

SET
FOREIGN_KEY_CHECKS = 1;
