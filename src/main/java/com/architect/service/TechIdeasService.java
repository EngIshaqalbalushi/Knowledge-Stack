package com.architect.service;

import com.architect.model.TechIdeasResult;
import com.architect.model.TechIdeasResult.Idea;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TechIdeasService {

    private final Map<String, List<Idea>> ideaDb = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final AiTechIdeasService aiTechIdeasService;

    public TechIdeasService(AiTechIdeasService aiTechIdeasService) {
        this.aiTechIdeasService = aiTechIdeasService;
        ideaDb.put("AI", List.of(
            new Idea("AI Resume Auditor", "LLM-based parser that identifies architectural gaps in senior engineering profiles through vector-based skill mapping.", List.of("Python", "Pinecone", "OpenAI"), "Enterprise"),
            new Idea("Code Review Copilot", "AI-powered PR review agent that learns team patterns and flags inconsistencies before human review.", List.of("TypeScript", "OpenAI", "GitHub API"), "Startup"),
            new Idea("ML Pipeline Monitor", "Real-time observability platform for ML model drift, data quality, and inference latency.", List.of("Python", "Kubernetes", "Prometheus"), "Enterprise")
        ));
        ideaDb.put("Web Dev", List.of(
            new Idea("GitMetrics Dashboard", "Visualizing repository velocity and contribution complexity using WebGL-based topology graphs.", List.of("Rust", "Wasm", "Three.js"), "Infrastructure"),
            new Idea("Headless CMS Toolkit", "Framework-agnostic headless CMS builder with visual schema designer and GraphQL API generation.", List.of("TypeScript", "GraphQL", "Node.js"), "Startup"),
            new Idea("Edge Function Manager", "Serverless edge function orchestrator with A/B testing and canary deployments.", List.of("Go", "Cloudflare Workers", "Redis"), "Enterprise")
        ));
        ideaDb.put("Fintech", List.of(
            new Idea("Event-Sourced Ledger", "High-throughput transaction ledger leveraging Kafka streams and CRDTs for eventual consistency.", List.of("Go", "Kafka", "gRPC"), "Distributed"),
            new Idea("RegTech Compliance Engine", "Automated regulatory compliance checker for financial transactions using rule-based ML.", List.of("Java", "Drools", "Python"), "Enterprise"),
            new Idea("Micro-Investment Platform", "Round-up investment app with fractional shares and social trading features.", List.of("TypeScript", "React Native", "PostgreSQL"), "Startup")
        ));
        ideaDb.put("Health", List.of(
            new Idea("FHIR Data Pipeline", "Healthcare interoperability platform transforming HL7/FHIR data into analytics-ready datasets.", List.of("Java", "Apache Flink", "HL7 FHIR"), "Enterprise"),
            new Idea("Remote Patient Monitor", "IoT-based vitals monitoring system with ML-driven anomaly detection.", List.of("Python", "MQTT", "TensorFlow Lite"), "Startup"),
            new Idea("Drug Discovery Platform", "Molecular simulation pipeline using quantum-inspired algorithms for candidate screening.", List.of("Python", "CUDA", "PyTorch"), "Research")
        ));
    }

    public TechIdeasResult generate(String industry, String skillLevel, String goal, List<String> techStack) {
        // Try AI-powered generation first
        TechIdeasResult aiResult = aiTechIdeasService.generate(industry, skillLevel, goal, techStack);
        if (aiResult != null) {
            return aiResult;
        }

        String key = industry != null ? industry : "AI";
        List<Idea> baseIdeas = ideaDb.getOrDefault(key, ideaDb.get("AI"));

        List<Idea> ideas = new ArrayList<>();
        int count = Math.min(baseIdeas.size(), 4);
        List<Idea> shuffled = new ArrayList<>(baseIdeas);
        java.util.Collections.shuffle(shuffled, random);
        for (int i = 0; i < count; i++) {
            Idea idea = shuffled.get(i);
            if (techStack != null && !techStack.isEmpty()) {
                List<String> combined = new ArrayList<>(techStack);
                combined.addAll(idea.getTechStack().stream().limit(3 - combined.size()).toList());
                ideas.add(new Idea(idea.getTitle(), idea.getDescription(), combined.stream().distinct().toList(), idea.getScale()));
            } else {
                ideas.add(idea);
            }
        }

        if (skillLevel != null && skillLevel.equalsIgnoreCase("Beginner")) {
            ideas.replaceAll(idea -> new Idea(
                idea.getTitle() + " (Simplified)",
                "Beginner-friendly version: " + idea.getDescription(),
                idea.getTechStack().stream().limit(2).toList(),
                "Learning"
            ));
        }

        if (goal != null && goal.toLowerCase().contains("startup")) {
            ideas.add(new Idea(
                "SaaS Boilerplate " + key,
                "A production-ready SaaS starter with auth, billing, and multi-tenant architecture tailored for " + key + ".",
                List.of("Next.js", "Stripe", "Supabase"),
                "Startup"
            ));
        }

        List<String> trends = switch (key) {
            case "AI" -> List.of("LLM fine-tuning costs dropping 10x", "Agentic AI workflows gaining traction", "Edge AI inference becoming mainstream");
            case "Web Dev" -> List.of("WebAssembly expanding beyond browser", "React Server Components adoption rising", "TypeScript surpassing JavaScript in new projects");
            case "Fintech" -> List.of("Embedded finance becoming standard", "CBDC pilot programs expanding globally", "DeFi 2.0 focusing on real-world assets");
            case "Health" -> List.of("AI diagnostic tools receiving FDA approval", "Wearable health data interoperability", "Federated learning for patient privacy");
            default -> List.of("Platform engineering on the rise", "Developer experience tooling maturing", "Open-source business models evolving");
        };

        List<String> tools = switch (key) {
            case "AI" -> List.of("LangChain", "Weaviate", "Ollama", "Hugging Face Transformers", "vLLM");
            case "Web Dev" -> List.of("Next.js 14", "Astro", "tRPC", "Prisma", "Biome");
            case "Fintech" -> List.of("Plaid API", "Stripe Connect", "Alpaca Markets", "Chainlink", "Fireblocks");
            case "Health" -> List.of("FHIR API", "DICOM Viewer", "OpenMRS", "BioPython", "OHDSI");
            default -> List.of("Docker", "Kubernetes", "Terraform", "GitHub Actions", "Prometheus");
        };

        return new TechIdeasResult(ideas, trends, tools);
    }
}
