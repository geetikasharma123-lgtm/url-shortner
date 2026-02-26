package com.origin.urlshortener.util.store;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory store.
 * codeToUrl: short code -> original URL
 * urlToCode: original URL -> short code
 */
public final class InMemoryUrlStore {

    private final ConcurrentHashMap<String, String> codeToUrl = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> urlToCode = new ConcurrentHashMap<>();

    public Optional<String> findUrlByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(codeToUrl.get(code));
    }

    public Optional<String> findCodeByUrl(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(urlToCode.get(url));
    }

    public boolean codeExists(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        return codeToUrl.containsKey(code);
    }

    public synchronized void save(String code, String url) {
        String normalizedCode = Objects.requireNonNull(code, "code must not be null").trim();
        String normalizedUrl = Objects.requireNonNull(url, "url must not be null").trim();

        if (normalizedCode.isEmpty() || normalizedUrl.isEmpty()) {
            throw new IllegalArgumentException("code and url must not be blank");
        }

        String existingUrl = codeToUrl.putIfAbsent(normalizedCode, normalizedUrl);
        if (existingUrl != null && !existingUrl.equals(normalizedUrl)) {
            throw new IllegalStateException("Short code already mapped to a different URL");
        }

        String existingCode = urlToCode.putIfAbsent(normalizedUrl, normalizedCode);
        if (existingCode != null && !existingCode.equals(normalizedCode)) {
            throw new IllegalStateException("URL already mapped to a different short code");
        }
    }
}
