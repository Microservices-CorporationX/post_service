package faang.school.postservice.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OrthographyService {
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

    public String getCorrectContent(String content, Long postId) {
        try {
            HttpResponse<String> response = getResponsesWithCorrectText(content);
            if (extractStatus(response)) {
                content = extractTextFromRequest(response);
                log.info("Post with id: {} was checked for grammar. New content: {}", postId, content);
            } else {
                log.error("Response status was false for post id: {}", postId);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return content;
    }


    @Retryable(value = {InterruptedException.class, IOException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    HttpResponse<String> getResponsesWithCorrectText(String text) throws IOException, InterruptedException {
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

    String extractTextFromRequest(HttpResponse<String> response) throws IOException, InterruptedException {
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonResponse.getAsJsonObject("response").get("corrected").getAsString();
    }

    boolean extractStatus(HttpResponse<String> response) {
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        try {
            if (jsonResponse.has("status")) {
                return jsonResponse.get("status").getAsBoolean();
            }
        } catch (Exception e) {
            System.err.println("Error extracting boolean: " + e.getMessage());
        }
        return false;
    }
}
