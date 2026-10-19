package com.langly.langly_backend.exception;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(Long id) {
        super("Article not found: " + id);
    }
}