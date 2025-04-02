package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackendProjectDto {

    /**
     * 后端项目名
     */
    private String name;

    /**
     * 项目组名
     */
    private String groupId;

    /**
     * 模块名
     */
    private String artifactId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 作者
     */
    private String author;

    /**
     * 包路径
     */
    private String packagePath;

    /**
     * 端口号
     */
    private String port;

    /**
     * 数据库服务器
     */
    private String databaseServer;

    /**
     * 数据库端口
     */
    private String databasePort;

    /**
     * 数据库
     */
    private String database;

    /**
     * 数据库用户名
     */
    private String databaseUser;

    /**
     * 数据库密码
     */
    private String databasePassword;

    /**
     * 后端项目模板路径
     */
    private String backendGitPath;

    /**
     * 模板名
     */
    private String backendTemplateName;

}
