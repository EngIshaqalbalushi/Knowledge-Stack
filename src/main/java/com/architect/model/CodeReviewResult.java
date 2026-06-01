package com.architect.model;

import java.util.List;

public class CodeReviewResult {
    private String language;
    private String detectedLanguage;
    private String fixedCode;
    private List<Issue> issues;
    private List<Suggestion> suggestions;
    private Metrics metrics;
    private boolean aiPowered;

    public CodeReviewResult() {}

    public CodeReviewResult(String language, String detectedLanguage, String fixedCode,
                            List<Issue> issues, List<Suggestion> suggestions, Metrics metrics) {
        this.language = language;
        this.detectedLanguage = detectedLanguage;
        this.fixedCode = fixedCode;
        this.issues = issues;
        this.suggestions = suggestions;
        this.metrics = metrics;
        this.aiPowered = false;
    }

    public boolean isAiPowered() { return aiPowered; }
    public void setAiPowered(boolean aiPowered) { this.aiPowered = aiPowered; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getDetectedLanguage() { return detectedLanguage; }
    public void setDetectedLanguage(String detectedLanguage) { this.detectedLanguage = detectedLanguage; }
    public String getFixedCode() { return fixedCode; }
    public void setFixedCode(String fixedCode) { this.fixedCode = fixedCode; }
    public List<Issue> getIssues() { return issues; }
    public void setIssues(List<Issue> issues) { this.issues = issues; }
    public List<Suggestion> getSuggestions() { return suggestions; }
    public void setSuggestions(List<Suggestion> suggestions) { this.suggestions = suggestions; }
    public Metrics getMetrics() { return metrics; }
    public void setMetrics(Metrics metrics) { this.metrics = metrics; }

    public static class Issue {
        private int line;
        private String type;
        private String severity;
        private String message;
        private String explanation;

        public Issue() {}

        public Issue(int line, String type, String severity, String message, String explanation) {
            this.line = line;
            this.type = type;
            this.severity = severity;
            this.message = message;
            this.explanation = explanation;
        }

        public int getLine() { return line; }
        public void setLine(int line) { this.line = line; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public static class Suggestion {
        private String title;
        private String description;
        private String code;

        public Suggestion() {}

        public Suggestion(String title, String description, String code) {
            this.title = title;
            this.description = description;
            this.code = code;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class Metrics {
        private String complexity;
        private String securityRating;
        private String engineOverhead;
        private String refactorIndex;

        public Metrics() {}

        public Metrics(String complexity, String securityRating, String engineOverhead, String refactorIndex) {
            this.complexity = complexity;
            this.securityRating = securityRating;
            this.engineOverhead = engineOverhead;
            this.refactorIndex = refactorIndex;
        }

        public String getComplexity() { return complexity; }
        public void setComplexity(String complexity) { this.complexity = complexity; }
        public String getSecurityRating() { return securityRating; }
        public void setSecurityRating(String securityRating) { this.securityRating = securityRating; }
        public String getEngineOverhead() { return engineOverhead; }
        public void setEngineOverhead(String engineOverhead) { this.engineOverhead = engineOverhead; }
        public String getRefactorIndex() { return refactorIndex; }
        public void setRefactorIndex(String refactorIndex) { this.refactorIndex = refactorIndex; }
    }
}
