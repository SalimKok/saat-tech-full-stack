package com.saattech.dto.contentcast;

import com.saattech.enums.CastType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentCastRequestDto {

    @NotNull(message = "Cast ID field cannot be left blank.")
    private Long castId;

    @NotNull(message = "Cast role cannot be left blank.")
    private CastType role;

    private String castName;
}