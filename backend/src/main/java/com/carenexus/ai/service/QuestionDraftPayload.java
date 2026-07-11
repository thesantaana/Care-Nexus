package com.carenexus.ai.service;

import java.util.ArrayList;
import java.util.List;

public class QuestionDraftPayload {
    public String questionType;
    public String questionContent;
    public String standardAnswer;
    public String analysis;
    public List<OptionPayload> options = new ArrayList<>();

    public static class OptionPayload {
        public String label;
        public String content;
        public boolean correct;

        public OptionPayload() {
        }

        public OptionPayload(String label, String content, boolean correct) {
            this.label = label;
            this.content = content;
            this.correct = correct;
        }
    }
}
