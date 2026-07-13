package com.carenexus.training.controller;

import com.carenexus.common.response.ApiResponse;
import com.carenexus.file.FileStorageService;
import com.carenexus.file.StoredFile;
import com.carenexus.training.dto.TrainingNoteRequest;
import com.carenexus.training.service.TrainingNoteService;
import com.carenexus.training.vo.TrainingNoteResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/training/notes")
public class TrainingNoteController {
    private final TrainingNoteService noteService;
    private final FileStorageService fileStorageService;

    public TrainingNoteController(TrainingNoteService noteService,
            FileStorageService fileStorageService) {
        this.noteService = noteService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ApiResponse<List<TrainingNoteResponse>> mine() {
        return ApiResponse.success(noteService.listMine());
    }

    @GetMapping("/resource/{resourceId}")
    public ApiResponse<TrainingNoteResponse> get(@PathVariable Long resourceId) {
        return ApiResponse.success(noteService.getByResource(resourceId));
    }

    @PutMapping("/resource/{resourceId}")
    public ApiResponse<TrainingNoteResponse> save(@PathVariable Long resourceId,
            @Valid @RequestBody TrainingNoteRequest request) {
        return ApiResponse.success(noteService.save(resourceId, request));
    }

    @PostMapping("/images")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file)
            throws IOException {
        noteService.requireAccess();
        try (InputStream input = file.getInputStream()) {
            StoredFile stored = fileStorageService.store(file.getOriginalFilename(), file.getContentType(),
                    input, file.getSize());
            return ApiResponse.success(Collections.singletonMap("url", "/note-media/" + stored.getStorageName()));
        }
    }
}
