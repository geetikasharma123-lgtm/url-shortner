package com.origin.urlshortener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerControllerTest {

    private static final String SHORTEN_ENDPOINT = "/api/shorten";
    private static final String INFO_ENDPOINT_PREFIX = "/api/info/";
    private static final String REDIRECT_PREFIX = "/";
    private static final String BASE_SHORT_URL = "http://localhost:8080/";
    private static final String STATUS_JSON_PATH = "$.status";
    private static final String CODE_JSON_PATH = "$.code";
    private static final String ORIGINAL_URL_JSON_PATH = "$.originalUrl";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shorten_then_redirect_works() throws Exception {
        String originalUrl = "https://example.com/test/long/path?x=1DGHGHSDH";
        MvcResult shortenResult = shortenUrl(originalUrl)
                .andExpect(status().isOk())
                .andExpect(jsonPath(CODE_JSON_PATH, not(blankString())))
                .andExpect(jsonPath("$.shortUrl", containsString(BASE_SHORT_URL)))
                .andExpect(jsonPath(ORIGINAL_URL_JSON_PATH, is(originalUrl)))
                .andReturn();

        String code = extractCode(shortenResult);

        mvc.perform(get(REDIRECT_PREFIX + code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void info_returns_original_url() throws Exception {
        String originalUrl = "https://example.com/a";
        MvcResult shortenResult = shortenUrl(originalUrl)
                .andExpect(status().isOk())
                .andReturn();
        String code = extractCode(shortenResult);

        mvc.perform(get(INFO_ENDPOINT_PREFIX + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CODE_JSON_PATH, is(code)))
                .andExpect(jsonPath(ORIGINAL_URL_JSON_PATH, is(originalUrl)));
    }

    @Test
    void shorten_invalid_url_returns_400() throws Exception {
        String body = "{\"url\":\"not-a-url\"}";

        mvc.perform(post(SHORTEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(STATUS_JSON_PATH, is(400)));
    }

    @Test
    void redirect_unknown_code_returns_404() throws Exception {
        mvc.perform(get("/doesNotExist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(STATUS_JSON_PATH, is(404)));
    }

    private org.springframework.test.web.servlet.ResultActions shortenUrl(String originalUrl) throws Exception {
        String body = objectMapper.createObjectNode()
                .put("url", originalUrl)
                .toString();
        return mvc.perform(post(SHORTEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private String extractCode(MvcResult shortenResult) throws Exception {
        String responseBody = shortenResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.path("code").asText();
    }
}
