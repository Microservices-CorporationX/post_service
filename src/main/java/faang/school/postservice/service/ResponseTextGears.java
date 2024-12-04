package faang.school.postservice.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Log4j2
@Component
public class ResponseTextGears {
    @Value("${services.text-gears.url}")
    private String url;
    @Value("${services.text-gears.header-1}")
    private String nameApiKey;
    @Value("${services.text-gears.api-key}")
    private String apiKey;
    @Value("${services.text-gears.header-2}")
    private String nameHost;
    @Value("${services.text-gears.value-host}")
    private String valueHost;
    @Value("${services.text-gears.header-3}")
    private String contentType;
    @Value("${services.text-gears.method}")
    private String method;
    @Value("${services.text-gears.body}")
    private String body;

    @Retryable(value = {InterruptedException.class, IOException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    HttpResponse<String> getResponsesWithCorrectText(String text) throws IOException, InterruptedException {
        System.out.println("Input to getResponsesWithCorrectText: " + text);
        System.out.println(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(nameApiKey, apiKey)
                .header(nameHost, valueHost)
                .header(contentType, APPLICATION_FORM_URLENCODED_VALUE)
                .method(method, HttpRequest.BodyPublishers.ofString(body + text))
                .build();
        int attempts = 0;
        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Request failed. Attempt: " + attempts);
            throw new IOException("Request failed. Attempt: " + attempts);
        } catch (InterruptedException e) {
            log.error("Request failed. Attempt: " + attempts);
            throw new InterruptedException("Request failed. Attempt: " + attempts);
        }
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
