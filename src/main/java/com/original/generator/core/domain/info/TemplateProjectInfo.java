package com.original.generator.core.domain.info;

import lombok.Data;

@Data
public class TemplateProjectInfo {

    /**
     * 模板项目信息
     */
    private String name;

    /**
     * 模板项目组名
     */
    private String groupId;

    /**
     * 模板项目模块名
     */
    private String artifactId;

    /**
     * 模板项目版本号
     */
    private String version;

    /**
     * 模板项目描述
     */
    private String description;

    /**
     * 模板项目作者
     */
    private String author;

    /**
     * 模板项目包路径
     */
    private String packagePath;

    /**
     * 模板项目端口号
     */
    private String port;

    /**
     * 模板项目数据库
     */
    private String database;

    /**
     * 模板项目数据库用户名
     */
    private String databaseUser;

    /**
     * 模板项目数据库密码
     */
    private String databasePassword;

    /**
     * 模板项目数据库服务器
     */
    private String databaseServer;

    /**
     * 模板项目数据库端口
     */
    private String databasePort;

    /**
     * 模板项目数据库脚本位置
     */
    private String databaseSqlScript;

}