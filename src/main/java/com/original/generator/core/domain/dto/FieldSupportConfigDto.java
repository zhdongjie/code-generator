package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldSupportConfigDto {

    /**
     * 是否支持搜索
     */
    private boolean supportSearch;

    /**
     * 是否支持搜索区间
     */
    private boolean supportSearchRange;

    /**
     * 是否支持维护
     */
    private boolean supportMaintenance;

    /**
     * 是否支持查看
     */
    private boolean supportView;

    /**
     * 是否支持导入
     */
    private boolean supportImport;

    /**
     * 是否支持导出
     */
    private boolean supportExport;

}
