package com.saattech.mapper;

import com.saattech.dto.request.LicenseRequestDto;
import com.saattech.dto.response.LicenseResponseDto;
import com.saattech.entity.License;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    LicenseResponseDto toDto(License license);

    License toEntity(LicenseRequestDto requestDto);
}
