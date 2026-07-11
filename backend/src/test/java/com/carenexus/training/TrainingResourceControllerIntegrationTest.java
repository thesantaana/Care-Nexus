package com.carenexus.training;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carenexus.DoctorMapperTestConfiguration;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.audit.entity.OperationLog;
import com.carenexus.audit.mapper.OperationLogMapper;
import com.carenexus.auth.entity.SysRole;
import com.carenexus.auth.entity.SysUser;
import com.carenexus.auth.mapper.SysPermissionMapper;
import com.carenexus.auth.mapper.SysRoleMapper;
import com.carenexus.auth.mapper.SysUserMapper;
import com.carenexus.file.entity.FileResource;
import com.carenexus.file.mapper.FileResourceMapper;
import com.carenexus.training.constant.EnableStatus;
import com.carenexus.training.constant.TrainingResourceStatus;
import com.carenexus.training.constant.TrainingResourceType;
import com.carenexus.training.constant.TrainingStorageMode;
import com.carenexus.training.entity.TrainingCategory;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.entity.TrainingResourceTag;
import com.carenexus.training.entity.TrainingTag;
import com.carenexus.training.mapper.ExamAnswerMapper;
import com.carenexus.training.mapper.ExamQuestionMapper;
import com.carenexus.training.mapper.ExamQuestionOptionMapper;
import com.carenexus.training.mapper.ExamRecordMapper;
import com.carenexus.training.mapper.LearningAccessLogMapper;
import com.carenexus.training.mapper.LearningRecordMapper;
import com.carenexus.training.mapper.TrainingCategoryMapper;
import com.carenexus.training.mapper.TrainingExamMapper;
import com.carenexus.training.mapper.TrainingExamQuestionMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.mapper.TrainingResourceTagMapper;
import com.carenexus.training.mapper.TrainingTagMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration",
        "care-nexus.jwt.secret=test-demo-secret-for-t014-training-resource-32",
        "care-nexus.jwt.expiration-seconds=3600"
})
@AutoConfigureMockMvc
@Import(DoctorMapperTestConfiguration.class)
class TrainingResourceControllerIntegrationTest {

    private static final String PASSWORD_HASH = "$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u";
    private static final String TRAINER_LOGIN = "{\"username\":\"trainer_demo\",\"password\":\"Demo@123456\"}";
    private static final String CAREGIVER_LOGIN = "{\"username\":\"caregiver_demo\",\"password\":\"Demo@123456\"}";
    private static final String CATEGORY_JSON = "{\"categoryName\":\"鍩虹鎶ょ悊\",\"sortNo\":1}";
    private static final String TAG_JSON = "{\"tagName\":\"鍘嬬柈棰勯槻\"}";
    private static final String TEXT_RESOURCE_JSON = "{\"resourceType\":\"ARTICLE\",\"storageMode\":\"TEXT\","
            + "\"categoryId\":1,\"title\":\"鍘嬬柈棰勯槻\",\"summary\":\"summary\",\"content\":\"content\","
            + "\"durationSeconds\":300,\"tagIds\":[1,1]}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysUserMapper sysUserMapper;

    @MockBean
    private SysRoleMapper sysRoleMapper;

    @MockBean
    private SysPermissionMapper sysPermissionMapper;

    @MockBean
    private OperationLogMapper operationLogMapper;

    @MockBean
    private TrainingCategoryMapper categoryMapper;

    @MockBean
    private TrainingTagMapper tagMapper;

    @MockBean
    private TrainingResourceMapper resourceMapper;

    @MockBean
    private TrainingResourceTagMapper resourceTagMapper;

    @MockBean
    private FileResourceMapper fileResourceMapper;

    @MockBean
    private TrainingExamMapper trainingExamMapper;

    @MockBean
    private ExamQuestionMapper examQuestionMapper;

    @MockBean
    private ExamQuestionOptionMapper examQuestionOptionMapper;

    @MockBean
    private TrainingExamQuestionMapper trainingExamQuestionMapper;

    @MockBean
    private ExamRecordMapper examRecordMapper;

    @MockBean
    private ExamAnswerMapper examAnswerMapper;

    @MockBean
    private LearningRecordMapper learningRecordMapper;

    @MockBean
    private LearningAccessLogMapper learningAccessLogMapper;

    @BeforeEach
    void setUp() {
        when(sysRoleMapper.selectById(3L)).thenReturn(role(3L, "TRAINING_ADMIN", "Training Admin"));
        when(sysRoleMapper.selectById(4L)).thenReturn(role(4L, "CAREGIVER", "Caregiver"));
        when(sysPermissionMapper.selectPermissionCodesByRoleId(3L))
                .thenReturn(Arrays.asList("training:resource:view", "training:resource:manage"));
        when(sysPermissionMapper.selectPermissionCodesByRoleId(4L))
                .thenReturn(Collections.singletonList("training:resource:view"));
        when(sysUserMapper.selectByUsernameIncludingDeleted("trainer_demo"))
                .thenReturn(user(10L, "trainer_demo", 3L));
        when(sysUserMapper.selectByUsernameIncludingDeleted("caregiver_demo"))
                .thenReturn(user(11L, "caregiver_demo", 4L));
        when(sysUserMapper.selectById(10L)).thenReturn(user(10L, "trainer_demo", 3L));
        when(sysUserMapper.selectById(11L)).thenReturn(user(11L, "caregiver_demo", 4L));
        when(categoryMapper.selectById(1L)).thenReturn(category(1L, "鍩虹鎶ょ悊", EnableStatus.ENABLED));
        when(tagMapper.selectById(1L)).thenReturn(tag(1L, "鍘嬬柈棰勯槻", EnableStatus.ENABLED));
        when(tagMapper.selectById(2L)).thenReturn(tag(2L, "瀹夊叏瑙勮寖", EnableStatus.ENABLED));
        when(categoryMapper.selectCount(any())).thenReturn(0L);
        when(tagMapper.selectCount(any())).thenReturn(0L);
        when(categoryMapper.insert(any())).thenAnswer(invocation -> {
            TrainingCategory category = invocation.getArgument(0);
            category.setId(21L);
            return 1;
        });
        when(tagMapper.insert(any())).thenAnswer(invocation -> {
            TrainingTag tag = invocation.getArgument(0);
            tag.setId(22L);
            return 1;
        });
        when(resourceMapper.insert(any())).thenAnswer(invocation -> {
            TrainingResource resource = invocation.getArgument(0);
            resource.setId(31L);
            return 1;
        });
        when(resourceMapper.updateById(any())).thenReturn(1);
        when(categoryMapper.updateById(any())).thenReturn(1);
        when(tagMapper.updateById(any())).thenReturn(1);
        when(resourceTagMapper.deleteByResourceId(any())).thenReturn(1);
        when(resourceTagMapper.insert(any())).thenReturn(1);
        when(resourceTagMapper.selectTagIdsByResourceId(any())).thenReturn(Collections.emptyList());
        when(resourceTagMapper.selectResourceIdsByTagId(1L)).thenReturn(Collections.singletonList(1L));
        when(operationLogMapper.insert(any())).thenReturn(1);
    }

    @Test
    void unauthenticatedQueryReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/training/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void viewPermissionCanQueryEnabledCategories() throws Exception {
        when(categoryMapper.selectList(any())).thenReturn(Collections.singletonList(category(1L, "鍩虹鎶ょ悊",
                EnableStatus.ENABLED)));
        mockMvc.perform(get("/api/v1/training/categories").header("Authorization", bearer(caregiverToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].categoryName").value("鍩虹鎶ょ悊"));
    }

    @Test
    void viewPermissionCannotCreateCategory() throws Exception {
        mockMvc.perform(post("/api/v1/training/categories").header("Authorization", bearer(caregiverToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORY_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCategorySuccess() throws Exception {
        mockMvc.perform(post("/api/v1/training/categories").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORY_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryName").value("鍩虹鎶ょ悊"))
                .andExpect(jsonPath("$.data.status").value(EnableStatus.ENABLED));
    }

    @Test
    void createCategoryWithBlankNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/training/categories").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\" \",\"sortNo\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void duplicateCategoryNameReturnsConflict() throws Exception {
        when(categoryMapper.selectCount(any())).thenReturn(1L);
        mockMvc.perform(post("/api/v1/training/categories").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORY_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateMissingCategoryReturnsNotFound() throws Exception {
        when(categoryMapper.selectById(404L)).thenReturn(null);
        mockMvc.perform(put("/api/v1/training/categories/404").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORY_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategoryStatusRejectsInvalidStatus() throws Exception {
        mockMvc.perform(put("/api/v1/training/categories/1/status").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"BROKEN\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTagSuccessAndDuplicateReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/v1/training/tags").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TAG_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tagName").value("鍘嬬柈棰勯槻"));
        when(tagMapper.selectCount(any())).thenReturn(1L);
        mockMvc.perform(post("/api/v1/training/tags").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TAG_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateMissingTagReturnsNotFoundAndStatusCanSwitch() throws Exception {
        when(tagMapper.selectById(404L)).thenReturn(null);
        mockMvc.perform(put("/api/v1/training/tags/404").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TAG_JSON))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/v1/training/tags/1/status").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DISABLED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(EnableStatus.DISABLED));
    }

    @Test
    void createTextResourceSuccessDeduplicatesTagsAndUsesCurrentUser() throws Exception {
        mockMvc.perform(post("/api/v1/training/resources").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEXT_RESOURCE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(TrainingResourceStatus.DRAFT))
                .andExpect(jsonPath("$.data.createdBy.id").value(10));
        ArgumentCaptor<TrainingResourceTag> captor = ArgumentCaptor.forClass(TrainingResourceTag.class);
        verify(resourceTagMapper).insert(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(Long.valueOf(1L), captor.getValue().getTagId());
    }

    @Test
    void createResourceStorageValidationWorks() throws Exception {
        mockMvc.perform(post("/api/v1/training/resources").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceType\":\"ARTICLE\",\"storageMode\":\"TEXT\",\"categoryId\":1,"
                                + "\"title\":\"title\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/training/resources").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceType\":\"PPT\",\"storageMode\":\"LOCAL_FILE\",\"categoryId\":1,"
                                + "\"title\":\"title\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/training/resources").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceType\":\"VIDEO\",\"storageMode\":\"EXTERNAL_LINK\",\"categoryId\":1,"
                                + "\"title\":\"title\",\"externalUrl\":\"not-url\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLocalFileResourceRequiresActiveFile() throws Exception {
        FileResource file = new FileResource();
        file.setId(8L);
        file.setFileStatus("ACTIVE");
        file.setIsDeleted(0);
        when(fileResourceMapper.selectById(8L)).thenReturn(file);
        mockMvc.perform(post("/api/v1/training/resources").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceType\":\"PPT\",\"storageMode\":\"LOCAL_FILE\",\"categoryId\":1,"
                                + "\"title\":\"title\",\"fileResourceId\":8}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fileResourceId").value(8));
    }

    @Test
    void createResourceRejectsMissingCategoryOrTag() throws Exception {
        when(categoryMapper.selectById(404L)).thenReturn(null);
        mockMvc.perform(post("/api/v1/training/resources").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceType\":\"ARTICLE\",\"storageMode\":\"TEXT\",\"categoryId\":404,"
                                + "\"title\":\"title\",\"content\":\"content\"}"))
                .andExpect(status().isNotFound());
        when(tagMapper.selectById(404L)).thenReturn(null);
        mockMvc.perform(post("/api/v1/training/resources").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceType\":\"ARTICLE\",\"storageMode\":\"TEXT\",\"categoryId\":1,"
                                + "\"title\":\"title\",\"content\":\"content\",\"tagIds\":[404]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDraftResourceSuccessButPublishedUpdateConflicts() throws Exception {
        when(resourceMapper.selectById(1L)).thenReturn(resource(1L, TrainingResourceStatus.DRAFT));
        mockMvc.perform(put("/api/v1/training/resources/1").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEXT_RESOURCE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(TrainingResourceStatus.DRAFT));
        when(resourceMapper.selectById(2L)).thenReturn(resource(2L, TrainingResourceStatus.PUBLISHED));
        mockMvc.perform(put("/api/v1/training/resources/2").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEXT_RESOURCE_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void pageResourcesReturnsStablePageStructure() throws Exception {
        Page<TrainingResource> page = new Page<TrainingResource>(1, 10);
        page.setRecords(Collections.singletonList(resource(1L, TrainingResourceStatus.PUBLISHED)));
        page.setTotal(1);
        when(resourceMapper.selectPage(any(), any())).thenReturn(page);
        mockMvc.perform(get("/api/v1/training/resources")
                        .param("keyword", "鍘嬬柈")
                        .param("resourceType", "ARTICLE")
                        .param("categoryId", "1")
                        .param("tagId", "1")
                        .header("Authorization", bearer(caregiverToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].title").value("鍘嬬柈棰勯槻"))
                .andExpect(jsonPath("$.data.pageNo").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void viewUserCannotReadDraftOrOfflineDetail() throws Exception {
        when(resourceMapper.selectById(1L)).thenReturn(resource(1L, TrainingResourceStatus.OFFLINE));
        mockMvc.perform(get("/api/v1/training/resources/1").header("Authorization", bearer(caregiverToken())))
                .andExpect(status().isNotFound());
    }

    @Test
    void publishDraftSetsPublishedAtAndWritesOperationLog() throws Exception {
        when(resourceMapper.selectById(1L)).thenReturn(resource(1L, TrainingResourceStatus.DRAFT));
        mockMvc.perform(put("/api/v1/training/resources/1/publish").header("Authorization", bearer(trainerToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(TrainingResourceStatus.PUBLISHED))
                .andExpect(jsonPath("$.data.publishedAt").exists());
        verify(operationLogMapper, atLeastOnce()).insert(any(OperationLog.class));
    }

    @Test
    void repeatPublishReturnsConflict() throws Exception {
        when(resourceMapper.selectById(1L)).thenReturn(resource(1L, TrainingResourceStatus.PUBLISHED));
        mockMvc.perform(put("/api/v1/training/resources/1/publish").header("Authorization", bearer(trainerToken())))
                .andExpect(status().isConflict());
    }

    @Test
    void offlinePublishedResourceAndRejectRepeatOffline() throws Exception {
        when(resourceMapper.selectById(1L)).thenReturn(resource(1L, TrainingResourceStatus.PUBLISHED));
        mockMvc.perform(put("/api/v1/training/resources/1/offline").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"expired\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(TrainingResourceStatus.OFFLINE));
        verify(operationLogMapper, atLeastOnce()).insert(any(OperationLog.class));
        when(resourceMapper.selectById(2L)).thenReturn(resource(2L, TrainingResourceStatus.OFFLINE));
        mockMvc.perform(put("/api/v1/training/resources/2/offline").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"expired\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void t012AuthEndpointsStillWorkInTrainingContext() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", bearer(trainerToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.permissionCodes[0]").exists());
    }

    private String trainerToken() throws Exception {
        return loginToken(TRAINER_LOGIN);
    }

    private String caregiverToken() throws Exception {
        return loginToken(CAREGIVER_LOGIN);
    }

    private String loginToken(String body) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return response.substring(response.indexOf("\"token\":\"") + 9, response.indexOf("\",\"tokenType\""));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private SysUser user(Long id, String username, Long roleId) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setRealName(username);
        user.setPasswordHash(PASSWORD_HASH);
        user.setMainRoleId(roleId);
        user.setAccountStatus("NORMAL");
        user.setIsDeleted(0);
        return user;
    }

    private SysRole role(Long id, String code, String name) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setRoleCode(code);
        role.setRoleName(name);
        role.setRoleStatus("ENABLED");
        role.setIsDeleted(0);
        return role;
    }

    private TrainingCategory category(Long id, String name, String status) {
        TrainingCategory category = new TrainingCategory();
        category.setId(id);
        category.setCategoryName(name);
        category.setSortNo(1);
        category.setCategoryStatus(status);
        category.setIsDeleted(0);
        return category;
    }

    private TrainingTag tag(Long id, String name, String status) {
        TrainingTag tag = new TrainingTag();
        tag.setId(id);
        tag.setTagName(name);
        tag.setTagStatus(status);
        tag.setIsDeleted(0);
        return tag;
    }

    private TrainingResource resource(Long id, String status) {
        TrainingResource resource = new TrainingResource();
        resource.setId(id);
        resource.setResourceType(TrainingResourceType.ARTICLE);
        resource.setStorageMode(TrainingStorageMode.TEXT);
        resource.setCategoryId(1L);
        resource.setTitle("鍘嬬柈棰勯槻");
        resource.setSummary("summary");
        resource.setContent("content");
        resource.setDurationSeconds(300);
        resource.setResourceStatus(status);
        resource.setPublishedAt(TrainingResourceStatus.PUBLISHED.equals(status) ? LocalDateTime.now() : null);
        resource.setCreatedBy(10L);
        resource.setUpdatedBy(10L);
        resource.setIsDeleted(0);
        return resource;
    }
}
