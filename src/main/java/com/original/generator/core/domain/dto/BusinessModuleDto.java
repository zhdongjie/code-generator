package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessModuleDto {

    /**
     * 业务模块名称
     */
    private String moduleName;

    /**
     * 业务模块描述
     */
    private String comment;

    /**
     * 是否需要权限
     */
    private boolean authentication;

    /**
     * swagger配置
     */
    private SwaggerConfigDto swaggerConfig;

    /**
     * 业务功能支持配置
     */
    private BusinessSupportConfigDto businessSupportConfig;

    /**
     * 字段
     */
    private List<FieldDto> fieldList;

    /**
     * 业务字段关联配置
     */
    private FieldRelationConfigDto fieldRelationConfig;

}
