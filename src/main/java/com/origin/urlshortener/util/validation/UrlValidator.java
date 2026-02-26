package com.origin.urlshortener.util.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

public final class UrlValidator {

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");
    private static final int MAX_URL_LENGTH = 2048;

    /**
     * Validates that the URL is absolute and uses http/https and has a host.
     */
    public boolean isValidHttpUrl(String url) {
        if (url == null) {
            return false;
        }

        String trimmed = url.trim();
        if (trimmed.isEmpty() || trimmed.length() > MAX_URL_LENGTH) {
            return false;
        }

        // Fast reject for whitespace/control characters anywhere in the URL.
        if (containsUnsafeCharacters(trimmed)) {
            return false;
        }

        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (!uri.isAbsolute() || scheme == null || host == null || host.isBlank()) {
                return false;
            }
            if (uri.getRawUserInfo() != null) {
                return false;
            }

            return ALLOWED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT));
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private static boolean containsUnsafeCharacters(String value) {
        return value.chars().anyMatch(ch -> Character.isWhitespace(ch) || Character.isISOControl(ch));
    }
}
