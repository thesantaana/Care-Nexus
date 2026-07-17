package com.carenexus.ai.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class TrainingAiResponses {

    private TrainingAiResponses() {
    }

    public static class Reference {
        public Long resourceId;
        public String title;
    }

    public static class ContentResponse {
        public String content;
        public List<Reference> references = new ArrayList<>();
    }

    public static class DraftResponse {
        public Long id;
        public String questionType;
        public String questionContent;
        public String standardAnswer;
        public String analysis;
        public List<Option> options = new ArrayList<>();
        public String draftStatus;
        public List<Reference> sourceResources = new ArrayList<>();
        public Long reviewedBy;
        public LocalDateTime reviewedAt;
        public String reviewComment;
        public Long questionId;
        public LocalDateTime createdAt;
    }

    public static class Option {
        public String label;
        public String content;
        public boolean correct;
    }

    public static class DraftBatchResponse {
        public List<Long> draftIds = new ArrayList<>();
    }

    public static class PracticeResponse {
        public List<PracticeQuestion> questions = new ArrayList<>();
        public List<Reference> references = new ArrayList<>();
    }

    public static class PracticeQuestion {
        public String question;
        public List<String> options = new ArrayList<>();
        public int answer;
        public String explanation;
    }
}
