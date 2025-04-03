package com.original.generator.core.config;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisFlexConfig implements MyBatisFlexCustomizer, InsertListener, UpdateListener {

    @Override
    public void onInsert(Object entity) {
    }

    @Override
    public void onUpdate(Object entity) {
    }

    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        // SQL Audit
        AuditManager.setAuditEnable(true);
    }
}
