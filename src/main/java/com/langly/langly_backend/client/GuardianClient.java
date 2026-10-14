package com.langly.langly_backend.client;

import com.langly.langly_backend.dto.GuardianSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class GuardianClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String apiKey;
    private final int pageSize;

    public GuardianClient(@Value("${guardian.api.base-url}") String baseUrl,
                          @Value("${guardian.api.key}") String apiKey,
                          @Value("${guardian.page-size}") int pageSize) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.pageSize = pageSize;
    }
    public List<GuardianSearchResponse.Result> fetchLatest() {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/search")
                .queryParam("order-by", "newest")
                .queryParam("page-size", pageSize)
                .queryParam("show-fields", "trailText,body,thumbnail")
                .queryParam("api-key", apiKey)
                .build()
                .toUri();

        GuardianSearchResponse response =
                restTemplate.getForObject(uri, GuardianSearchResponse.class);

        if (response == null || response.response() == null
                || response.response().results() == null) {
            return List.of();
        }
        return response.response().results();
    }
}