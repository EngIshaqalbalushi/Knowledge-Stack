package com.architect.service;

import com.architect.model.CodeReviewResult;
import com.architect.model.CodeReviewResult.Issue;
import com.architect.model.CodeReviewResult.Metrics;
import com.architect.model.CodeReviewResult.Suggestion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiCodeReviewService {

    private static final Logger log = LoggerFactory.getLogger(AiCodeReviewService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final ObjectMapper mapper;

    public AiCodeReviewService(
            @Value("${ai.openrouter.base-url}") String baseUrl,
            @Value("${ai.openrouter.api-key}") String apiKey,
            @Value("${ai.openrouter.model}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.mapper = new ObjectMapper();
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("HTTP-Referer", "https://localhost:8080")
                .defaultHeader("X-Title", "KnowledgeStack Code Review")
                .build();
    }

    public CodeReviewResult review(String code, String languageHint, boolean beginnerMode) {
        try {
            String detectedLang = languageHint != null && !languageHint.isBlank() ? languageHint : detectLanguage(code);
            String prompt = buildPrompt(code, detectedLang, beginnerMode);
            String aiJson = callDeepSeek(prompt);
            CodeReviewResult result = parseResponse(aiJson, languageHint, detectedLang);
            if (result != null) {
                result.setAiPowered(true);
                return result;
            }
        } catch (Exception e) {
            log.warn("AI review failed, will fall back to pattern matching: {}", e.getMessage());
        }
        return null;
    }

    private String buildPrompt(String code, String language, boolean beginnerMode) {
        return """
You are a senior code reviewer. Analyze the following %s code and return ONLY valid JSON (no markdown, no code fences) with this exact structure:
{
  "issues": [
    {
      "line": <int>,
      "type": "<string e.g. Security, Performance, Best Practice, Error Handling, Code Quality, Null Safety, Memory, Design>",
      "severity": "<critical|high|medium|low>",
      "message": "<short issue title>",
      "explanation": "<detailed explanation%s>"
    }
  ],
  "fixedCode": "<the complete fixed code with all issues resolved, or the original code if no fixes needed>",
  "suggestions": [
    {
      "title": "<short category>",
      "description": "<what to do>",
      "code": "<code example>"
    }
  ],
  "metrics": {
    "complexity": "<O(1)|O(n)|O(n^2)|O(log n)>",
    "securityRating": "<A|B|C|D|F>",
    "engineOverhead": "<Low|Medium|High>",
    "refactorIndex": "<Low|Medium|High>"
  }
}

Find ALL real issues. Be thorough but accurate. Do NOT make up issues that don't exist.
Return the fixedCode as the complete improved version of the original code.

Code to review:
```%s
%s
```
""".formatted(language,
            beginnerMode ? ". Begin each explanation with 'In simple terms: '" : "",
            language, code);
    }

    private String callDeepSeek(String prompt) throws Exception {
        var body = new java.util.HashMap<String, Object>();
        body.put("model", model);
        body.put("messages", List.of(
            Map.of("role", "system", "content", "You are an expert code reviewer. Always respond with ONLY valid JSON, no markdown, no code fences."),
            Map.of("role", "user", "content", prompt)
        ));
        body.put("max_tokens", 4096);
        body.put("temperature", 0.1);

        String response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block(TIMEOUT);

        JsonNode root = mapper.readTree(response);
        String content = root.path("choices").get(0).path("message").path("content").asText();
        // Strip markdown code fences if present
        content = content.replaceAll("(?s)^```(?:json)?\\s*", "").replaceAll("(?s)\\s*```$", "").trim();
        return content;
    }

    private CodeReviewResult parseResponse(String json, String languageHint, String detectedLang) {
        try {
            JsonNode root = mapper.readTree(json);

            List<Issue> issues = new ArrayList<>();
            JsonNode issuesNode = root.path("issues");
            if (issuesNode.isArray()) {
                for (JsonNode n : issuesNode) {
                    issues.add(new Issue(
                        n.path("line").asInt(0),
                        n.path("type").asText("Info"),
                        n.path("severity").asText("info"),
                        n.path("message").asText("No message"),
                        n.path("explanation").asText("")
                    ));
                }
            }
            if (issues.isEmpty()) {
                issues.add(new Issue(0, "Info", "info", "No critical issues detected", "Your code looks clean."));
            }

            String fixedCode = root.path("fixedCode").asText("");

            List<Suggestion> suggestions = new ArrayList<>();
            JsonNode suggestionsNode = root.path("suggestions");
            if (suggestionsNode.isArray()) {
                for (JsonNode n : suggestionsNode) {
                    suggestions.add(new Suggestion(
                        n.path("title").asText(""),
                        n.path("description").asText(""),
                        n.path("code").asText("")
                    ));
                }
            }

            JsonNode metricsNode = root.path("metrics");
            Metrics metrics = new Metrics(
                metricsNode.path("complexity").asText("O(1)"),
                metricsNode.path("securityRating").asText("A"),
                metricsNode.path("engineOverhead").asText("Low"),
                metricsNode.path("refactorIndex").asText("Low")
            );

            return new CodeReviewResult(
                languageHint != null ? languageHint : detectedLang,
                detectedLang,
                fixedCode.isEmpty() ? null : fixedCode,
                issues,
                suggestions,
                metrics
            );
        } catch (Exception e) {
            log.warn("Failed to parse AI response: {}", e.getMessage());
            return null;
        }
    }

    private String detectLanguage(String code) {
        if (code.contains("function") || code.contains("=>") || code.contains("const ") || code.contains("let ") || code.contains("var ") || code.contains("document.") || code.contains("console.log")) return "JavaScript";
        if (code.contains("def ") || code.contains("print(") || code.contains("import ") || code.contains("lambda") || (code.contains(":") && (code.contains("#") || code.contains("class ")))) return "Python";
        if (code.contains("public class") || code.contains("System.out") || code.contains("private ") || code.contains("@Override") || code.contains("void main")) return "Java";
        if (code.contains("fn ") || code.contains("mut ") || code.contains("impl ") || code.contains("unwrap()")) return "Rust";
        if (code.contains("func ") || code.contains("package ") || code.contains("defer ") || code.contains("fmt.")) return "Go";
        if (code.contains("interface ") || code.contains(": string") || code.contains(": number") || code.contains(": any")) return "TypeScript";
        if (code.contains("#include") || code.contains("std::") || code.contains("int main") || code.contains("cout")) return "C++";
        if (code.contains("using System") || code.contains("Console.WriteLine") || code.contains("namespace ")) return "C#";
        if (code.contains("puts ") || code.contains("attr_accessor") || code.contains("gem ") || code.contains("do |")) return "Ruby";
        if (code.contains("<?php") || code.contains("echo ") && code.contains("$")) return "PHP";
        if (code.contains("import Swift") || code.contains("UIKit") || code.contains("Foundation")) return "Swift";
        if (code.contains("fun ") || code.contains("val ") || code.contains("import ") && code.contains("kotlin")) return "Kotlin";
        return "Unknown";
    }
}
