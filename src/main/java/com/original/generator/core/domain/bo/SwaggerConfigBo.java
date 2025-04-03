package com.original.generator.core.domain.bo;

import com.original.generator.core.domain.dto.SwaggerConfigDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerConfigBo {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 版本
     */
    private String version;


    private SwaggerConfigBo(SwaggerConfigDto swaggerConfig) {
        this.enabled = swaggerConfig.isEnabled();
        this.title = swaggerConfig.getTitle();
        this.description = swaggerConfig.getDescription();
        this.version = swaggerConfig.getVersion();
    }

    public static SwaggerConfigBo of(SwaggerConfigDto swaggerConfig) {
        return ObjectUtils.isEmpty(swaggerConfig)
                ? new SwaggerConfigBo()
                : new SwaggerConfigBo(swaggerConfig);
    }

}
