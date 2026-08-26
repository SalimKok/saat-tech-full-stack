package com.saattech.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbFindResponseDto {

    @JsonProperty("movie_results")
    private List<TmdbFindResult> movieResults;

    @JsonProperty("tv_results")
    private List<TmdbFindResult> tvResults;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbFindResult {
        private Long id;
        private String title;
        private String name;
    }
}
