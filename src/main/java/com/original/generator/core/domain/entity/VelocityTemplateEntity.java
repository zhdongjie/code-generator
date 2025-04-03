package com.original.generator.core.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_velocity_template")
public class VelocityTemplateEntity {

    /**
     * 主键ID
     */
    @Column(value = "id")
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 模板组Id
     */
    @Column(value = "group_id")
    private Long groupId;

    /**
     * 模板内容
     */
    @Column(value = "template")
    private String template;

    /**
     * 最后修改时间
     */
    @Column(value = "update_date")
    private LocalDateTime updateDate;

    /**
     * 文件保存路径
     */
    @Column(value = "save_path")
    private String savePath;

    /**
     * 模板名称
     */
    @Column(value = "template_name")
    private String templateName;

    /**
     * 模板类型
     */
    @Column(value = "template_type")
    private String templateType;

}
