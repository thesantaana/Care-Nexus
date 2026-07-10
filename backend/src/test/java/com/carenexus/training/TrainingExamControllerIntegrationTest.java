package com.carenexus.training;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carenexus.audit.mapper.OperationLogMapper;
import com.carenexus.auth.entity.SysRole;
import com.carenexus.auth.entity.SysUser;
import com.carenexus.auth.mapper.SysPermissionMapper;
import com.carenexus.auth.mapper.SysRoleMapper;
import com.carenexus.auth.mapper.SysUserMapper;
import com.carenexus.file.mapper.FileResourceMapper;
import com.carenexus.training.constant.EnableStatus;
import com.carenexus.training.constant.ExamPassStatus;
import com.carenexus.training.constant.ExamQuestionType;
import com.carenexus.training.constant.ExamStatus;
import com.carenexus.training.constant.LearningStatus;
import com.carenexus.training.constant.TrainingResourceStatus;
import com.carenexus.training.constant.TrainingResourceType;
import com.carenexus.training.constant.TrainingStorageMode;
import com.carenexus.training.entity.ExamQuestion;
import com.carenexus.training.entity.ExamQuestionOption;
import com.carenexus.training.entity.ExamRecord;
import com.carenexus.training.entity.LearningAccessLog;
import com.carenexus.training.entity.LearningRecord;
import com.carenexus.training.entity.TrainingExam;
import com.carenexus.training.entity.TrainingExamQuestion;
import com.carenexus.training.entity.TrainingResource;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration",
        "care-nexus.jwt.secret=test-demo-secret-for-t016-exam-learning-32",
        "care-nexus.jwt.expiration-seconds=3600"
})
@AutoConfigureMockMvc
class TrainingExamControllerIntegrationTest {

    private static final String PASSWORD_HASH = "$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u";
    private static final String TRAINER_LOGIN = "{\"username\":\"trainer_demo\",\"password\":\"Demo@123456\"}";
    private static final String CAREGIVER_LOGIN = "{\"username\":\"caregiver_demo\",\"password\":\"Demo@123456\"}";
    private static final String EXAM_JSON = "{\"examName\":\"Caregiver MVP Exam\","
            + "\"passScore\":48,\"maxAttempts\":3}";
    private static final String SINGLE_QUESTION_JSON = "{\"resourceId\":1,\"questionType\":\"SINGLE_CHOICE\","
            + "\"questionContent\":\"Which action helps prevent pressure injury?\",\"standardAnswer\":\"A\","
            + "\"analysis\":\"Turn and check skin regularly.\"}";

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
    private TrainingExamMapper examMapper;
    @MockBean
    private ExamQuestionMapper questionMapper;
    @MockBean
    private ExamQuestionOptionMapper optionMapper;
    @MockBean
    private TrainingExamQuestionMapper examQuestionMapper;
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
        when(resourceMapper.selectById(1L)).thenReturn(resource(1L, TrainingResourceStatus.PUBLISHED));
        when(resourceMapper.selectList(any())).thenReturn(Collections.singletonList(resource(1L,
                TrainingResourceStatus.PUBLISHED)));
        when(examMapper.insert(any())).thenAnswer(invocation -> {
            TrainingExam exam = invocation.getArgument(0);
            exam.setId(21L);
            return 1;
        });
        when(questionMapper.insert(any())).thenAnswer(invocation -> {
            ExamQuestion question = invocation.getArgument(0);
            question.setId(31L);
            return 1;
        });
        when(optionMapper.insert(any())).thenReturn(1);
        when(examRecordMapper.insert(any())).thenAnswer(invocation -> {
            ExamRecord record = invocation.getArgument(0);
            record.setId(51L);
            return 1;
        });
        when(examMapper.selectCount(any())).thenReturn(0L);
        when(examMapper.updateById(any())).thenReturn(1);
        when(questionMapper.updateById(any())).thenReturn(1);
        when(examQuestionMapper.insert(any())).thenReturn(1);
        when(examQuestionMapper.deleteByExamId(any())).thenReturn(1);
        when(examAnswerMapper.insert(any())).thenReturn(1);
        when(examRecordMapper.updateById(any())).thenReturn(1);
        when(learningRecordMapper.updateById(any())).thenReturn(1);
        when(learningAccessLogMapper.insert(any())).thenReturn(1);
        when(operationLogMapper.insert(any())).thenReturn(1);
    }

    @Test
    void viewPermissionCannotCreateExam() throws Exception {
        mockMvc.perform(post("/api/v1/training/exams").header("Authorization", bearer(caregiverToken()))
                        .contentType(MediaType.APPLICATION_JSON).content(EXAM_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void trainingAdminCanCreateExamAndObjectiveQuestion() throws Exception {
        mockMvc.perform(post("/api/v1/training/exams").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON).content(EXAM_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(ExamStatus.DRAFT));
        mockMvc.perform(post("/api/v1/training/questions").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON).content(SINGLE_QUESTION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionType").value(ExamQuestionType.SINGLE_CHOICE));
    }

    @Test
    void shortAnswerQuestionIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/training/questions").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceId\":1,\"questionType\":\"SHORT_ANSWER\","
                                + "\"questionContent\":\"Explain.\",\"standardAnswer\":\"Answer\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void singleChoiceOptionsMustHaveExactlyOneCorrectAnswer() throws Exception {
        when(questionMapper.selectById(31L)).thenReturn(question(31L, ExamQuestionType.SINGLE_CHOICE,
                ExamStatus.PUBLISHED));
        mockMvc.perform(put("/api/v1/training/questions/31/options").header("Authorization", bearer(trainerToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"options\":[{\"optionLabel\":\"A\",\"optionContent\":\"A\",\"correct\":true},"
                                + "{\"optionLabel\":\"B\",\"optionContent\":\"B\",\"correct\":true}]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void learningAccessUpdatesTotalLearningStatus() throws Exception {
        when(learningRecordMapper.selectOne(any())).thenReturn(learningRecord(11L, 0, LearningStatus.NOT_STARTED));
        mockMvc.perform(post("/api/v1/training/learning/access").header("Authorization", bearer(caregiverToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceId\":1,\"accessSeconds\":600}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalLearningSeconds").value(600))
                .andExpect(jsonPath("$.data.trainingStatus").value(LearningStatus.LEARNING));
    }

    @Test
    void submitExamRejectsWhenLearningRequirementNotMet() throws Exception {
        when(examMapper.selectById(21L)).thenReturn(exam(21L, ExamStatus.PUBLISHED));
        when(learningRecordMapper.selectOne(any())).thenReturn(learningRecord(11L, 60, LearningStatus.LEARNING));
        mockMvc.perform(post("/api/v1/training/exams/21/records").header("Authorization", bearer(caregiverToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answers\":[{\"questionId\":31,\"answer\":\"A\"}]}"))
                .andExpect(status().isConflict());
    }

    @Test
    void submitObjectiveExamScoresImmediatelyAndMarksPassed() throws Exception {
        when(examMapper.selectById(21L)).thenReturn(exam(21L, ExamStatus.PUBLISHED));
        when(learningRecordMapper.selectOne(any())).thenReturn(learningRecord(11L, 1800, LearningStatus.LEARNING));
        when(learningAccessLogMapper.selectList(any())).thenReturn(Collections.singletonList(accessLog(11L, 1L)));
        when(examRecordMapper.selectMaxAttemptNo(11L, 21L)).thenReturn(0);
        when(examQuestionMapper.selectByExamId(21L)).thenReturn(Collections.singletonList(examQuestion(21L, 31L)));
        when(questionMapper.selectById(31L)).thenReturn(question(31L, ExamQuestionType.SINGLE_CHOICE,
                ExamStatus.PUBLISHED));

        mockMvc.perform(post("/api/v1/training/exams/21/records").header("Authorization", bearer(caregiverToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answers\":[{\"questionId\":31,\"answer\":\"A\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.passStatus").value(ExamPassStatus.PASSED))
                .andExpect(jsonPath("$.data.trainingStatus").value(LearningStatus.PASSED));
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
        role.setRoleStatus(EnableStatus.ENABLED);
        role.setIsDeleted(0);
        return role;
    }

    private TrainingResource resource(Long id, String status) {
        TrainingResource resource = new TrainingResource();
        resource.setId(id);
        resource.setResourceType(TrainingResourceType.ARTICLE);
        resource.setStorageMode(TrainingStorageMode.TEXT);
        resource.setCategoryId(1L);
        resource.setTitle("Pressure injury prevention");
        resource.setResourceStatus(status);
        resource.setPublishedAt(LocalDateTime.now());
        resource.setIsDeleted(0);
        return resource;
    }

    private TrainingExam exam(Long id, String status) {
        TrainingExam exam = new TrainingExam();
        exam.setId(id);
        exam.setExamName("Caregiver MVP Exam");
        exam.setPassScore(BigDecimal.valueOf(48));
        exam.setMaxAttempts(3);
        exam.setExamStatus(status);
        exam.setIsDeleted(0);
        return exam;
    }

    private ExamQuestion question(Long id, String type, String status) {
        ExamQuestion question = new ExamQuestion();
        question.setId(id);
        question.setResourceId(1L);
        question.setQuestionType(type);
        question.setQuestionContent("Question");
        question.setStandardAnswer("A");
        question.setQuestionStatus(status);
        question.setIsDeleted(0);
        return question;
    }

    private TrainingExamQuestion examQuestion(Long examId, Long questionId) {
        TrainingExamQuestion relation = new TrainingExamQuestion();
        relation.setExamId(examId);
        relation.setQuestionId(questionId);
        relation.setScore(BigDecimal.valueOf(60));
        relation.setSortNo(1);
        return relation;
    }

    private LearningRecord learningRecord(Long userId, int totalSeconds, String status) {
        LearningRecord record = new LearningRecord();
        record.setId(41L);
        record.setUserId(userId);
        record.setTrainingScope("MVP");
        record.setTotalLearningSeconds(totalSeconds);
        record.setTrainingStatus(status);
        return record;
    }

    private LearningAccessLog accessLog(Long userId, Long resourceId) {
        LearningAccessLog log = new LearningAccessLog();
        log.setUserId(userId);
        log.setResourceId(resourceId);
        log.setAccessSeconds(1800);
        return log;
    }
}
