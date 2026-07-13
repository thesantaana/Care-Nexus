package com.carenexus.training.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.dto.TrainingNoteRequest;
import com.carenexus.training.entity.TrainingNote;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.TrainingNoteMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.TrainingNoteResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingNoteService {
    private final CurrentUserService currentUserService;
    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingNoteMapper noteMapper;
    private final TrainingResourceMapper resourceMapper;

    public TrainingNoteService(CurrentUserService currentUserService,
            TrainingResourceAccessPolicy accessPolicy, TrainingNoteMapper noteMapper,
            TrainingResourceMapper resourceMapper) {
        this.currentUserService = currentUserService;
        this.accessPolicy = accessPolicy;
        this.noteMapper = noteMapper;
        this.resourceMapper = resourceMapper;
    }

    public List<TrainingNoteResponse> listMine() {
        Long userId = currentUser().getUserId();
        return noteMapper.selectList(new QueryWrapper<TrainingNote>()
                        .eq("user_id", userId).orderByDesc("updated_at"))
                .stream().map(this::response).collect(Collectors.toList());
    }

    public void requireAccess() {
        currentUser();
    }

    public TrainingNoteResponse getByResource(Long resourceId) {
        Long userId = currentUser().getUserId();
        TrainingNote note = find(userId, resourceId);
        return note == null ? null : response(note);
    }

    @Transactional
    public TrainingNoteResponse save(Long resourceId, TrainingNoteRequest request) {
        CurrentUser user = currentUser();
        requireResource(resourceId);
        TrainingNote note = find(user.getUserId(), resourceId);
        LocalDateTime now = LocalDateTime.now();
        if (note == null) {
            note = new TrainingNote();
            note.setUserId(user.getUserId());
            note.setResourceId(resourceId);
            note.setCreatedAt(now);
        }
        note.setNoteTitle(request.getTitle().trim());
        note.setNoteContent(request.getContent() == null ? "" : request.getContent().trim());
        note.setUpdatedAt(now);
        if (note.getId() == null) {
            noteMapper.insert(note);
        } else {
            noteMapper.updateById(note);
        }
        return response(note);
    }

    private CurrentUser currentUser() {
        accessPolicy.requireViewOrManage();
        return currentUserService.getCurrentUser()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required"));
    }

    private TrainingNote find(Long userId, Long resourceId) {
        return noteMapper.selectOne(new QueryWrapper<TrainingNote>()
                .eq("user_id", userId).eq("resource_id", resourceId));
    }

    private TrainingResource requireResource(Long resourceId) {
        TrainingResource resource = resourceMapper.selectById(resourceId);
        if (resource == null || Integer.valueOf(1).equals(resource.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
        return resource;
    }

    private TrainingNoteResponse response(TrainingNote note) {
        TrainingNoteResponse response = new TrainingNoteResponse();
        response.id = note.getId();
        response.resourceId = note.getResourceId();
        TrainingResource resource = resourceMapper.selectById(note.getResourceId());
        response.resourceTitle = resource == null ? "已下架培训资源" : resource.getTitle();
        response.title = note.getNoteTitle();
        response.content = note.getNoteContent();
        response.updatedAt = note.getUpdatedAt();
        return response;
    }
}
