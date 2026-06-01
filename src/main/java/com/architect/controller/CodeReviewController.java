package com.architect.controller;

import com.architect.model.CodeReviewRequest;
import com.architect.model.CodeReviewResult;
import com.architect.service.CodeReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code-review")
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    public CodeReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<CodeReviewResult> analyze(@Valid @RequestBody CodeReviewRequest request) {
        CodeReviewResult result = codeReviewService.review(request.getCode(), request.getLanguage(), request.isBeginnerMode());
        return ResponseEntity.ok(result);
    }
}
