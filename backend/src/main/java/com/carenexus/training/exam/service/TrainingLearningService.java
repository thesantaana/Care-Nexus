package com.carenexus.training.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.auth.CurrentUser;
import com.carenexus.training.constant.LearningStatus;
import com.carenexus.training.constant.TrainingResourceStatus;
import com.carenexus.training.dto.LearningAccessRequest;
import com.carenexus.training.dto.FavoriteUpdateRequest;
import com.carenexus.training.entity.LearningAccessLog;
import com.carenexus.training.entity.LearningRecord;
import com.carenexus.training.entity.LearningResourceFavorite;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.LearningAccessLogMapper;
import com.carenexus.training.mapper.LearningRecordMapper;
import com.carenexus.training.mapper.LearningResourceFavoriteMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.LearningAccessResponse;
import com.carenexus.training.vo.LearningRecordResponse;
import com.carenexus.training.vo.CourseLearningStatusResponse;
import com.carenexus.training.vo.CourseFavoriteResponse;
import com.carenexus.training.vo.LearningLibraryResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingLearningService {

    private static final int ACTIVE_FLAG = 0;
    private static final String DEFAULT_SCOPE = "MVP";
    private static final int REQUIRED_LEARNING_SECONDS = 1800;

    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingExamSupport support;
    private final LearningRecordMapper learningRecordMapper;
    private final LearningAccessLogMapper learningAccessLogMapper;
    private final LearningResourceFavoriteMapper favoriteMapper;
    private final TrainingResourceMapper resourceMapper;

    public TrainingLearningService(TrainingResourceAccessPolicy accessPolicy, TrainingExamSupport support,
            LearningRecordMapper learningRecordMapper, LearningAccessLogMapper learningAccessLogMapper,
            LearningResourceFavoriteMapper favoriteMapper, TrainingResourceMapper resourceMapper) {
        this.accessPolicy = accessPolicy;
        this.support = support;
        this.learningRecordMapper = learningRecordMapper;
        this.learningAccessLogMapper = learningAccessLogMapper;
        this.favoriteMapper = favoriteMapper;
        this.resourceMapper = resourceMapper;
    }

    @Transactional
    public LearningAccessResponse recordLearningAccess(LearningAccessRequest request) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        TrainingResource resource = support.requirePublishedResource(request.getResourceId());
        LearningAccessLog log = new LearningAccessLog();
        log.setUserId(currentUser.getUserId());
        log.setResourceId(resource.getId());
        log.setAccessSeconds(request.getAccessSeconds());
        log.setAccessedAt(LocalDateTime.now());
        learningAccessLogMapper.insert(log);

        LearningRecord record = getOrCreateLearningRecord(currentUser.getUserId());
        record.setTotalLearningSeconds(safeInt(record.getTotalLearningSeconds()) + request.getAccessSeconds());
        record.setLatestLearningTime(log.getAccessedAt());
        if (LearningStatus.NOT_STARTED.equals(record.getTrainingStatus())) {
            record.setTrainingStatus(LearningStatus.LEARNING);
        }
        learningRecordMapper.updateById(record);
        return toLearningAccessResponse(resource.getId(), request.getAccessSeconds(), record);
    }

    public LearningRecordResponse myLearningRecord() {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        return toLearningRecordResponse(getOrCreateLearningRecord(currentUser.getUserId()));
    }

    public CourseLearningStatusResponse myCourseLearningStatus(Long resourceId) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        support.requirePublishedResource(resourceId);
        int learnedSeconds = courseLearningSeconds(currentUser.getUserId(), resourceId);
        CourseLearningStatusResponse response = new CourseLearningStatusResponse();
        response.setResourceId(resourceId);
        response.setLearnedSeconds(learnedSeconds);
        response.setRequiredSeconds(REQUIRED_LEARNING_SECONDS);
        response.setCompleted(learnedSeconds >= REQUIRED_LEARNING_SECONDS);
        return response;
    }

    public boolean isCourseCompleted(Long userId, Long resourceId) {
        return courseLearningSeconds(userId, resourceId) >= REQUIRED_LEARNING_SECONDS;
    }

    public LearningLibraryResponse myLearningLibrary() {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        Long userId = currentUser.getUserId();
        Set<Long> publishedIds = publishedResources().stream().map(TrainingResource::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<LearningAccessLog> logs = learningAccessLogMapper.selectList(new QueryWrapper<LearningAccessLog>()
                .eq("user_id", userId).orderByDesc("accessed_at"));
        Set<Long> courses = new LinkedHashSet<>();
        java.util.Map<Long, Integer> learnedSeconds = new java.util.LinkedHashMap<>();
        for (LearningAccessLog log : logs) {
            if (publishedIds.contains(log.getResourceId())) {
                courses.add(log.getResourceId());
                learnedSeconds.merge(log.getResourceId(), safeInt(log.getAccessSeconds()), Integer::sum);
            }
        }
        List<Long> completed = learnedSeconds.entrySet().stream()
                .filter(entry -> entry.getValue() >= REQUIRED_LEARNING_SECONDS)
                .map(java.util.Map.Entry::getKey).collect(Collectors.toList());
        List<Long> favorites = favoriteMapper.selectList(new QueryWrapper<LearningResourceFavorite>()
                .eq("user_id", userId).orderByDesc("created_at")).stream()
                .map(LearningResourceFavorite::getResourceId).filter(publishedIds::contains)
                .collect(Collectors.toList());
        LearningLibraryResponse response = new LearningLibraryResponse();
        response.setCourseResourceIds(new java.util.ArrayList<>(courses));
        response.setCompletedResourceIds(completed);
        response.setFavoriteResourceIds(favorites);
        return response;
    }

    @Transactional
    public CourseFavoriteResponse updateFavorite(Long resourceId, FavoriteUpdateRequest request) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        support.requirePublishedResource(resourceId);
        QueryWrapper<LearningResourceFavorite> wrapper = new QueryWrapper<LearningResourceFavorite>()
                .eq("user_id", currentUser.getUserId()).eq("resource_id", resourceId);
        LearningResourceFavorite existing = favoriteMapper.selectOne(wrapper);
        if (Boolean.TRUE.equals(request.getFavorite()) && existing == null) {
            LearningResourceFavorite favorite = new LearningResourceFavorite();
            favorite.setUserId(currentUser.getUserId());
            favorite.setResourceId(resourceId);
            favoriteMapper.insert(favorite);
        } else if (Boolean.FALSE.equals(request.getFavorite()) && existing != null) {
            favoriteMapper.deleteById(existing.getId());
        }
        CourseFavoriteResponse response = new CourseFavoriteResponse();
        response.setResourceId(resourceId);
        response.setFavorite(request.getFavorite());
        return response;
    }

    public LearningRecord getOrCreateLearningRecord(Long userId) {
        QueryWrapper<LearningRecord> wrapper = new QueryWrapper<LearningRecord>()
                .eq("user_id", userId)
                .eq("training_scope", DEFAULT_SCOPE);
        LearningRecord record = learningRecordMapper.selectOne(wrapper);
        if (record != null) {
            return record;
        }
        LearningRecord created = new LearningRecord();
        created.setUserId(userId);
        created.setTrainingScope(DEFAULT_SCOPE);
        created.setTotalLearningSeconds(0);
        created.setTrainingStatus(LearningStatus.NOT_STARTED);
        learningRecordMapper.insert(created);
        return created;
    }

    public LearningRecordResponse toLearningRecordResponse(LearningRecord record) {
        int requiredResources = publishedResources().size();
        int visitedResources = visitedPublishedResourceCount(record.getUserId());
        boolean examAllowed = safeInt(record.getTotalLearningSeconds()) >= REQUIRED_LEARNING_SECONDS
                && requiredResources > 0 && visitedResources >= requiredResources;
        LearningRecordResponse response = new LearningRecordResponse();
        response.setUserId(record.getUserId());
        response.setTrainingScope(record.getTrainingScope());
        response.setTotalLearningSeconds(safeInt(record.getTotalLearningSeconds()));
        response.setLatestLearningTime(record.getLatestLearningTime());
        response.setTrainingStatus(record.getTrainingStatus());
        response.setVisitedResourceCount(visitedResources);
        response.setRequiredResourceCount(requiredResources);
        response.setRequiredLearningSeconds(REQUIRED_LEARNING_SECONDS);
        response.setExamAllowed(examAllowed);
        return response;
    }

    private LearningAccessResponse toLearningAccessResponse(Long resourceId, Integer accessSeconds,
            LearningRecord record) {
        LearningAccessResponse response = new LearningAccessResponse();
        response.setResourceId(resourceId);
        response.setAccessSeconds(accessSeconds);
        response.setTotalLearningSeconds(record.getTotalLearningSeconds());
        response.setTrainingStatus(record.getTrainingStatus());
        response.setLatestLearningTime(record.getLatestLearningTime());
        return response;
    }

    private int visitedPublishedResourceCount(Long userId) {
        Set<Long> publishedIds = publishedResources().stream().map(TrainingResource::getId).collect(Collectors.toSet());
        if (publishedIds.isEmpty()) {
            return 0;
        }
        List<LearningAccessLog> logs = learningAccessLogMapper.selectList(new QueryWrapper<LearningAccessLog>()
                .eq("user_id", userId));
        Set<Long> visited = new LinkedHashSet<>();
        for (LearningAccessLog log : logs) {
            if (publishedIds.contains(log.getResourceId())) {
                visited.add(log.getResourceId());
            }
        }
        return visited.size();
    }

    private int courseLearningSeconds(Long userId, Long resourceId) {
        List<LearningAccessLog> logs = learningAccessLogMapper.selectList(new QueryWrapper<LearningAccessLog>()
                .eq("user_id", userId)
                .eq("resource_id", resourceId));
        int total = 0;
        for (LearningAccessLog log : logs) {
            total += safeInt(log.getAccessSeconds());
        }
        return total;
    }

    private List<TrainingResource> publishedResources() {
        return resourceMapper.selectList(new QueryWrapper<TrainingResource>()
                .eq("is_deleted", ACTIVE_FLAG)
                .eq("resource_status", TrainingResourceStatus.PUBLISHED));
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
