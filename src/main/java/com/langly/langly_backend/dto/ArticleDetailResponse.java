package com.langly.langly_backend.dto;

import com.langly.langly_backend.model.Article;

import java.time.Instant;

public record ArticleDetailResponse(
        Long id,
        String title,
        String trailText,
        String thumbnail,
        Instant publishedAt,
        String webUrl,
        String byline,
        String body) {

    public static ArticleDetailResponse from(Article a) {
        return new ArticleDetailResponse(
                a.getId(),
                a.getTitle(),
                a.getTrailText(),
                a.getThumbnail(),
                a.getPublishedAt(),
                a.getWebUrl(),
                a.getByline(),
                a.getBody());
    }
}