package com.saattech.dto.cast;

import com.saattech.constant.validation.CastValidationMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CastRequestDto {

    @NotBlank(message = CastValidationMessages.NAME_NOT_BLANK)
    private String name;
    private String poster;
}

