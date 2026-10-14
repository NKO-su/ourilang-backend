package com.langly.langly_backend.dto;

import java.time.Instant;

public record ArticleSummaryResponse(
        Long id,
        String title,
        String trailText,
        String thumbnail,
        Instant publishedAt,
        String webUrl) {

    public static ArticleSummaryResponse from(ArticleSummary s) {
        return new ArticleSummaryResponse(
                s.getId(),
                s.getTitle(),
                s.getTrailText(),
                s.getThumbnail(),
                s.getPublishedAt(),
                s.getWebUrl());
    }
}