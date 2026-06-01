package com.architect.controller;

import com.architect.model.ResearchRequest;
import com.architect.model.ResearchResult;
import com.architect.service.ResearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
public class ResearchController {

    private final ResearchService researchService;

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @PostMapping("/search")
    public ResponseEntity<ResearchResult> search(@Valid @RequestBody ResearchRequest request) {
        ResearchResult result = researchService.search(request.getTopic(), request.getFilterType());
        return ResponseEntity.ok(result);
    }
}
