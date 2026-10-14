package com.langly.langly_backend.repository;

import com.langly.langly_backend.dto.ArticleSummary;
import com.langly.langly_backend.model.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    boolean existsByGuardianId(String guardianId);

    List<ArticleSummary> findByIdLessThanOrderByIdDesc(Long cursor, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Article a WHERE a.fetchedAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") Instant cutoff);
}