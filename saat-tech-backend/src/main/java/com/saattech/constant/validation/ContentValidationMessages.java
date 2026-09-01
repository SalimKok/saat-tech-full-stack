package com.saattech.constant.validation;

public final class ContentValidationMessages {
    private ContentValidationMessages() {}
    public static final String TITLE_NOT_BLANK = "Content title cannot be left blank.";
    public static final String TYPE_NOT_NULL = "Content type cannot be left blank.";
    public static final String METADATA_NOT_NULL = "Metadata cannot be left blank.";
    public static final String RATING_POSITIVE_OR_ZERO = "IMDB rating must be 0 or greater.";
}
