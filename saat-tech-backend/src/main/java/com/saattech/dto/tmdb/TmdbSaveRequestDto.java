package com.saattech.dto.tmdb;

import lombok.Data;

@Data
public class TmdbSaveRequestDto {
    private String name;
    private String youtubeKey;
    private String site;
    private String type;
    private Integer size;
    private String language;
}

