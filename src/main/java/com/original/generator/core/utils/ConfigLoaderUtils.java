package com.original.generator.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.original.generator.core.domain.info.TemplateProjectInfo;
import com.original.generator.core.exception.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigLoaderUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoaderUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ConcurrentHashMap<String, TemplateProjectInfo> configCache = new ConcurrentHashMap<>();

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

    public static void clearCache() {
        configCache.clear();
        logger.info("Config cache cleared");
    }

    public static void removeFromCache(String configFilePath) {
        if (configFilePath != null) {
            configCache.remove(configFilePath);
            logger.info("Config removed from cache: {}", configFilePath);
        }
    }
}
