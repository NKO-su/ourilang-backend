package com.langly.langly_backend.dto;

import java.util.List;

public record ArticlePageResponse(
        List<ArticleSummaryResponse> articles,
        Long nextCursor,
        boolean hasMore
) {
}