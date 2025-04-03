package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackendProjectDto {

    /**
     * 后端项目名
     */
    @NotBlank(message = "后端项目名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "后端项目名称只能包含字母、数字、下划线和连字符")
    private String name;

    /**
     * 项目组名
     */
    @NotBlank(message = "项目组名不能为空")
    @Pattern(regexp = "^[a-z0-9.]+$", message = "项目组名只能包含小写字母、数字和点")
    private String groupId;

    /**
     * 模块名
     */
    @NotBlank(message = "模块名不能为空")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "模块名只能包含小写字母、数字和连字符")
    private String artifactId;

    /**
     * 版本号
     */
    @NotBlank(message = "版本号不能为空")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "版本号格式不正确，应为x.y.z格式")
    private String version;

    /**
     * 项目描述
     */
    @Size(max = 500, message = "项目描述长度不能超过500个字符")
    private String description;

    /**
     * 作者
     */
    @NotBlank(message = "作者不能为空")
    @Size(max = 50, message = "作者名称长度不能超过50个字符")
    private String author;

    /**
     * 包路径
     */
    @NotBlank(message = "包路径不能为空")
    @Pattern(regexp = "^[a-z0-9.]+$", message = "包路径只能包含小写字母、数字和点")
    private String packagePath;

    /**
     * 端口号
     */
    @NotBlank(message = "端口号不能为空")
    @Pattern(regexp = "^\\d{1,5}$", message = "端口号必须是1-5位数字")
    private String port;

    /**
     * 数据库服务器
     */
    @NotBlank(message = "数据库服务器不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9.-]+$", message = "数据库服务器地址格式不正确")
    private String databaseServer;

    /**
     * 数据库端口
     */
    @NotBlank(message = "数据库端口不能为空")
    @Pattern(regexp = "^\\d{1,5}$", message = "数据库端口必须是1-5位数字")
    private String databasePort;

    /**
     * 数据库
     */
    @NotBlank(message = "数据库名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "数据库名称只能包含字母、数字和下划线")
    private String database;

    /**
     * 数据库用户名
     */
    @NotBlank(message = "数据库用户名不能为空")
    @Size(max = 50, message = "数据库用户名长度不能超过50个字符")
    private String databaseUser;

    /**
     * 数据库密码
     */
    @NotBlank(message = "数据库密码不能为空")
    @Size(max = 50, message = "数据库密码长度不能超过50个字符")
    private String databasePassword;

    /**
     * 后端项目模板路径
     */
    @NotBlank(message = "后端项目模板路径不能为空")
    private String backendGitPath;

    /**
     * 模板名
     */
    @NotBlank(message = "后端项目模板名不能为空")
    private String backendTemplateName;

}
