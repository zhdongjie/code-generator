package com.original.generator.core.exception;

public class GitException extends BaseException {
    public GitException(String message) {
        super("GIT_ERROR", message);
    }

    public GitException(String message, Throwable cause) {
        super("GIT_ERROR", message, cause);
    }
} 