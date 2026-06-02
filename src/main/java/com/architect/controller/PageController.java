package com.architect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/research")
    public String research() {
        return "research";
    }

    @GetMapping("/code-review")
    public String codeReview() {
        return "code-review";
    }

    @GetMapping("/tech-ideas")
    public String techIdeas() {
        return "tech-ideas";
    }

    @GetMapping("/favicon.ico")
    public String favicon() {
        return "redirect:/static/favicon.svg";
    }
}
