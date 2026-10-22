package com.langly.langly_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String guardianId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 1000)
    private String webUrl;

    @Column(nullable = false)
    private Instant publishedAt;

    @Column(columnDefinition = "TEXT")
    private String trailText;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(length = 1000)
    private String thumbnail;

    @Column(length = 500)
    private String byline;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant fetchedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGuardianId() { return guardianId; }
    public void setGuardianId(String guardianId) { this.guardianId = guardianId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getWebUrl() { return webUrl; }
    public void setWebUrl(String webUrl) { this.webUrl = webUrl; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public String getTrailText() { return trailText; }
    public void setTrailText(String trailText) { this.trailText = trailText; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String getByline() { return byline; }
    public void setByline(String byline) { this.byline = byline; }

    public Instant getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(Instant fetchedAt) { this.fetchedAt = fetchedAt; }
}