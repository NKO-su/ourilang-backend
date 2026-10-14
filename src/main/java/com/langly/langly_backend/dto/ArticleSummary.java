package com.langly.langly_backend.dto;

import java.time.Instant;

public interface ArticleSummary {
    Long getId();
    String getTitle();
    String getTrailText();
    String getThumbnail();
    Instant getPublishedAt();
    String getWebUrl();
}