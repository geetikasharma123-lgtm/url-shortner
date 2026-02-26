# URL Shortener API (Spring Boot, In-Memory)

Simple URL Shortener.

# Features
- Shorten URL (unique, short, non-sequential Base62 codes)
- Redirect to original URL
- Info endpoint to fetch original URL by code

## Start the app
```bash
mvn spring-boot:run
```

App runs on:
- http://localhost:8080

# Notes
- In-memory store is reset on restart.
- `app.base-url` and `app.code-length` configurable in `application.yml`.


# OpenAPI / Swagger
After starting the app, open:
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

