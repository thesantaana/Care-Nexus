package com.carenexus.training;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.dto.AssignmentImportPreviewResponse;
import com.carenexus.training.service.AssignmentDocxParser;
import java.io.ByteArrayOutputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class AssignmentDocxParserTest {

    private final AssignmentDocxParser parser = new AssignmentDocxParser();

    @Test
    void shouldParseSingleChoiceAndTrueFalseQuestions() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (XWPFDocument document = new XWPFDocument()) {
            add(document, "1. 进行护理操作前首先应完成什么准备？");
            add(document, "A. 直接开始操作");
            add(document, "B. 完成手卫生并核对要求");
            add(document, "答案：B");
            add(document, "解析：规范准备是安全护理的基础。");
            add(document, "2. 发现异常风险后可以不记录继续操作。");
            add(document, "答案：错误");
            add(document, "解析：应暂停操作并按规范上报。");
            document.write(output);
        }

        MockMultipartFile file = new MockMultipartFile("file", "护理作业.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", output.toByteArray());
        AssignmentImportPreviewResponse preview = parser.parse(file);

        assertEquals(2, preview.questions.size());
        assertEquals("SINGLE_CHOICE", preview.questions.get(0).type);
        assertEquals("完成手卫生并核对要求", preview.questions.get(0).standardAnswer);
        assertEquals("TRUE_FALSE", preview.questions.get(1).type);
        assertEquals("错误", preview.questions.get(1).standardAnswer);
    }

    @Test
    void shouldRejectLegacyDocFile() {
        MockMultipartFile file = new MockMultipartFile("file", "护理作业.doc", "application/msword",
                new byte[] {1, 2, 3});

        assertThrows(BusinessException.class, () -> parser.parse(file));
    }

    private void add(XWPFDocument document, String text) {
        document.createParagraph().createRun().setText(text);
    }
}
