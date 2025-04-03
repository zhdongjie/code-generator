package com.original.generator.core.exception;

public class TemplateException extends BaseException {
    public TemplateException(String message) {
        super("TEMPLATE_ERROR", message);
    }

    public TemplateException(String message, Throwable cause) {
        super("TEMPLATE_ERROR", message, cause);
    }
} 