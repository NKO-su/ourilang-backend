package com.langly.langly_backend.service;

import com.langly.langly_backend.dto.ArticleDetailResponse;
import com.langly.langly_backend.dto.ArticlePageResponse;
import com.langly.langly_backend.dto.ArticleSummary;
import com.langly.langly_backend.dto.ArticleSummaryResponse;
import com.langly.langly_backend.exception.ArticleNotFoundException;
import com.langly.langly_backend.model.Article;
import com.langly.langly_backend.repository.ArticleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ArticleService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticlePageResponse getArticles(Long cursor, Integer size) {
        int pageSize = (size == null || size < 1) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        long effectiveCursor = (cursor == null) ? Long.MAX_VALUE : cursor;

        List<ArticleSummary> rows = articleRepository.findByIdLessThanOrderByIdDesc(
                effectiveCursor, PageRequest.of(0, pageSize + 1));

        boolean hasMore = rows.size() > pageSize;
        List<ArticleSummary> pageRows = hasMore ? rows.subList(0, pageSize) : rows;

        List<ArticleSummaryResponse> articles = pageRows.stream()
                .map(ArticleSummaryResponse::from)
                .toList();

        Long nextCursor = (hasMore && !articles.isEmpty())
                ? articles.get(articles.size() - 1).id()
                : null;

        return new ArticlePageResponse(articles, nextCursor, hasMore);
    }

    public ArticleDetailResponse getArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        return ArticleDetailResponse.from(article);
    }
}