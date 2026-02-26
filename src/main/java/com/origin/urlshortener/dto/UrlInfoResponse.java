package com.origin.urlshortener.dto;

public class UrlInfoResponse {

    private String code;
    private String originalUrl;

    public UrlInfoResponse() {}

    public UrlInfoResponse(String code, String originalUrl) {
        this.code = code;
        this.originalUrl = originalUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
