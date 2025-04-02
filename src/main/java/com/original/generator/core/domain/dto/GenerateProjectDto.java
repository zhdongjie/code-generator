package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateProjectDto {

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 生成项目路径
     */
    private String filePath;

    /**
     * 生成文件是否覆盖
     */
    private boolean cover;

    /**
     * 后端项目
     */
    private BackendProjectDto backendProject;

    /**
     * 前端项目
     */
    private FrontendProjectDto frontendProject;

    /**
     * 模块列表
     */
    private List<BusinessModuleDto> businessModuleList;

}
