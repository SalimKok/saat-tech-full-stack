package com.saattech.service.implementation;

import com.saattech.dto.license.LicenseRequestDto;
import com.saattech.dto.license.LicenseResponseDto;
import com.saattech.entity.Content;
import com.saattech.entity.License;
import com.saattech.enums.ContentStatus;
import com.saattech.enums.LicenseStatus;
import com.saattech.event.ContentSavedEvent;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.mapper.LicenseMapper;
import com.saattech.repository.ContentRepository;
import com.saattech.repository.LicenseRepository;
import com.saattech.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {
    private final LicenseRepository licenseRepository;
    private final ContentRepository contentRepository;
    private final LicenseMapper licenseMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public LicenseResponseDto addLicenseToContent(Long contentId, LicenseRequestDto requestDto) {
        Content content = contentRepository.findByIdAndStatusNot(contentId, ContentStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + contentId));
        License license = licenseMapper.toEntity(requestDto);
        license.setContent(content);
        License savedLicense = licenseRepository.save(license);
        return licenseMapper.toDto(savedLicense);
    }

    @Override
    @Transactional
    public LicenseResponseDto updateLicense(Long licenseId, LicenseRequestDto requestDto) {
        License existingLicense = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new ResourceNotFoundException("License not found! ID: " + licenseId));
        if (requestDto.getName() != null) existingLicense.setName(requestDto.getName());
        if (requestDto.getStartDate() != null) existingLicense.setStartDate(requestDto.getStartDate());
        if (requestDto.getEndDate() != null) existingLicense.setEndDate(requestDto.getEndDate());
        if (requestDto.getStatus() != null) existingLicense.setStatus(requestDto.getStatus());
        License updatedLicense = licenseRepository.save(existingLicense);
        return licenseMapper.toDto(updatedLicense);
    }

    @Override
    @Transactional
    public LicenseResponseDto detachLicenseFromContent(Long licenseId) {
        License existingLicense = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new ResourceNotFoundException("License not found! ID: " + licenseId));

        Content affectedContent = existingLicense.getContent();
        existingLicense.setContent(null);

        License detachedLicense = licenseRepository.save(existingLicense);
        checkAndUnpublishContentIfNoLicenses(affectedContent, existingLicense);

        return licenseMapper.toDto(detachedLicense);
    }

    @Override
    @Transactional
    public void deleteLicense(Long licenseId) {
        License existingLicense = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new ResourceNotFoundException("License not found! ID: " + licenseId));

        existingLicense.setStatus(LicenseStatus.DELETED);
        Content affectedContent = existingLicense.getContent();

        existingLicense.setContent(null);
        licenseRepository.save(existingLicense);

        checkAndUnpublishContentIfNoLicenses(affectedContent, existingLicense);
    }

    @Override
    @Transactional
    public void processExpiredLicenses() {
        List<License> expiredLicenses = licenseRepository.findByStatusAndEndDateBefore(
                LicenseStatus.ACTIVE, LocalDate.now());
        if (expiredLicenses.isEmpty()) {
            return;
        }

        Set<Content> affectedContents = new HashSet<>();
        for (License license : expiredLicenses) {
            license.setStatus(LicenseStatus.EXPIRED);

            if (license.getContent() != null) {
                affectedContents.add(license.getContent());
                license.setContent(null);
            }
        }
        licenseRepository.saveAll(expiredLicenses);

        for (Content content : affectedContents) {
            boolean hasActiveLicense = false;

            if (content.getLicenses() != null && !content.getLicenses().isEmpty()) {
                hasActiveLicense = content.getLicenses().stream()
                        .anyMatch(l -> l.getStatus() == LicenseStatus.ACTIVE && !expiredLicenses.contains(l));
            }

            if (!hasActiveLicense && content.getStatus() == ContentStatus.PUBLISHED) {
                content.setStatus(ContentStatus.NO_ACTIVE_LICENSE);
                contentRepository.save(content);
                applicationEventPublisher.publishEvent(new ContentSavedEvent(this, content));
            }
        }
    }

    private void checkAndUnpublishContentIfNoLicenses(Content content, License removedLicense) {
        if (content == null || content.getStatus() != ContentStatus.PUBLISHED) {
            return;
        }
        boolean hasActiveLicense = false;
        if (content.getLicenses() != null && !content.getLicenses().isEmpty()) {
            hasActiveLicense = content.getLicenses().stream()
                    .anyMatch(l -> l.getStatus() == LicenseStatus.ACTIVE && !l.getId().equals(removedLicense.getId()));
        }
        if (!hasActiveLicense) {
            content.setStatus(ContentStatus.NO_ACTIVE_LICENSE);
            contentRepository.save(content);
            applicationEventPublisher.publishEvent(new ContentSavedEvent(this, content));
        }
    }
}

