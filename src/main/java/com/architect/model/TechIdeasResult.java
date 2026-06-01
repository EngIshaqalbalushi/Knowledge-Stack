package com.architect.model;

import java.util.List;

public class TechIdeasResult {
    private List<Idea> ideas;
    private List<String> marketTrends;
    private List<String> tools;

    public TechIdeasResult() {}

    public TechIdeasResult(List<Idea> ideas, List<String> marketTrends, List<String> tools) {
        this.ideas = ideas;
        this.marketTrends = marketTrends;
        this.tools = tools;
    }

    public List<Idea> getIdeas() { return ideas; }
    public void setIdeas(List<Idea> ideas) { this.ideas = ideas; }
    public List<String> getMarketTrends() { return marketTrends; }
    public void setMarketTrends(List<String> marketTrends) { this.marketTrends = marketTrends; }
    public List<String> getTools() { return tools; }
    public void setTools(List<String> tools) { this.tools = tools; }

    public static class Idea {
        private String title;
        private String description;
        private List<String> techStack;
        private String scale;

        public Idea() {}

        public Idea(String title, String description, List<String> techStack, String scale) {
            this.title = title;
            this.description = description;
            this.techStack = techStack;
            this.scale = scale;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getTechStack() { return techStack; }
        public void setTechStack(List<String> techStack) { this.techStack = techStack; }
        public String getScale() { return scale; }
        public void setScale(String scale) { this.scale = scale; }
    }
}
