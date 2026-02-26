package com.origin.urlshortener.dto;

public record UrlInfoResponse(
        String code,
        String originalUrl
) {
}
