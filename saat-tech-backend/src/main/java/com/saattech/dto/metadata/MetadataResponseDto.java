package com.saattech.dto.metadata;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MetadataResponseDto {
    private Long id;
    private String title;
    private String poster;
    private LocalDate released;
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
