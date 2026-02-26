package com.origin.urlshortener.controller;


import com.origin.urlshortener.dto.ShortenUrlRequest;
import com.origin.urlshortener.dto.ShortenUrlResponse;
import com.origin.urlshortener.dto.UrlInfoResponse;
import com.origin.urlshortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "URL Shortener", description = "Shorten, redirect, and lookup URLs")
@RestController
public class UrlShortenerController {

    private final com.origin.urlshortener.service.UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    /**
     * Shorten a long URL.
     */
    @Operation(summary = "Shorten a URL", description = "Creates a short, non-sequential code for a valid http/https URL.")
@ApiResponse(responseCode = "200", description = "Short URL created")
@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
@PostMapping("/api/shorten")
    public ResponseEntity<ShortenUrlResponse> shorten(@Valid @RequestBody ShortenUrlRequest request) {
        var result = service.shorten(request.getUrl());
        return ResponseEntity.ok(new ShortenUrlResponse(result.code(), result.shortUrl(), result.originalUrl()));
    }

    /**
     * Redirect to original URL when a short code is requested.
     */
    @Operation(summary = "Redirect", description = "Redirects to the original URL for the provided short code.")
@ApiResponse(responseCode = "302", description = "Redirect to original URL")
@ApiResponse(responseCode = "404", description = "Code not found", content = @Content)
@GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String target = service.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(target).toString())
                .build();
    }

    /**
     * Get original URL info for a short code
     */
    @Operation(summary = "Get URL info", description = "Returns the original URL for the provided short code.")
@ApiResponse(responseCode = "200", description = "Original URL returned")
@ApiResponse(responseCode = "404", description = "Code not found", content = @Content)
@GetMapping("/api/info/{code}")
    public ResponseEntity<UrlInfoResponse> info(@PathVariable String code) {
        var info = service.info(code);
        return ResponseEntity.ok(new UrlInfoResponse(info.code(), info.originalUrl()));
    }
}
