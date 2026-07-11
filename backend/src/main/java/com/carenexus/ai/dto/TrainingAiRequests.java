package com.carenexus.ai.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public final class TrainingAiRequests {

    private TrainingAiRequests() {
    }

    public static class ContentRequest {
        @NotEmpty private List<Long> sourceResourceIds = new ArrayList<>();
        @Size(max = 500) private String question;

        public List<Long> getSourceResourceIds() { return sourceResourceIds; }

        public void setSourceResourceIds(List<Long> value) { this.sourceResourceIds = value; }

        public String getQuestion() { return question; }

        public void setQuestion(String value) { this.question = value; }
    }

    public static class QuestionDraftRequest {
        @NotEmpty private List<Long> sourceResourceIds = new ArrayList<>();
        @NotBlank private String questionType;
        @NotNull @Min(1) @Max(10) private Integer count;

        public List<Long> getSourceResourceIds() { return sourceResourceIds; }

        public void setSourceResourceIds(List<Long> value) { this.sourceResourceIds = value; }

        public String getQuestionType() { return questionType; }

        public void setQuestionType(String value) { this.questionType = value; }

        public Integer getCount() { return count; }

        public void setCount(Integer value) { this.count = value; }
    }

    public static class ReviewRequest {
        @NotBlank private String reviewResult;
        @Size(max = 500) private String comment;

        public String getReviewResult() { return reviewResult; }

        public void setReviewResult(String value) { this.reviewResult = value; }

        public String getComment() { return comment; }

        public void setComment(String value) { this.comment = value; }
    }
}
