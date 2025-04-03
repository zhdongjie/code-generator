package com.original.generator.core.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateProjectDto {

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "项目名称只能包含字母、数字、下划线和连字符")
    private String projectName;

    /**
     * 生成项目路径
     */
    @NotBlank(message = "生成项目路径不能为空")
    private String filePath;

    /**
     * 生成文件是否覆盖
     */
    private boolean cover;

    /**
     * 后端项目
     */
    @Valid
    @NotNull(message = "后端项目配置不能为空")
    private BackendProjectDto backendProject;

    /**
     * 前端项目
     */
    @Valid
    private FrontendProjectDto frontendProject;

    /**
     * 模块列表
     */
    @Valid
    @NotEmpty(message = "业务模块列表不能为空")
    private List<BusinessModuleDto> businessModuleList;

}
