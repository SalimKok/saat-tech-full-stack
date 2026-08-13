package com.saattech.dto.request;

import com.saattech.enums.ContentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ContentRequestDto {

    private Integer seasonNo;
    private Integer episodeNo;

    @NotNull(message = "Content type cannot be left blank.")
    private ContentType contentType;

    private Long parentId;

    @Valid
    private List<ContentCastRequestDto> casts;

    private List<ContentRequestDto> subContents;

    @Valid
    @NotNull(message = "Metadata cannot be left blank.")
    private MetadataRequestDto metadata;

    @Valid
    private List<LicenseRequestDto> licenses;
}