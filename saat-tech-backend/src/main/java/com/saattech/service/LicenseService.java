package com.saattech.service;

import com.saattech.dto.license.LicenseRequestDto;
import com.saattech.dto.license.LicenseResponseDto;

public interface LicenseService {
    LicenseResponseDto addLicenseToContent(Long contentId, LicenseRequestDto requestDto);

    LicenseResponseDto updateLicense(Long licenseId, LicenseRequestDto requestDto);

    void deleteLicense(Long licenseId);

    LicenseResponseDto detachLicenseFromContent(Long licenseId);

    void processExpiredLicenses();
}
