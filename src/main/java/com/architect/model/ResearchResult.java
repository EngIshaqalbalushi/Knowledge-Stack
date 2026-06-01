package com.architect.model;

import java.util.List;

public class ResearchResult {
    private String topic;
    private String aiSummary;
    private List<ResourceItem> articles;
    private List<ResourceItem> videos;
    private List<ResourceItem> papers;
    private List<String> relatedTopics;

    public ResearchResult() {}

    public ResearchResult(String topic, String aiSummary, List<ResourceItem> articles,
                          List<ResourceItem> videos, List<ResourceItem> papers,
                          List<String> relatedTopics) {
        this.topic = topic;
        this.aiSummary = aiSummary;
        this.articles = articles;
        this.videos = videos;
        this.papers = papers;
        this.relatedTopics = relatedTopics;
    }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public List<ResourceItem> getArticles() { return articles; }
    public void setArticles(List<ResourceItem> articles) { this.articles = articles; }
    public List<ResourceItem> getVideos() { return videos; }
    public void setVideos(List<ResourceItem> videos) { this.videos = videos; }
    public List<ResourceItem> getPapers() { return papers; }
    public void setPapers(List<ResourceItem> papers) { this.papers = papers; }
    public List<String> getRelatedTopics() { return relatedTopics; }
    public void setRelatedTopics(List<String> relatedTopics) { this.relatedTopics = relatedTopics; }

    public static class ResourceItem {
        private String title;
        private String url;
        private String description;
        private String source;

        public ResourceItem() {}

        public ResourceItem(String title, String url, String description, String source) {
            this.title = title;
            this.url = url;
            this.description = description;
            this.source = source;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }
}
