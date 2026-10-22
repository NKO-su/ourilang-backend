package com.langly.langly_backend.service;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ArticleBodyFilter {

    private static final Set<String> ALLOWED_TAGS = Set.of(
            "p", "em", "strong", "i", "b", "br", "ul", "li", "h2", "a",
            "span", "div", "aside", "figure", "gu-atom"
    );

    private static final Pattern TAG_NAME = Pattern.compile("</?([a-zA-Z][a-zA-Z0-9-]*)");

    public boolean isDisplayable(String body) {
        if (body == null || body.isBlank()) {
            return false;
        }
        Matcher matcher = TAG_NAME.matcher(body);
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();
            if (!ALLOWED_TAGS.contains(tag)) {
                return false;
            }
        }
        return true;
    }
}