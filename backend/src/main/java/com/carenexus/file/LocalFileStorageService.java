package com.carenexus.file;

import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final FileStorageProperties properties;

    public LocalFileStorageService(FileStorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public StoredFile store(String originalFilename, String contentType, InputStream inputStream, long size)
            throws IOException {
        validate(originalFilename, contentType, size);
        Path root = Paths.get(properties.getRootPath()).toAbsolutePath().normalize();
        Files.createDirectories(root);
        String extension = getExtension(originalFilename);
        String storageName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = root.resolve(storageName).normalize();
        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        return new StoredFile(originalFilename, storageName, storageName, size);
    }

    private void validate(String originalFilename, String contentType, long size) {
        if (!StringUtils.hasText(originalFilename) || originalFilename.contains("..")
                || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid file name");
        }
        if (size <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "File must not be empty");
        }
        if (size > resolveMaxSize(contentType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "File size exceeds limit");
        }
        String extension = getExtension(originalFilename).replace(".", "").toLowerCase(Locale.ROOT);
        if (!StringUtils.hasText(extension)
                || properties.getBlockedExtensions().contains(extension)
                || !properties.getAllowedExtensions().contains(extension)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "File extension is not allowed");
        }
        if (!StringUtils.hasText(contentType) || !isAllowedMimeForExtension(extension, contentType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "File MIME type does not match extension");
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private boolean isAllowedMimeForExtension(String extension, String contentType) {
        String normalized = contentType.split(";")[0].trim().toLowerCase(Locale.ROOT);
        List<String> allowedTypes = properties.getExtensionMimeTypes().get(extension);
        if (allowedTypes == null) {
            return false;
        }
        for (String allowed : allowedTypes) {
            if (normalized.equals(allowed.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private long resolveMaxSize(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return properties.getDocumentMaxSizeBytes();
        }
        String normalized = contentType.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("image/")) {
            return properties.getImageMaxSizeBytes();
        }
        if (normalized.startsWith("video/")) {
            return properties.getVideoMaxSizeBytes();
        }
        return properties.getDocumentMaxSizeBytes();
    }
}
