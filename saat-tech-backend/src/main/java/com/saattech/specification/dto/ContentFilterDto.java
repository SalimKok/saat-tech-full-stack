package com.saattech.specification.dto;

import com.saattech.enums.ContentType;
import com.saattech.enums.EntityStatus;
import lombok.Data;

@Data
public class ContentFilterDto {
    private String title;
    private ContentType contentType;
    private EntityStatus status;
    private String genre;
    private Double minRating;
    private Integer year;
    private Float titleBoost;
    private Float plotBoost;
    private Float castBoost;
    private Float genreBoost;
}