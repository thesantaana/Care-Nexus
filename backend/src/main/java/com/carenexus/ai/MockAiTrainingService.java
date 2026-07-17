package com.carenexus.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "care-nexus.ai.mode", havingValue = "mock", matchIfMissing = true)
public class MockAiTrainingService implements AiTrainingService {

    @Override
    public TrainingAiResult answerQuestion(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        return new TrainingAiResult("根据《" + source.getTitle() + "》：" + excerpt(source.getContent())
                + "。此内容仅用于护理培训资料学习。", false);
    }

    @Override
    public TrainingAiResult summarize(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        return new TrainingAiResult("《" + source.getTitle() + "》要点：" + excerpt(source.getContent())
                + "。请以已发布培训资料为准。", false);
    }

    @Override
    public TrainingAiResult suggestLearning(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        return new TrainingAiResult("建议复习《" + source.getTitle() + "》，重点回顾："
                + excerpt(source.getContent()) + "。完成资料学习后再参加客观题考核。", false);
    }

    @Override
    public TrainingAiResult generateQuestionDraft(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        String content = "TRUE_FALSE".equals(request.getQuestionType())
                ? "判断：护理操作应遵循《" + source.getTitle() + "》中的培训要求。"
                : "根据《" + source.getTitle() + "》，以下哪项描述符合培训资料？";
        return new TrainingAiResult(content, true);
    }

    @Override
    public TrainingAiResult generatePractice(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        String title = escape(source.getTitle());
        String content = "[{\"question\":\"学习《" + title
                + "》时，以下哪项做法最符合规范？\",\"options\":[\"按资料流程操作并记录\",\"跳过风险核对\",\"仅凭个人经验处理\",\"发现异常后继续操作\"],\"answer\":0,\"explanation\":\"护理操作应以已发布培训资料和机构规范为依据，完成风险核对、规范操作和记录。\"},"
                + "{\"question\":\"发现服务对象出现异常风险时，应如何处理？\",\"options\":[\"暂缓操作并及时报告\",\"隐瞒异常\",\"自行作出医疗诊断\",\"等待自然恢复\"],\"answer\":0,\"explanation\":\"发现异常应先保障安全，暂停不安全操作，及时记录并按流程报告。\"},"
                + "{\"question\":\"完成护理操作后，哪项工作不能省略？\",\"options\":[\"整理用品并如实记录\",\"删除异常记录\",\"替老人作医疗决定\",\"跳过手卫生\"],\"answer\":0,\"explanation\":\"操作后应完成清洁整理、手卫生和服务记录，重要异常还需上报。\"}]";
        return new TrainingAiResult(content, false);
    }

    @Override
    public TrainingAiResult explainAssignment(TrainingAiRequest request) {
        TrainingAiSource source = firstSource(request);
        return new TrainingAiResult("本题依据《" + source.getTitle() + "》。正确答案是“"
                + request.getStandardAnswer() + "”。应结合资料中的规范流程、风险核对和记录要求判断；"
                + "若选择不同答案，请重点复习相关操作顺序及异常上报要求。", false);
    }

    private TrainingAiSource firstSource(TrainingAiRequest request) {
        if (request == null || request.getSources() == null || request.getSources().isEmpty()) {
            throw new IllegalArgumentException("Validated training source is required");
        }
        return request.getSources().get(0);
    }

    private String excerpt(String content) {
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 120 ? normalized : normalized.substring(0, 120) + "...";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
