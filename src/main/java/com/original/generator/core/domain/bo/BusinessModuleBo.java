package com.original.generator.core.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessModuleBo {

    /**
     * 业务模块名称
     */
    private String moduleName;

    /**
     * 业务模块名称(首字母小写)
     */
    private String moduleNameFirstLetterLower;

    /**
     * 业务数据库表名
     */
    private String tableName;

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
    private SwaggerConfigBo swaggerConfig;

    /**
     * 业务功能支持配置
     */
    private BusinessSupportConfigBo businessSupportConfig;

    /**
     * 字段
     */
    private List<FieldBo> fieldList;

}
