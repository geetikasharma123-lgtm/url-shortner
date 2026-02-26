package com.origin.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void shorten_then_redirect_works() throws Exception {
        String body = "{\"url\":\"https://example.com/test/long/path?x=1DGHGHSDH\"}";
        var shortenResult = mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", not(blankString())))
                .andExpect(jsonPath("$.shortUrl", containsString("http://localhost:8080/")))
                .andExpect(jsonPath("$.originalUrl", is("https://example.com/test/long/path?x=1DGHGHSDH")))
                .andReturn();

        String json = shortenResult.getResponse().getContentAsString();
        String code = json.replaceAll(".*\"code\"\s*:\s*\"([^\"]+)\".*", "$1");

        // redirect
        mvc.perform(get("/" + code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/test/long/path?x=1DGHGHSDH"));
    }

    @Test
    void info_returns_original_url() throws Exception {
        String body = "{\"url\":\"https://example.com/a\"}";

        String json = mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String code = json.replaceAll(".*\"code\"\s*:\s*\"([^\"]+)\".*", "$1");

        mvc.perform(get("/api/info/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(code)))
                .andExpect(jsonPath("$.originalUrl", is("https://example.com/a")));
    }

    @Test
    void shorten_invalid_url_returns_400() throws Exception {
        String body = "{\"url\":\"not-a-url\"}";

        mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void redirect_unknown_code_returns_404() throws Exception {
        mvc.perform(get("/doesNotExist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
