package com.original.generator.core.exception;

public class SqlGenerationException extends BaseException {
    public SqlGenerationException(String message) {
        super("SQL_GENERATION_ERROR", message);
    }

    public SqlGenerationException(String message, Throwable cause) {
        super("SQL_GENERATION_ERROR", message, cause);
    }
} 