package com.saattech.dto.cast;

import com.saattech.enums.CastType;
import lombok.Data;

@Data
public class CastContentDto {
    private Long contentId;
    private String title;
    private String poster;
    private CastType role;
}
