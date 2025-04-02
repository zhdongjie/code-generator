package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldOptionDto {

    /**
     * 选项名称
     */
    private String label;

    /**
     * 选项值
     */
    private String value;

}
