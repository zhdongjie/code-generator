package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessSupportConfigDto {

    /**
     * 是否支持导入
     */
    private boolean supportImport;

    /**
     * 是否支持导出
     */
    private boolean supportExport;

    /**
     * 是否支持批量删除
     */
    private boolean supportDeleteBatch;

    /**
     * 是否支持日志
     */
    private boolean supportLog;

}
