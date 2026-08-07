package com.saattech.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CastRequestDto {

    @NotBlank(message = "cast name cannot be left blank.")
    private String name;
    private String poster;
}

