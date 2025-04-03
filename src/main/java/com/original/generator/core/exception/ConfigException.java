package com.original.generator.core.exception;

public class ConfigException extends BaseException {
    public ConfigException(String message) {
        super("CONFIG_ERROR", message);
    }

    public ConfigException(String message, Throwable cause) {
        super("CONFIG_ERROR", message, cause);
    }
} 