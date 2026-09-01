package com.saattech.constant.exception;

public final class StorageExceptionMessages {
    private StorageExceptionMessages() {}
    public static final String INIT_FAILED = "Could not initialize storage directory";
    public static final String EMPTY_FILE = "Failed to store empty file.";
    public static final String SECURITY_VIOLATION = "Security violation: Only image and video files are allowed.";
    public static final String UNSUPPORTED_FORMAT = "Unsupported file format. Only JPG, PNG, WEBP, MP4 or WEBM are allowed.";
    public static final String STORE_FAILED = "Failed to store file.";
}

