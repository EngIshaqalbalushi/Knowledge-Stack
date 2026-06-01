package com.architect.model;

import jakarta.validation.constraints.NotBlank;

public class CodeReviewRequest {
    @NotBlank
    private String code;
    private String language;
    private boolean beginnerMode;

    public CodeReviewRequest() {}

    public CodeReviewRequest(String code, String language, boolean beginnerMode) {
        this.code = code;
        this.language = language;
        this.beginnerMode = beginnerMode;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public boolean isBeginnerMode() { return beginnerMode; }
    public void setBeginnerMode(boolean beginnerMode) { this.beginnerMode = beginnerMode; }
}
