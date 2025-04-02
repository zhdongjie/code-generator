package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerConfigDto {

    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 描述
     */
    private String description;

}
