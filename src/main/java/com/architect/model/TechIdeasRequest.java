package com.architect.model;

import java.util.List;

public class TechIdeasRequest {
    private String industry;
    private String skillLevel;
    private String goal;
    private List<String> techStack;

    public TechIdeasRequest() {}

    public TechIdeasRequest(String industry, String skillLevel, String goal, List<String> techStack) {
        this.industry = industry;
        this.skillLevel = skillLevel;
        this.goal = goal;
        this.techStack = techStack;
    }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public List<String> getTechStack() { return techStack; }
    public void setTechStack(List<String> techStack) { this.techStack = techStack; }
}
