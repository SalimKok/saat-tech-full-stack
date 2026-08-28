package com.saattech.dto.trailer;

import lombok.Data;

@Data
public class TrailerResponseDto {

    private Long id;
    private String name;
    private String youtubeKey;
    private String youtubeEmbedUrl;
    private String youtubeThumbnailUrl;
    private String site;
    private String type;
    private Integer size;
    private String language;
    private String fileUrl;
}
