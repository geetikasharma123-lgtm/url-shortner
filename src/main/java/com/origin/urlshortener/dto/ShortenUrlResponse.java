package com.origin.urlshortener.dto;

public class ShortenUrlResponse {

    private String code;
    private String shortUrl;
    private String originalUrl;

    public ShortenUrlResponse() {}

    public ShortenUrlResponse(String code, String shortUrl, String originalUrl) {
        this.code = code;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
