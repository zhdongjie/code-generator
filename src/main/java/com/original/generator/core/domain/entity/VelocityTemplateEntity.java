package com.original.generator.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VelocityTemplateEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 模板组Id
     */
    private Long groupId;

    /**
     * 模板内容
     */
    private String template;

    /**
     * 最后修改时间
     */
    private Date updateDate;

    /**
     * 文件保存路径
     */
    private String savePath;

    /**
     * 模板名称
     */
    private String templateName;

}
