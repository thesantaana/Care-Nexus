package com.carenexus.training.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AssignmentPublishRequest {
    @NotNull
    public Long resourceId;

    @NotBlank
    @Size(max = 120)
    public String title;

    @Valid
    @NotEmpty
    @Size(max = 100)
    public List<AssignmentImportQuestion> questions = new ArrayList<>();
}
