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
    private boolean enable;

    /**
     * 描述
     */
    private String description;

    private SwaggerConfigBo(SwaggerConfigDto swaggerConfig) {
        this.enable = swaggerConfig.isEnable();
        this.description = swaggerConfig.getDescription();
    }

    public static SwaggerConfigBo of(SwaggerConfigDto swaggerConfig) {
        return ObjectUtils.isEmpty(swaggerConfig)
                ? new SwaggerConfigBo()
                : new SwaggerConfigBo(swaggerConfig);
    }

}
