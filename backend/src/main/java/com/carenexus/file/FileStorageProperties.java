package com.carenexus.file;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "care-nexus.file-storage")
public class FileStorageProperties {

    private String rootPath = "uploads";
    private long imageMaxSizeBytes = 10485760L;
    private long documentMaxSizeBytes = 52428800L;
    private long videoMaxSizeBytes = 524288000L;
    private List<String> allowedExtensions = Arrays.asList(
            "jpg", "jpeg", "png", "webp", "pdf", "ppt", "pptx", "doc", "docx", "mp4", "webm");
    private List<String> allowedMimePrefixes = Arrays.asList("image/", "application/pdf", "video/");
    private List<String> blockedExtensions = Arrays.asList("exe", "bat", "cmd", "sh", "js", "jsp", "php");
    private Map<String, List<String>> extensionMimeTypes = defaultExtensionMimeTypes();

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public long getImageMaxSizeBytes() {
        return imageMaxSizeBytes;
    }

    public void setImageMaxSizeBytes(long imageMaxSizeBytes) {
        this.imageMaxSizeBytes = imageMaxSizeBytes;
    }

    public long getDocumentMaxSizeBytes() {
        return documentMaxSizeBytes;
    }

    public void setDocumentMaxSizeBytes(long documentMaxSizeBytes) {
        this.documentMaxSizeBytes = documentMaxSizeBytes;
    }

    public long getVideoMaxSizeBytes() {
        return videoMaxSizeBytes;
    }

    public void setVideoMaxSizeBytes(long videoMaxSizeBytes) {
        this.videoMaxSizeBytes = videoMaxSizeBytes;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public List<String> getAllowedMimePrefixes() {
        return allowedMimePrefixes;
    }

    public void setAllowedMimePrefixes(List<String> allowedMimePrefixes) {
        this.allowedMimePrefixes = allowedMimePrefixes;
    }

    public List<String> getBlockedExtensions() {
        return blockedExtensions;
    }

    public void setBlockedExtensions(List<String> blockedExtensions) {
        this.blockedExtensions = blockedExtensions;
    }

    public Map<String, List<String>> getExtensionMimeTypes() {
        return extensionMimeTypes;
    }

    public void setExtensionMimeTypes(Map<String, List<String>> extensionMimeTypes) {
        this.extensionMimeTypes = extensionMimeTypes;
    }

    private static Map<String, List<String>> defaultExtensionMimeTypes() {
        Map<String, List<String>> types = new HashMap<>();
        types.put("jpg", Arrays.asList("image/jpeg"));
        types.put("jpeg", Arrays.asList("image/jpeg"));
        types.put("png", Arrays.asList("image/png"));
        types.put("webp", Arrays.asList("image/webp"));
        types.put("pdf", Arrays.asList("application/pdf"));
        types.put("ppt", Arrays.asList("application/vnd.ms-powerpoint"));
        types.put("pptx", Arrays.asList("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        types.put("doc", Arrays.asList("application/msword"));
        types.put("docx", Arrays.asList("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        types.put("mp4", Arrays.asList("video/mp4"));
        types.put("webm", Arrays.asList("video/webm"));
        return types;
    }
}
