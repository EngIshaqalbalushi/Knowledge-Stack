package com.architect.service;

import com.architect.model.TechIdeasResult;
import com.architect.model.TechIdeasResult.Idea;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiTechIdeasService {

    private static final Logger log = LoggerFactory.getLogger(AiTechIdeasService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    private final WebClient webClient;
    private final String model;
    private final ObjectMapper mapper;

    public AiTechIdeasService(
            @Value("${ai.openrouter.base-url}") String baseUrl,
            @Value("${ai.openrouter.api-key}") String apiKey,
            @Value("${ai.openrouter.model}") String model) {
        this.model = model;
        this.mapper = new ObjectMapper();
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("HTTP-Referer", "https://localhost:8080")
                .defaultHeader("X-Title", "KnowledgeStack Tech Ideas")
                .build();
    }

    public TechIdeasResult generate(String industry, String skillLevel, String goal, List<String> techStack) {
        try {
            String prompt = buildPrompt(industry, skillLevel, goal, techStack);
            String aiJson = callDeepSeek(prompt);
            return parseResponse(aiJson);
        } catch (Exception e) {
            log.warn("AI ideas generation failed, will fall back to canned data: {}", e.getMessage());
            return null;
        }
    }

    private String buildPrompt(String industry, String skillLevel, String goal, List<String> techStack) {
        String techPref = techStack != null && !techStack.isEmpty() ? String.join(", ", techStack) : "any";
        return """
You are a senior technology strategist and architect. Generate 4 innovative tech project ideas for the given parameters.

Return ONLY valid JSON (no markdown, no code fences) with this exact structure:
{
  "ideas": [
    {
      "title": "<project name>",
      "description": "<2-3 sentence description of the project>",
      "techStack": ["<tech1>", "<tech2>", "<tech3>"],
      "scale": "<Startup|Enterprise|Infrastructure|Research|Learning>"
    }
  ],
  "marketTrends": ["<trend 1>", "<trend 2>", "<trend 3>", "<trend 4>", "<trend 5>"],
  "tools": ["<tool1>", "<tool2>", "<tool3>", "<tool4>", "<tool5>"]
}

Parameters:
- Industry: %s
- Skill Level: %s
- Goal: %s
- Tech Stack Preferences: %s

Make ideas realistic, technically specific, and directly relevant to the given industry and skill level.
For Beginner skill level, make ideas simpler with fewer dependencies.
If a goal like "startup" is specified, favor ideas that could become viable businesses.
Include the preferred tech stack in the idea's techStack where appropriate.
""".formatted(
            industry != null ? industry : "General Technology",
            skillLevel != null ? skillLevel : "Advanced",
            goal != null && !goal.isEmpty() ? goal : "Any",
            techPref);
    }

    private String callDeepSeek(String prompt) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(
            Map.of("role", "system", "content", "You are a technology strategist. Always respond with ONLY valid JSON, no markdown, no code fences."),
            Map.of("role", "user", "content", prompt)
        ));
        body.put("max_tokens", 4096);
        body.put("temperature", 0.7);

        String response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block(TIMEOUT);

        JsonNode root = mapper.readTree(response);
        String content = root.path("choices").get(0).path("message").path("content").asText();
        content = content.replaceAll("(?s)^```(?:json)?\\s*", "").replaceAll("(?s)\\s*```$", "").trim();
        return content;
    }

    private TechIdeasResult parseResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            List<Idea> ideas = new ArrayList<>();
            JsonNode ideasNode = root.path("ideas");
            if (ideasNode.isArray()) {
                for (JsonNode n : ideasNode) {
                    List<String> techStack = new ArrayList<>();
                    JsonNode ts = n.path("techStack");
                    if (ts.isArray()) {
                        for (JsonNode t : ts) techStack.add(t.asText());
                    }
                    ideas.add(new Idea(
                        n.path("title").asText("Untitled Project"),
                        n.path("description").asText(""),
                        techStack,
                        n.path("scale").asText("Startup")
                    ));
                }
            }

            List<String> trends = new ArrayList<>();
            JsonNode trendsNode = root.path("marketTrends");
            if (trendsNode.isArray()) {
                for (JsonNode t : trendsNode) trends.add(t.asText());
            }

            List<String> tools = new ArrayList<>();
            JsonNode toolsNode = root.path("tools");
            if (toolsNode.isArray()) {
                for (JsonNode t : toolsNode) tools.add(t.asText());
            }

            return new TechIdeasResult(ideas, trends, tools);
        } catch (Exception e) {
            log.warn("Failed to parse AI ideas response: {}", e.getMessage());
            return null;
        }
    }
}
