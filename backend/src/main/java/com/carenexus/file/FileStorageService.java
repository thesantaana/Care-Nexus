package com.carenexus.file;

import java.io.IOException;
import java.io.InputStream;

public interface FileStorageService {

    StoredFile store(String originalFilename, String contentType, InputStream inputStream, long size) throws IOException;
}
