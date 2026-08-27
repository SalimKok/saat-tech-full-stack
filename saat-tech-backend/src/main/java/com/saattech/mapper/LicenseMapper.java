package com.saattech.mapper;

import com.saattech.dto.license.LicenseRequestDto;
import com.saattech.dto.license.LicenseResponseDto;
import com.saattech.entity.License;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    LicenseResponseDto toDto(License license);

    License toEntity(LicenseRequestDto requestDto);
}
