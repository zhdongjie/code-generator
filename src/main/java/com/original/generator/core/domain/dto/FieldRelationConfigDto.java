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
public class FieldRelationConfigDto {

    /**
     * 业务模块
     */
    @NotBlank(message = "业务模块不能为空")
    @Size(max = 50, message = "业务模块名称长度不能超过50个字符")
    private String businessModule;

    /**
     * 自身字段
     */
    @NotBlank(message = "自身字段不能为空")
    @Size(max = 50, message = "自身字段名称长度不能超过50个字符")
    private String selfField;

    /**
     * 目标字段
     */
    @NotBlank(message = "目标字段不能为空")
    @Size(max = 50, message = "目标字段名称长度不能超过50个字符")
    private String targetField;

    /**
     * 关系类型
     */
    @NotBlank(message = "关系类型不能为空")
    @Pattern(regexp = "^(ONE_TO_ONE|ONE_TO_MANY|MANY_TO_ONE|MANY_TO_MANY)$",
            message = "关系类型必须是ONE_TO_ONE、ONE_TO_MANY、MANY_TO_ONE或MANY_TO_MANY")
    private String relationType;

}
