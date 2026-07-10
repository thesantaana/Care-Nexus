package com.carenexus.file.service;

import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.file.ActiveFileResource;
import com.carenexus.file.FileResourceService;
import com.carenexus.file.entity.FileResource;
import com.carenexus.file.mapper.FileResourceMapper;
import org.springframework.stereotype.Service;

@Service
public class FileResourceServiceImpl implements FileResourceService {

    private static final String ACTIVE = "ACTIVE";

    private final FileResourceMapper fileResourceMapper;

    public FileResourceServiceImpl(FileResourceMapper fileResourceMapper) {
        this.fileResourceMapper = fileResourceMapper;
    }

    @Override
    public ActiveFileResource getRequiredActiveFile(Long fileResourceId) {
        FileResource fileResource = fileResourceMapper.selectById(fileResourceId);
        if (fileResource == null || !ACTIVE.equals(fileResource.getFileStatus())
                || Integer.valueOf(1).equals(fileResource.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "File resource not found");
        }
        return new ActiveFileResource(fileResource.getId(), fileResource.getFileType(), fileResource.getFileSize(),
                fileResource.getRelativePath());
    }
}
