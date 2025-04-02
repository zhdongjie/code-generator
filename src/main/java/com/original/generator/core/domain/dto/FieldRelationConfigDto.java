package com.original.generator.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldRelationConfigDto {

    /**
     * 业务模块
     */
    private String businessModule;

    /**
     * 自身字段
     */
    private String selfField;

    /**
     * 目标字段
     */
    private String targetField;

    /**
     * 关系类型
     */
    private String relationType;

}
