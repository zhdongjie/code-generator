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
public class SwaggerConfigDto {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 标题
     */
    @NotBlank(message = "Swagger标题不能为空")
    @Size(max = 100, message = "Swagger标题长度不能超过100个字符")
    private String title;

    /**
     * 描述
     */
    @Size(max = 500, message = "Swagger描述长度不能超过500个字符")
    private String description;

    /**
     * 版本
     */
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "版本号格式不正确，应为x.y.z格式")
    private String version;

}
