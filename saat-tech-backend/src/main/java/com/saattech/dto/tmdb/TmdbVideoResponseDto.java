package com.saattech.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbVideoResponseDto {

    private Long id;
    private List<TmdbVideoResult> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbVideoResult {
        private String name;
        private String key;
        private String site;
        private Integer size;
        private String type;

        @JsonProperty("iso_639_1")
        private String language;
    }
}
