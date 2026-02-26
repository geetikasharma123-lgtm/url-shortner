package com.origin.urlshortener.service;

import com.origin.urlshortener.util.code.Base62CodeGenerator;
import com.origin.urlshortener.util.exception.InvalidUrlException;
import com.origin.urlshortener.util.exception.UrlNotFoundException;
import com.origin.urlshortener.util.store.InMemoryUrlStore;
import com.origin.urlshortener.util.validation.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

    private static final int MAX_GENERATION_ATTEMPTS = 20;
    private static final String INVALID_URL_MESSAGE = "Invalid URL format. Must be absolute http/https URL.";

    private final InMemoryUrlStore store;
    private final Base62CodeGenerator generator;
    private final UrlValidator validator;
    private final String baseUrl;
    private final int codeLength;

    @Autowired
    public UrlShortenerService(
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.code-length:6}") int codeLength
    ) {
        this(new InMemoryUrlStore(), new Base62CodeGenerator(), new UrlValidator(), baseUrl, codeLength);
    }

    UrlShortenerService(
            InMemoryUrlStore store,
            Base62CodeGenerator generator,
            UrlValidator validator,
            String baseUrl,
            int codeLength
    ) {
        this.store = store;
        this.generator = generator;
        this.validator = validator;
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.codeLength = validateCodeLength(codeLength);
    }

    public ShortenResult shorten(String originalUrl) {
        String normalizedUrl = originalUrl == null ? null : originalUrl.trim();
        if (!validator.isValidHttpUrl(normalizedUrl)) {
            throw new InvalidUrlException(INVALID_URL_MESSAGE);
        }

        return store.findCodeByUrl(normalizedUrl)
                .map(code -> new ShortenResult(code, buildShortUrl(code), normalizedUrl))
                .orElseGet(() -> {
                    String code = generateUniqueCode();
                    store.save(code, normalizedUrl);
                    return new ShortenResult(code, buildShortUrl(code), normalizedUrl);
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
        for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
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

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("app.base-url must not be blank");
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private static int validateCodeLength(int codeLength) {
        if (codeLength < 1) {
            throw new IllegalArgumentException("app.code-length must be greater than 0");
        }
        return codeLength;
    }

    public record ShortenResult(String code, String shortUrl, String originalUrl) {
    }

    public record UrlInfo(String code, String originalUrl) {
    }
}
