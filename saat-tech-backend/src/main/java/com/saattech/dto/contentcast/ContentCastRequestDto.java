package com.saattech.dto.contentcast;

import com.saattech.constant.validation.CastValidationMessages;
import com.saattech.enums.CastType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentCastRequestDto {

    @NotNull(message = CastValidationMessages.ID_NOT_NULL)
    private Long castId;

    @NotNull(message = CastValidationMessages.ROLE_NOT_NULL)
    private CastType role;

    private String castName;
}