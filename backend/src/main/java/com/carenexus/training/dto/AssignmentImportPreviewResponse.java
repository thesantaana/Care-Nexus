package com.carenexus.training.dto;

import java.util.ArrayList;
import java.util.List;

public class AssignmentImportPreviewResponse {
    public String fileName;
    public List<AssignmentImportQuestion> questions = new ArrayList<>();
    public List<String> warnings = new ArrayList<>();
}
