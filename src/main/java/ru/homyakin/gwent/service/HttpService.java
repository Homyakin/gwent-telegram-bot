package ru.homyakin.gwent.service;

import io.vavr.control.Either;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.exceptions.EitherError;
import ru.homyakin.gwent.models.exceptions.UnknownHttpError;

@Service
public class HttpService {
    private final static Logger logger = LoggerFactory.getLogger(HttpService.class);
    private final HttpClient httpClient;

    public HttpService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public Either<EitherError, String> getHtmlBodyByUrl(String url) {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Either.right(response.body());
        } catch (Exception e) {
            logger.error("Unexpected error during http request to {}", url, e);
            return Either.left(new UnknownHttpError());
        }
    }
}
