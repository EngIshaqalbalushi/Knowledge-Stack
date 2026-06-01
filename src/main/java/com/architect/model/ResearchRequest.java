package com.architect.model;

import jakarta.validation.constraints.NotBlank;

public class ResearchRequest {
    @NotBlank
    private String topic;
    private String filterType;

    public ResearchRequest() {}

    public ResearchRequest(String topic, String filterType) {
        this.topic = topic;
        this.filterType = filterType;
    }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getFilterType() { return filterType; }
    public void setFilterType(String filterType) { this.filterType = filterType; }
}
