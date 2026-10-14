package com.langly.langly_backend.controller;

import com.langly.langly_backend.dto.ArticleDetailResponse;
import com.langly.langly_backend.dto.ArticlePageResponse;
import com.langly.langly_backend.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ArticlePageResponse getArticles(
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Integer size) {
        return articleService.getArticles(cursor, size);
    }

    @GetMapping("/{id}")
    public ArticleDetailResponse getArticle(@PathVariable Long id) {
        return articleService.getArticle(id);
    }
}