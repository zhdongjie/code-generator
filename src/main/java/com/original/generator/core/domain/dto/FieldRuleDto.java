package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldRuleDto {

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 正则表达式
     */
    private String pattern;

    /**
     * 最小长度
     */
    private Integer minLength;

    /**
     * 最大长度
     */
    private Integer maxLength;

    /**
     * 最小值
     */
    private BigDecimal minValue;

    /**
     * 最大值
     */
    private BigDecimal maxValue;

}
