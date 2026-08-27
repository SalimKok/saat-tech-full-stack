package com.saattech.dto.contentcast;

import com.saattech.dto.cast.CastResponseDto;
import com.saattech.enums.CastType;
import lombok.Data;

@Data
public class ContentCastResponseDto {

    private Long id;
    private CastResponseDto cast;
    private CastType role;
}