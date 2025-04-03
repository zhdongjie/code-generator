package com.original.generator.core.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.original.generator.core.domain.entity.VelocityTemplateEntity;
import com.original.generator.core.mapper.VelocityTemplateMapper;
import com.original.generator.core.service.VelocityTemplateService;
import org.springframework.stereotype.Service;

@Service
public class VelocityTemplateServiceImpl extends ServiceImpl<VelocityTemplateMapper, VelocityTemplateEntity> implements VelocityTemplateService {
}
