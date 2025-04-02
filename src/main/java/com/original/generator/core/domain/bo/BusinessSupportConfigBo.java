package com.original.generator.core.domain.bo;

import com.original.generator.core.domain.dto.BusinessSupportConfigDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessSupportConfigBo {

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

    private BusinessSupportConfigBo(BusinessSupportConfigDto businessSupportConfig) {
        this.supportImport = businessSupportConfig.isSupportImport();
        this.supportExport = businessSupportConfig.isSupportExport();
        this.supportDeleteBatch = businessSupportConfig.isSupportDeleteBatch();
        this.supportLog = businessSupportConfig.isSupportLog();
    }

    public static BusinessSupportConfigBo of(BusinessSupportConfigDto businessSupportConfig) {
        return ObjectUtils.isEmpty(businessSupportConfig)
                ? new BusinessSupportConfigBo()
                : new BusinessSupportConfigBo(businessSupportConfig);
    }

}
