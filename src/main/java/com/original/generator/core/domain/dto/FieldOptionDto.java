package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldOptionDto {

    /**
     * 选项名称
     */
    @NotBlank(message = "选项名称不能为空")
    @Size(max = 50, message = "选项名称长度不能超过50个字符")
    private String label;

    /**
     * 选项值
     */
    @NotBlank(message = "选项值不能为空")
    @Size(max = 50, message = "选项值长度不能超过50个字符")
    private String value;

}
