package com.saattech.specification.dto;

import com.saattech.enums.ContentStatus;
import com.saattech.enums.ContentType;
import lombok.Data;

@Data
public class ContentFilterDto {
    private String title;
    private ContentType contentType;
    private ContentStatus status;
    private String genre;
    private Double minRating;
    private Integer year;
    private Float titleBoost;
    private Float plotBoost;
    private Float castBoost;
    private Float genreBoost;
    private Double bm25Weight;
    private Double vectorWeight;
}