package com.original.generator.core.utils;

import com.original.generator.core.config.VelocityProperties;
import com.original.generator.core.exception.TemplateException;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.DataSourceResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class VelocityTemplateUtils {
    private static final Logger logger = LoggerFactory.getLogger(VelocityTemplateUtils.class);

    private final VelocityProperties properties;
    private final DataSource dataSource;

    private VelocityEngine velocityEngine;
    private final Map<String, Template> templateCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            velocityEngine = new VelocityEngine();
            Properties props = new Properties();
            DataSourceResourceLoader dataSourceResourceLoader = new DataSourceResourceLoader();
            dataSourceResourceLoader.setDataSource(dataSource);

            props.put("input.encoding", properties.getInputEncoding());
            props.put("output.encoding", properties.getOutputEncoding());
            props.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");

            props.put("resource.loader", properties.getResourceLoader());
            props.put("resource.loader.ds.class", properties.getClassResourceLoader());
            props.put("resource.loader.ds.resource.table", properties.getDatasourceTable());
            props.put("resource.loader.ds.resource.key_column", properties.getDatasourceKeyColumn());
            props.put("resource.loader.ds.resource.template_column", properties.getDatasourceTemplateColumn());
            props.put("resource.loader.ds.resource.timestamp_column", properties.getDatasourceTimestampColumn());
            props.put("resource.loader.ds.instance", dataSourceResourceLoader);

            if (properties.isHotReloadEnabled()) {
                props.put("resource.loader.ds.cache", properties.isCacheEnabled());
                props.put("resource.loader.ds.modification_check_interval", properties.getModificationCheckInterval());
            }

            velocityEngine.init(props);
            logger.info("Velocity engine initialized with properties: {}", properties);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TemplateException("Failed to initialize Velocity engine", e);
        }
    }

    /**
     * 获取模板
     */
    public Template getTemplate(String templatePath) {
        if (!properties.isCacheEnabled()) {
            try {
                return velocityEngine.getTemplate(templatePath);
            } catch (ResourceNotFoundException | ParseErrorException e) {
                throw new TemplateException("Failed to load template: " + templatePath, e);
            }
        }

        return templateCache.computeIfAbsent(templatePath, path -> {
            try {
                Template template = velocityEngine.getTemplate(path);
                logger.debug("Template loaded and cached: {}", path);
                return template;
            } catch (ResourceNotFoundException | ParseErrorException e) {
                throw new TemplateException("Failed to load template: " + path, e);
            }
        });
    }

    /**
     * 渲染模板
     */
    public String render(String templatePath, Map<String, Object> context) {
        try {
            Template template = getTemplate(templatePath);
            VelocityContext velocityContext = new VelocityContext(context);
            StringWriter writer = new StringWriter();
            template.merge(velocityContext, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new TemplateException("Failed to render template: " + templatePath, e);
        }
    }

    /**
     * 清除模板缓存
     */
    public void clearCache() {
        templateCache.clear();
        logger.info("Template cache cleared");
    }

    /**
     * 从缓存中移除指定模板
     */
    public void removeFromCache(String templatePath) {
        if (templatePath != null) {
            templateCache.remove(templatePath);
            logger.info("Template removed from cache: {}", templatePath);
        }
    }

    /**
     * 预编译模板
     */
    public void precompileTemplates(String... templatePaths) {
        if (!properties.isPrecompileEnabled()) {
            return;
        }

        for (String path : templatePaths) {
            try {
                getTemplate(path);
                logger.debug("Template precompiled: {}", path);
            } catch (Exception e) {
                throw new TemplateException("Failed to precompile template: " + path, e);
            }
        }
    }
} 