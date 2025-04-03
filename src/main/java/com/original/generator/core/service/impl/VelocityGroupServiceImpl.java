package com.original.generator.core.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.original.generator.core.domain.entity.VelocityGroupEntity;
import com.original.generator.core.mapper.VelocityGroupMapper;
import com.original.generator.core.service.VelocityGroupService;
import org.springframework.stereotype.Service;

@Service
public class VelocityGroupServiceImpl extends ServiceImpl<VelocityGroupMapper, VelocityGroupEntity> implements VelocityGroupService {
}
