package com.original.generator.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.original.generator.core.domain.info.TemplateProjectInfo;
import com.original.generator.core.exception.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置加载工具类
 * 负责加载和管理模板项目的配置文件，支持配置缓存
 * <p>
 * 主要功能：
 * 1. 从文件加载配置
 * 2. 缓存配置以提高性能
 * 3. 管理配置缓存
 *
 * @author 代码生成器团队
 * @version 1.0
 */
public class ConfigLoaderUtils {
    /**
     * 日志记录器
     * 用于记录配置加载和缓存操作的日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoaderUtils.class);

    /**
     * JSON对象映射器
     * 用于将JSON文件解析为Java对象
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 配置缓存
     * 使用ConcurrentHashMap存储已加载的配置，提高访问性能
     * Key: 配置文件路径
     * Value: 模板项目信息对象
     */
    private static final ConcurrentHashMap<String, TemplateProjectInfo> configCache = new ConcurrentHashMap<>();

    /**
     * 加载配置
     * 从指定路径加载配置文件，支持配置缓存
     * <p>
     * 步骤：
     * 1. 验证配置文件路径
     * 2. 检查缓存中是否存在
     * 3. 如果不存在，从文件加载并缓存
     * 4. 返回配置对象
     *
     * @param configFilePath 配置文件路径
     * @return 模板项目信息对象
     * @throws ConfigException 如果配置加载失败
     */
    public static TemplateProjectInfo loadConfig(String configFilePath) {
        if (configFilePath == null || configFilePath.trim().isEmpty()) {
            throw new ConfigException("Config file path cannot be null or empty");
        }

        return configCache.computeIfAbsent(configFilePath, path -> {
            try {
                File configFile = new File(path);
                if (!configFile.exists()) {
                    throw new ConfigException("Config file does not exist: " + path);
                }
                TemplateProjectInfo config = objectMapper.readValue(configFile, TemplateProjectInfo.class);
                logger.info("Successfully loaded config from: {}", path);
                return config;
            } catch (IOException e) {
                throw new ConfigException("Failed to load config from: " + path, e);
            }
        });
    }

    /**
     * 清除配置缓存
     * 清空所有缓存的配置，通常在配置更新后调用
     */
    public static void clearCache() {
        configCache.clear();
        logger.info("Config cache cleared");
    }

    /**
     * 从缓存中移除指定配置
     * 当特定配置文件需要更新时，可以从缓存中移除该配置
     *
     * @param configFilePath 要移除的配置文件路径
     */
    public static void removeFromCache(String configFilePath) {
        if (configFilePath != null) {
            configCache.remove(configFilePath);
            logger.info("Config removed from cache: {}", configFilePath);
        }
    }
}
