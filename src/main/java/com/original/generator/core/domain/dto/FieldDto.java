package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDto {
    /**
     * 字段名
     */
    @NotBlank(message = "字段名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "字段名只能包含字母、数字和下划线")
    private String name;

    /**
     * 数据类型
     */
    @NotBlank(message = "数据类型不能为空")
    @Pattern(regexp = "^(String|Integer|Long|BigDecimal|Date|Boolean)$",
            message = "数据类型必须是String、Integer、Long、BigDecimal、Date或Boolean")
    private String type;

    /**
     * 长度
     */
    @Min(value = 0, message = "长度不能小于0")
    private Integer length;

    /**
     * 小数点
     */
    @Min(value = 0, message = "小数点位数不能小于0")
    private Integer decimalPoint;

    /**
     * 描述
     */
    @Size(max = 200, message = "字段描述长度不能超过200个字符")
    private String comment;

    /**
     * 格式
     */
    @Pattern(regexp = ".*", message = "格式不正确")
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
    @Valid
    private FieldRuleDto fieldRule;

    /**
     * 字段功能支持配置
     */
    @Valid
    private FieldSupportConfigDto fieldSupportConfig;

    /**
     * 字段选项配置
     */
    @Valid
    private List<FieldOptionDto> fieldOptionList;

}
