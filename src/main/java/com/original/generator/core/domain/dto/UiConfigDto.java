package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UiConfigDto {
    /**
     * 主题
     * 可选值：light, dark
     */
    @NotBlank(message = "主题不能为空")
    @Pattern(regexp = "^(light|dark)$", message = "主题只能是light或dark")
    private String theme;

    /**
     * 主色调
     * 十六进制颜色代码
     */
    @NotBlank(message = "主色调不能为空")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "主色调必须是有效的十六进制颜色代码")
    private String primaryColor;

    /**
     * 布局方式
     * 可选值：side, top
     */
    @NotBlank(message = "布局方式不能为空")
    @Pattern(regexp = "^(side|top)$", message = "布局方式只能是side或top")
    private String layout;

    /**
     * 是否固定头部
     */
    private Boolean fixedHeader = true;

    /**
     * 是否固定侧边栏
     */
    private Boolean fixedSidebar = true;

    /**
     * 是否显示面包屑
     */
    private Boolean showBreadcrumb = true;

    /**
     * 是否显示标签页
     */
    private Boolean showTabs = true;

    /**
     * 是否显示页脚
     */
    private Boolean showFooter = true;
}