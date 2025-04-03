package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendProjectDto {

    /**
     * 前端项目名称
     */
    @NotBlank(message = "前端项目名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "前端项目名称只能包含字母、数字、下划线和连字符")
    private String name;

}
