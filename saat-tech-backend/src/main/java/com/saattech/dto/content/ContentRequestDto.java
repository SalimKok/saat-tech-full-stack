package com.saattech.dto.content;

import com.saattech.constant.validation.ContentValidationMessages;
import com.saattech.dto.contentcast.ContentCastRequestDto;
import com.saattech.dto.license.LicenseRequestDto;
import com.saattech.dto.metadata.MetadataRequestDto;
import com.saattech.enums.ContentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ContentRequestDto {

    private Integer seasonNo;
    private Integer episodeNo;

    @NotNull(message = ContentValidationMessages.TYPE_NOT_NULL)
    private ContentType contentType;

    private Long parentId;

    @Valid
    private List<ContentCastRequestDto> casts;

    private List<ContentRequestDto> subContents;

    @Valid
    @NotNull(message = ContentValidationMessages.METADATA_NOT_NULL)
    private MetadataRequestDto metadata;

    @Valid
    private List<LicenseRequestDto> licenses;
}