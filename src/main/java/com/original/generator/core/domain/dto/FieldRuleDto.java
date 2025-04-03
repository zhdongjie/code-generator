package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
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
    @Pattern(regexp = ".*", message = "正则表达式格式不正确")
    private String pattern;

    /**
     * 最小长度
     */
    @Min(value = 0, message = "最小长度不能小于0")
    private Integer minLength;

    /**
     * 最大长度
     */
    @Min(value = 0, message = "最大长度不能小于0")
    private Integer maxLength;

    /**
     * 最小值
     */
    @DecimalMin(value = "0", message = "最小值不能小于0")
    private BigDecimal minValue;

    /**
     * 最大值
     */
    @DecimalMin(value = "0", message = "最大值不能小于0")
    private BigDecimal maxValue;

}
