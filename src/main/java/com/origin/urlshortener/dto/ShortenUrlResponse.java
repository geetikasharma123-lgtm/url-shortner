package com.origin.urlshortener.dto;

public record ShortenUrlResponse(
        String code,
        String shortUrl,
        String originalUrl
) {
}
