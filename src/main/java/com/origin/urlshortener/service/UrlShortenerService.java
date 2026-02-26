package com.origin.urlshortener.service;

import com.origin.urlshortener.common.code.Base62CodeGenerator;
import com.origin.urlshortener.common.exception.InvalidUrlException;
import com.origin.urlshortener.common.exception.UrlNotFoundException;
import com.origin.urlshortener.common.store.InMemoryUrlStore;
import com.origin.urlshortener.common.validation.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

    private final InMemoryUrlStore store = new InMemoryUrlStore();
    private final Base62CodeGenerator generator = new Base62CodeGenerator();
    private final UrlValidator validator = new UrlValidator();

    private final String baseUrl;
    private final int codeLength;

    public UrlShortenerService(
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.code-length:6}") int codeLength
    ) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.codeLength = codeLength;
    }

    public ShortenResult shorten(String originalUrl) {
        if (!validator.isValidHttpUrl(originalUrl)) {
            throw new InvalidUrlException("Invalid URL format. Must be absolute http/https URL.");
        }

        return store.findCodeByUrl(originalUrl.trim())
                .map(code -> new ShortenResult(code, buildShortUrl(code), originalUrl.trim()))
                .orElseGet(() -> {
                    String code = generateUniqueCode();
                    String url = originalUrl.trim();
                    store.save(code, url);
                    return new ShortenResult(code, buildShortUrl(code), url);
                });
    }

    public String resolve(String code) {
        return store.findUrlByCode(code)
                .orElseThrow(() -> new UrlNotFoundException(code));
    }

    public UrlInfo info(String code) {
        String url = resolve(code);
        return new UrlInfo(code, url);
    }

    private String generateUniqueCode() {
        for (int i = 0; i < 20; i++) {
            String code = generator.generate(codeLength);
            if (!store.codeExists(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Unable to generate unique short code. Please retry.");
    }

    private String buildShortUrl(String code) {
        return baseUrl + "/" + code;
    }

    public record ShortenResult(String code, String shortUrl, String originalUrl) {}
    public record UrlInfo(String code, String originalUrl) {}
}
