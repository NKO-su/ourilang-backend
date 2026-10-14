package com.langly.langly_backend.scheduler;

import com.langly.langly_backend.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class ArticleCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(ArticleCleanupScheduler.class);

    private final ArticleRepository articleRepository;
    private final long maxAgeHours;

    public ArticleCleanupScheduler(ArticleRepository articleRepository,
                                   @Value("${guardian.article.max-age-hours}") long maxAgeHours) {
        this.articleRepository = articleRepository;
        this.maxAgeHours = maxAgeHours;
    }

    @Scheduled(fixedRateString = "${guardian.cleanup.interval}", initialDelay = 10000)
    public void cleanup() {
        Instant cutoff = Instant.now().minus(Duration.ofHours(maxAgeHours));
        int deleted = articleRepository.deleteOlderThan(cutoff);
        if (deleted > 0) {
            log.info("Cleanup: deleted {} articles older than {}h", deleted, maxAgeHours);
        }
    }
}