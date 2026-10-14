package com.langly.langly_backend.service;

import com.langly.langly_backend.client.GuardianClient;
import com.langly.langly_backend.dto.GuardianSearchResponse;
import com.langly.langly_backend.model.Article;
import com.langly.langly_backend.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.List;

@Service
public class GuardianFetchService {

    private static final Logger log = LoggerFactory.getLogger(GuardianFetchService.class);

    private final GuardianClient guardianClient;
    private final ArticleRepository articleRepository;

    public GuardianFetchService(GuardianClient guardianClient,
                                ArticleRepository articleRepository) {
        this.guardianClient = guardianClient;
        this.articleRepository = articleRepository;
    }
    public int fetchAndSave() {
        List<GuardianSearchResponse.Result> results;
        try {
            results = guardianClient.fetchLatest();
        } catch (RestClientException e) {
            log.error("Guardian fetch failed: {}", e.getClass().getSimpleName());
            return 0;
        }

        int saved = 0;
        for (GuardianSearchResponse.Result r : results) {
            if (!"article".equals(r.type())) {
                continue;
            }
            if (r.id() == null || r.webPublicationDate() == null) {
                continue;
            }
            if (articleRepository.existsByGuardianId(r.id())) {
                continue;
            }

            Article article = new Article();
            article.setGuardianId(r.id());
            article.setTitle(r.webTitle());
            article.setWebUrl(r.webUrl());
            article.setPublishedAt(Instant.parse(r.webPublicationDate()));

            GuardianSearchResponse.Fields f = r.fields();
            if (f != null) {
                article.setTrailText(f.trailText());
                article.setBody(f.body());
                article.setThumbnail(f.thumbnail());
            }

            articleRepository.save(article);
            saved++;
        }

        log.info("Guardian fetch done: {} new articles saved", saved);
        return saved;
    }
}