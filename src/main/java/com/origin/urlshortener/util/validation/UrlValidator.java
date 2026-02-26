package com.origin.urlshortener.util.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

public final class UrlValidator {

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    /**
     * Validates that the URL is absolute and uses http/https and has a host.
     */
    public boolean isValidHttpUrl(String url) {
        if (url == null) {
            return false;
        }

        String trimmed = url.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        // Fast reject for whitespace/control characters anywhere in the URL.
        if (trimmed.chars().anyMatch(Character::isWhitespace)
                || trimmed.chars().anyMatch(Character::isISOControl)) {
            return false;
        }

        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || host == null || host.isBlank()) {
                return false;
            }

            return ALLOWED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT));
        } catch (URISyntaxException ex) {
            return false;
        }
    }
}
