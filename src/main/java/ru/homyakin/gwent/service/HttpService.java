package ru.homyakin.gwent.service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpService {
    private final static Logger logger = LoggerFactory.getLogger(HttpService.class);
    private final HttpClient httpClient;

    public HttpService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public Optional<String> getHtmlBodyByUrl(String url) {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                logger.error("Http code is not ok: {}", response.statusCode());
                return Optional.empty();
            }
            return Optional.of(response.body());
        } catch (Exception e) {
            logger.error("Unexpected error during http request to {}", url, e);
            return Optional.empty();
        }
    }
}
