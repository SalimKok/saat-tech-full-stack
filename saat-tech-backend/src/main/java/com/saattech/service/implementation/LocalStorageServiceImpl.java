package com.saattech.service.implementation;

import com.saattech.config.properties.StorageProperties;
import com.saattech.constant.exception.StorageExceptionMessages;
import com.saattech.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageServiceImpl implements StorageService {

    private final StorageProperties storageProperties;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(storageProperties.getUploadDir());
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(StorageExceptionMessages.INIT_FAILED, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException(StorageExceptionMessages.EMPTY_FILE);
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                throw new RuntimeException(StorageExceptionMessages.SECURITY_VIOLATION);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }

            java.util.List<String> allowedExtensions = java.util.Arrays.asList(".jpg", ".jpeg", ".png", ".webp", ".mp4", ".webm");
            if (!allowedExtensions.contains(extension)) {
                throw new RuntimeException(StorageExceptionMessages.UNSUPPORTED_FORMAT);
            }

            String newFilename = UUID.randomUUID().toString() + extension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();
            Files.copy(file.getInputStream(), destinationFile);
            return storageProperties.getUrlPrefix() + "/" + newFilename;

        } catch (IOException e) {
            throw new RuntimeException(StorageExceptionMessages.STORE_FAILED, e);
        }
    }

    @Override
    public void delete(String fileUrl) {
    }
}
