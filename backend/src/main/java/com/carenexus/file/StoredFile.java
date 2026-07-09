package com.carenexus.file;

public class StoredFile {

    private String originalName;
    private String storageName;
    private String relativePath;
    private long size;

    public StoredFile(String originalName, String storageName, String relativePath, long size) {
        this.originalName = originalName;
        this.storageName = storageName;
        this.relativePath = relativePath;
        this.size = size;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getStorageName() {
        return storageName;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public long getSize() {
        return size;
    }
}
