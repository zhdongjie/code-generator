package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendProjectDto {

    /**
     * 前端项目名称
     */
    @NotBlank(message = "前端项目名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "前端项目名称只能包含字母、数字、下划线和连字符")
    private String name;

    /**
     * 项目版本号
     */
    @NotBlank(message = "项目版本号不能为空")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "版本号格式不正确，应为x.y.z格式")
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
     * 开发服务器端口
     */
    @NotNull(message = "端口号不能为空")
    @Positive(message = "端口号必须为正数")
    private Integer port;

    /**
     * API基础URL
     */
    @NotBlank(message = "API基础URL不能为空")
    @Pattern(regexp = "^https?://[\\w.-]+(?:\\.[\\w.-]+)*(?:\\:\\d+)?$", 
            message = "API基础URL格式不正确")
    private String apiBaseUrl;

    /**
     * 前端模板Git地址
     */
    @NotBlank(message = "前端模板Git地址不能为空")
    @Pattern(regexp = "^(https?|git)://[\\w.-]+(?:\\.[\\w.-]+)*(?:\\:\\d+)?/[\\w.-]+/[\\w.-]+(?:\\.git)?$", 
            message = "Git地址格式不正确")
    private String frontendGitPath;

    /**
     * 前端模板名称
     */
    @NotBlank(message = "前端模板名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "前端模板名称只能包含字母、数字、下划线和连字符")
    private String frontendTemplateName;

    /**
     * UI配置
     */
    @NotNull(message = "UI配置不能为空")
    @Valid
    private UiConfigDto uiConfig;

}
