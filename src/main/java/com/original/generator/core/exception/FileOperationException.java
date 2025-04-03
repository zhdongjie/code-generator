package com.original.generator.core.exception;

public class FileOperationException extends BaseException {
    public FileOperationException(String message) {
        super("FILE_OPERATION_ERROR", message);
    }

    public FileOperationException(String message, Throwable cause) {
        super("FILE_OPERATION_ERROR", message, cause);
    }
} 