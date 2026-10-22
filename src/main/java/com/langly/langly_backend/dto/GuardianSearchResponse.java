package com.langly.langly_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GuardianSearchResponse(Response response) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(String status, List<Result> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            String id,
            String type,
            String webTitle,
            String webUrl,
            String webPublicationDate,
            Fields fields) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Fields(String trailText, String body, String thumbnail, String byline) {}
}