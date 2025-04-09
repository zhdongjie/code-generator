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

/**
 * Velocity模板工具类
 * 负责管理和渲染Velocity模板，支持从数据库加载模板并进行缓存
 * <p>
 * 主要功能：
 * 1. 初始化Velocity引擎
 * 2. 从数据库加载模板
 * 3. 缓存模板以提高性能
 * 4. 渲染模板内容
 * 5. 管理模板缓存
 *
 * @author 代码生成器团队
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class VelocityTemplateUtils {
    private static final Logger logger = LoggerFactory.getLogger(VelocityTemplateUtils.class);

    /**
     * Velocity配置属性
     * 包含模板加载、编码、缓存等配置信息
     */
    private final VelocityProperties properties;

    /**
     * 数据源
     * 用于从数据库加载模板内容
     */
    private final DataSource dataSource;

    /**
     * Velocity引擎实例
     * 用于加载和渲染模板
     */
    private VelocityEngine velocityEngine;

    /**
     * 模板缓存
     * 使用ConcurrentHashMap存储已加载的模板，提高访问性能
     * Key: 模板路径
     * Value: 模板对象
     */
    private final Map<String, Template> templateCache = new ConcurrentHashMap<>();

    /**
     * 初始化Velocity引擎
     * 在组件构造后自动执行，完成以下工作：
     * 1. 创建Velocity引擎实例
     * 2. 配置数据源资源加载器
     * 3. 设置编码和日志配置
     * 4. 配置热重载和缓存选项
     *
     * @throws TemplateException 如果初始化失败
     */
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
     * 根据模板路径获取模板对象，支持模板缓存
     * <p>
     * 步骤：
     * 1. 检查是否启用缓存
     * 2. 如果未启用缓存，直接从引擎获取模板
     * 3. 如果启用缓存，先从缓存获取，不存在则加载并缓存
     *
     * @param templatePath 模板路径
     * @return 模板对象
     * @throws TemplateException 如果模板加载失败
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
     * 将模板与上下文数据合并，生成最终内容
     * <p>
     * 步骤：
     * 1. 获取模板对象
     * 2. 创建Velocity上下文
     * 3. 合并模板和上下文
     * 4. 返回渲染结果
     *
     * @param templatePath 模板路径
     * @param context      模板上下文数据
     * @return 渲染后的内容
     * @throws TemplateException 如果渲染失败
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
     * 清空所有缓存的模板，通常在模板更新后调用
     */
    public void clearCache() {
        templateCache.clear();
        logger.info("Template cache cleared");
    }

    /**
     * 从缓存中移除指定模板
     * 当特定模板需要更新时，可以从缓存中移除该模板
     *
     * @param templatePath 要移除的模板路径
     */
    public void removeFromCache(String templatePath) {
        if (templatePath != null) {
            templateCache.remove(templatePath);
            logger.info("Template removed from cache: {}", templatePath);
        }
    }

    /**
     * 预编译模板
     * 提前加载并编译指定的模板，提高首次访问性能
     * <p>
     * 注意：只有在启用预编译功能时才会执行
     *
     * @param templatePaths 要预编译的模板路径列表
     * @throws TemplateException 如果预编译失败
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