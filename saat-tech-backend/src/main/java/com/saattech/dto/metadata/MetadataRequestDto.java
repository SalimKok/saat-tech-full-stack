package com.saattech.dto.metadata;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MetadataRequestDto {
    @NotBlank(message = "Content title cannot be left blank.")
    private String title;

    private String poster;
    private LocalDate released;

    @PositiveOrZero(message = "IMDB rating must be 0 or greater.")
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
