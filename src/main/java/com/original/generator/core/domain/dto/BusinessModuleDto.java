package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessModuleDto {

    /**
     * 业务模块名称
     */
    @NotBlank(message = "业务模块名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "业务模块名称只能包含字母、数字和下划线")
    private String moduleName;

    /**
     * 业务模块描述
     */
    @Size(max = 200, message = "业务模块描述长度不能超过200个字符")
    private String comment;

    /**
     * 是否需要权限
     */
    private boolean authentication;

    /**
     * swagger配置
     */
    @Valid
    private SwaggerConfigDto swaggerConfig;

    /**
     * 业务功能支持配置
     */
    @Valid
    private BusinessSupportConfigDto businessSupportConfig;

    /**
     * 字段
     */
    @Valid
    @NotEmpty(message = "字段列表不能为空")
    private List<FieldDto> fieldList;

    /**
     * 业务字段关联配置
     */
    @Valid
    private FieldRelationConfigDto fieldRelationConfig;

}
