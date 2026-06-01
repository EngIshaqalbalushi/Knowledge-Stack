package com.architect.controller;

import com.architect.model.TechIdeasRequest;
import com.architect.model.TechIdeasResult;
import com.architect.service.TechIdeasService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tech-ideas")
public class TechIdeasController {

    private final TechIdeasService techIdeasService;

    public TechIdeasController(TechIdeasService techIdeasService) {
        this.techIdeasService = techIdeasService;
    }

    @PostMapping("/generate")
    public ResponseEntity<TechIdeasResult> generate(@Valid @RequestBody TechIdeasRequest request) {
        TechIdeasResult result = techIdeasService.generate(
            request.getIndustry(),
            request.getSkillLevel(),
            request.getGoal(),
            request.getTechStack()
        );
        return ResponseEntity.ok(result);
    }
}
