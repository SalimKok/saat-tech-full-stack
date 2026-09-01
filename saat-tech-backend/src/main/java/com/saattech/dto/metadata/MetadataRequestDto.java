package com.saattech.dto.metadata;

import com.saattech.constant.validation.ContentValidationMessages;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MetadataRequestDto {
    @NotBlank(message = ContentValidationMessages.TITLE_NOT_BLANK)
    private String title;

    private String poster;
    private LocalDate released;

    @PositiveOrZero(message = ContentValidationMessages.RATING_POSITIVE_OR_ZERO)
    private Double imdbRating;

    private String imdbVotes;
    private String runtime;
    private String genre;
    private String plot;
    private String language;
    private String country;
    private String rated;
    private String awards;
    private String boxOffice;
    private String metascore;
    private String imdbID;
}
