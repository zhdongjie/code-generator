package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDto {
    /**
     * 字段名
     */
    private String name;

    /**
     * 数据类型
     */
    private String type;

    /**
     * 长度
     */
    private Integer length;

    /**
     * 小数点
     */
    private Integer decimalPoint;

    /**
     * 描述
     */
    private String comment;

    /**
     * 格式
     */
    private String format;

    /**
     * 是否唯一
     */
    private boolean unique;

    /**
     * 是否主键
     */
    private boolean primaryKey;

    /**
     * 字段校验规则
     */
    private FieldRuleDto fieldRule;

    /**
     * 字段功能支持配置
     */
    private FieldSupportConfigDto fieldSupportConfig;

    /**
     * 字段选项配置
     */
    private List<FieldOptionDto> fieldOptionList;

}
