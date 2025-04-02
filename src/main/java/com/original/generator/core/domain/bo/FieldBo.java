package com.original.generator.core.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldBo {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段数据类型
     */
    private String fieldType;

    /**
     * 字段数据类型路径
     */
    private String fieldTypePath;

    /**
     * 是否主键
     */
    private boolean primaryKey;

    /**
     * 数据库字段名
     */
    private String columnName;

    /**
     * 数据库字段类型
     */
    private String columnType;

    /**
     * 数据库字段长度
     */
    private Integer columnLength;

    /**
     * 数据库字段小数点
     */
    private Integer columnDecimalPoint;

    /**
     * 注释
     */
    private String comment;

    /**
     * 格式
     */
    private String format;

    /**
     * 字段功能支持配置
     */
    private FieldSupportConfigBo fieldSupportConfig;

}
