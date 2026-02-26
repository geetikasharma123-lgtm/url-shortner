package com.origin.urlshortener.common.store;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory store.
 * codeToUrl: short code -> original URL
 * urlToCode: original URL -> short code
 */
public class InMemoryUrlStore {

    private final ConcurrentHashMap<String, String> codeToUrl = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> urlToCode = new ConcurrentHashMap<>();

    public Optional<String> findUrlByCode(String code) {
        return Optional.ofNullable(codeToUrl.get(code));
    }

    public Optional<String> findCodeByUrl(String url) {
        return Optional.ofNullable(urlToCode.get(url));
    }

    public boolean codeExists(String code) {
        return codeToUrl.containsKey(code);
    }

    public void save(String code, String url) {
        codeToUrl.put(code, url);
        urlToCode.put(url, code);
    }
}
