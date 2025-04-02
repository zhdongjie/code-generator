package com.original.generator.core.domain.bo;

import com.original.generator.core.domain.dto.FieldSupportConfigDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldSupportConfigBo {

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

    private FieldSupportConfigBo(FieldSupportConfigDto fieldSupportConfig) {
        this.supportSearch = fieldSupportConfig.isSupportSearch();
        this.supportSearchRange = fieldSupportConfig.isSupportSearchRange();
        this.supportMaintenance = fieldSupportConfig.isSupportMaintenance();
        this.supportView = fieldSupportConfig.isSupportView();
        this.supportImport = fieldSupportConfig.isSupportImport();
        this.supportExport = fieldSupportConfig.isSupportExport();
    }

    public static FieldSupportConfigBo of(FieldSupportConfigDto fieldSupportConfig) {
        return ObjectUtils.isEmpty(fieldSupportConfig)
                ? new FieldSupportConfigBo()
                : new FieldSupportConfigBo(fieldSupportConfig);
    }

}
