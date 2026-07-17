package com.carenexus.training.service;

import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.dto.AssignmentImportPreviewResponse;
import com.carenexus.training.dto.AssignmentImportQuestion;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AssignmentDocxParser {
    private static final Pattern QUESTION = Pattern.compile("^\\s*\\d+[.、)]\\s*(.+)$");
    private static final Pattern OPTION = Pattern.compile("^\\s*([A-Fa-f])[.、)]\\s*(.+)$");
    private static final Pattern ANSWER = Pattern.compile("^\\s*答案\\s*[:：]\\s*(.+)$");
    private static final Pattern ANALYSIS = Pattern.compile("^\\s*解析\\s*[:：]\\s*(.*)$");

    public AssignmentImportPreviewResponse parse(MultipartFile file) throws IOException {
        validateFile(file);
        AssignmentImportPreviewResponse preview = new AssignmentImportPreviewResponse();
        preview.fileName = file.getOriginalFilename();
        try (InputStream input = file.getInputStream(); XWPFDocument document = new XWPFDocument(input)) {
            parseParagraphs(document.getParagraphs(), preview);
        } catch (IllegalArgumentException error) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "无法读取该 DOCX 文件，请确认文件未损坏");
        }
        if (preview.questions.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "未识别到题目，请按模板整理题号、答案和解析");
        }
        return preview;
    }

    private void parseParagraphs(List<XWPFParagraph> paragraphs, AssignmentImportPreviewResponse preview) {
        AssignmentImportQuestion current = null;
        boolean readingAnalysis = false;
        for (XWPFParagraph paragraph : paragraphs) {
            String line = paragraph.getText() == null ? "" : paragraph.getText().trim();
            if (line.isEmpty()) {
                continue;
            }
            Matcher questionMatcher = QUESTION.matcher(line);
            if (questionMatcher.matches()) {
                finish(current, preview);
                current = new AssignmentImportQuestion();
                current.content = questionMatcher.group(1).trim();
                readingAnalysis = false;
                continue;
            }
            if (current == null) {
                preview.warnings.add("已忽略题目前的文字：" + line);
                continue;
            }
            Matcher optionMatcher = OPTION.matcher(line);
            Matcher answerMatcher = ANSWER.matcher(line);
            Matcher analysisMatcher = ANALYSIS.matcher(line);
            if (optionMatcher.matches()) {
                current.options.add(optionMatcher.group(2).trim());
                readingAnalysis = false;
            } else if (answerMatcher.matches()) {
                current.standardAnswer = answerMatcher.group(1).trim();
                readingAnalysis = false;
            } else if (analysisMatcher.matches()) {
                current.analysis = analysisMatcher.group(1).trim();
                readingAnalysis = true;
            } else if (readingAnalysis) {
                current.analysis = (current.analysis + " " + line).trim();
            } else {
                current.content = current.content + " " + line;
            }
        }
        finish(current, preview);
    }

    private void finish(AssignmentImportQuestion question, AssignmentImportPreviewResponse preview) {
        if (question == null) {
            return;
        }
        if (question.standardAnswer == null || question.standardAnswer.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "题目“" + question.content + "”缺少答案");
        }
        if (question.options.isEmpty()) {
            question.type = "TRUE_FALSE";
            question.options.add("正确");
            question.options.add("错误");
            question.standardAnswer = normalizeBoolean(question.standardAnswer);
        } else {
            question.type = "SINGLE_CHOICE";
            int index = answerIndex(question.standardAnswer);
            if (index < 0 || index >= question.options.size()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "题目“" + question.content + "”的答案不在选项中");
            }
            question.standardAnswer = question.options.get(index);
        }
        if (question.analysis == null || question.analysis.trim().isEmpty()) {
            preview.warnings.add("题目“" + question.content + "”未填写解析，可在发布前补充");
            question.analysis = "";
        }
        preview.questions.add(question);
    }

    private int answerIndex(String answer) {
        String normalized = answer.trim().toUpperCase(Locale.ROOT);
        return normalized.length() == 1 && normalized.charAt(0) >= 'A' && normalized.charAt(0) <= 'F'
                ? normalized.charAt(0) - 'A' : -1;
    }

    private String normalizeBoolean(String answer) {
        String normalized = answer.trim().toLowerCase(Locale.ROOT);
        if ("正确".equals(normalized) || "对".equals(normalized) || "true".equals(normalized)) {
            return "正确";
        }
        if ("错误".equals(normalized) || "错".equals(normalized) || "false".equals(normalized)) {
            return "错误";
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "判断题答案必须是正确或错误");
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "请选择 DOCX 文件");
        }
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase(Locale.ROOT).endsWith(".docx")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仅支持 .docx 文件，不支持旧版 .doc");
        }
    }
}
