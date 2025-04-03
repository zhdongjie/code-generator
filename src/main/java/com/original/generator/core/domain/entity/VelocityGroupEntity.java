package com.original.generator.core.domain.entity;

import com.mybatisflex.annotation.*;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_velocity_group")
public class VelocityGroupEntity {

    /**
     * 主键ID
     */
    @Column(value = "id")
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * Git仓库地址
     */
    @Column(value = "git_repository_path")
    private String gitRepositoryPath;

    /**
     * 模板分类名称
     */
    @Column(value = "group_name")
    private String groupName;

    /**
     * 模板分类类别
     */
    @Column(value = "group_type")
    private String groupType;

    /**
     * 模板列表
     */
    @RelationOneToMany(selfField = "id", targetField = "groupId")
    private List<VelocityTemplateEntity> templateList;

} 