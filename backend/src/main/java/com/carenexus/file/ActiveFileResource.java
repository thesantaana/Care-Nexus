package com.carenexus.file;

public class ActiveFileResource {

    private final Long id;
    private final String fileType;
    private final Long fileSize;
    private final String relativePath;

    public ActiveFileResource(Long id, String fileType, Long fileSize, String relativePath) {
        this.id = id;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.relativePath = relativePath;
    }

    public Long getId() {
        return id;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getRelativePath() {
        return relativePath;
    }
}
