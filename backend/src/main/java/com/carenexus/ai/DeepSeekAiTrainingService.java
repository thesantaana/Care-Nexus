package com.carenexus.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@ConditionalOnProperty(name = "care-nexus.ai.mode", havingValue = "deepseek")
public class DeepSeekAiTrainingService implements AiTrainingService {
    private static final String SYSTEM_PROMPT =
            "你是CareNexus护理培训助手。只能根据提供的培训资料回答，不得编造资料中没有的信息。"
            + "资料不足时必须明确说明。不得提供疾病诊断、药品处方、个体化治疗方案或医疗决策。"
            + "回答应简洁、专业，适合护工学习。";
    private final DeepSeekClient client;

    public DeepSeekAiTrainingService(DeepSeekClient client) { this.client = client; }

    @Override
    public TrainingAiResult answerQuestion(TrainingAiRequest request) {
        requireSources(request);
        if (!StringUtils.hasText(request.getPrompt())) {
            throw new IllegalArgumentException("Question is required");
        }
        return result("培训资料：\n" + context(request) + "\n\n学员问题："
                + request.getPrompt() + "\n\n请仅依据资料回答。", false);
    }

    @Override
    public TrainingAiResult summarize(TrainingAiRequest request) {
        requireSources(request);
        return result("请总结以下培训资料的核心要点，以条目形式输出：\n" + context(request), false);
    }

    @Override
    public TrainingAiResult suggestLearning(TrainingAiRequest request) {
        requireSources(request);
        String learning = StringUtils.hasText(request.getPrompt()) ? request.getPrompt() : "暂无学习记录";
        return result("培训资料：\n" + context(request) + "\n\n学习情况：" + learning
                + "\n\n请给出3条具体、可执行的学习建议。", false);
    }

    @Override
    public TrainingAiResult generateQuestionDraft(TrainingAiRequest request) {
        requireSources(request);
        String type = "TRUE_FALSE".equals(request.getQuestionType()) ? "判断题" : "单选题";
        return result("请依据以下资料生成1道" + type
                + "题干草稿。只输出题干，不输出选项、答案或解析：\n" + context(request), true);
    }

    @Override
    public TrainingAiResult generatePractice(TrainingAiRequest request) {
        requireSources(request);
        return result("请严格依据以下资料生成3道单选练习题。只输出JSON数组，不要使用Markdown。"
                + "每项字段为question、options（4个字符串）、answer（从0开始的正确选项下标）、explanation。\n"
                + context(request), false);
    }

    @Override
    public TrainingAiResult explainAssignment(TrainingAiRequest request) {
        requireSources(request);
        return result("培训资料：\n" + context(request) + "\n\n题目：" + request.getPrompt()
                + "\n学员答案：" + request.getUserAnswer()
                + "\n正确答案：" + request.getStandardAnswer()
                + "\n请解释正确答案，并指出常见错误原因。不得扩展为医疗诊断或治疗建议。", false);
    }

    private TrainingAiResult result(String prompt, boolean draft) {
        return new TrainingAiResult(client.chat(SYSTEM_PROMPT, prompt), draft);
    }

    private void requireSources(TrainingAiRequest request) {
        if (request == null || request.getSources() == null || request.getSources().isEmpty()) {
            throw new IllegalArgumentException("Validated training source is required");
        }
    }

    private String context(TrainingAiRequest request) {
        StringBuilder context = new StringBuilder();
        for (TrainingAiSource source : request.getSources()) {
            context.append("【资料").append(source.getResourceId()).append("】")
                    .append(source.getTitle()).append("\n").append(source.getContent()).append("\n\n");
        }
        return context.toString().trim();
    }
}
