package com.original.generator.core.domain.bo;

import com.original.generator.core.domain.dto.BackendProjectDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackendProjectBo {

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

    private BackendProjectBo(BackendProjectDto backendProject) {
        this.name = backendProject.getName();
        this.groupId = backendProject.getGroupId();
        this.artifactId = backendProject.getArtifactId();
        this.version = backendProject.getVersion();
        this.description = backendProject.getDescription();
        this.author = backendProject.getAuthor();
        this.packagePath = backendProject.getPackagePath();
        this.port = backendProject.getPort();
        this.databaseServer = backendProject.getDatabaseServer();
        this.databasePort = backendProject.getDatabasePort();
        this.database = backendProject.getDatabase();
        this.databaseUser = backendProject.getDatabaseUser();
        this.databasePassword = backendProject.getDatabasePassword();
        this.backendGitPath = backendProject.getBackendGitPath();
        this.backendTemplateName = backendProject.getBackendTemplateName();
    }

    public static BackendProjectBo of(BackendProjectDto backendProject) {
        return ObjectUtils.isEmpty(backendProject)
                ? new BackendProjectBo()
                : new BackendProjectBo(backendProject);
    }

}
