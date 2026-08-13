package com.saattech.mapper;

import com.saattech.dto.request.LicenseRequestDto;
import com.saattech.dto.response.LicenseResponseDto;
import com.saattech.entity.License;
import org.springframework.stereotype.Component;

@Component
public class LicenseMapper {
    public LicenseResponseDto toDto(License license) {
        if (license == null) {
            return null;
        }
        LicenseResponseDto dto = new LicenseResponseDto();
        dto.setId(license.getId());
        dto.setName(license.getName());
        dto.setStartDate(license.getStartDate());
        dto.setEndDate(license.getEndDate());
        dto.setStatus(license.getStatus());
        return dto;
    }

    public License toEntity(LicenseRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        License license = new License();
        license.setName(requestDto.getName());
        license.setStartDate(requestDto.getStartDate());
        license.setEndDate(requestDto.getEndDate());
        license.setStatus(requestDto.getStatus());
        return license;
    }
}
