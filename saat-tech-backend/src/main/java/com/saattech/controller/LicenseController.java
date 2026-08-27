package com.saattech.controller;

import com.saattech.dto.license.LicenseRequestDto;
import com.saattech.dto.license.LicenseResponseDto;
import com.saattech.security.IsAdmin;
import com.saattech.service.LicenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
@IsAdmin
public class LicenseController {
    private final LicenseService licenseService;

    @PostMapping("/content/{contentId}")
    public ResponseEntity<LicenseResponseDto> addLicense(
            @PathVariable Long contentId,
            @Valid @RequestBody LicenseRequestDto requestDto) {

        LicenseResponseDto response = licenseService.addLicenseToContent(contentId, requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{licenseId}")
    public ResponseEntity<LicenseResponseDto> updateLicense(
            @PathVariable Long licenseId,
            @Valid @RequestBody LicenseRequestDto requestDto) {

        LicenseResponseDto response = licenseService.updateLicense(licenseId, requestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{licenseId}/detach")
    public ResponseEntity<LicenseResponseDto> detachLicense(@PathVariable Long licenseId) {
        LicenseResponseDto response = licenseService.detachLicenseFromContent(licenseId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{licenseId}")
    public ResponseEntity<Void> deleteLicense(@PathVariable Long licenseId) {
        licenseService.deleteLicense(licenseId);
        return ResponseEntity.noContent().build();
    }
}
