package faang.school.postservice.service;

import faang.school.postservice.config.ExternalServiceProperties;
import faang.school.postservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class ExternalService {
    private final RestTemplate restTemplate;
    private final ExternalServiceProperties externalServiceProperties;

    public boolean userExists(Long userId) {
        String url = externalServiceProperties.getProjectServiceUrl() + userId;
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            return handleHttpStatus(response.getStatusCode(), "user exists");
        } catch (Exception e) {
            throw new ExternalServiceException("Error checking if user exists", e);
        }
    }

    public boolean projectExists(Long projectId) {
        String url = externalServiceProperties.getProjectServiceUrl() + projectId;
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            return handleHttpStatus(response.getStatusCode(), "project exists");
        } catch (Exception e) {
            throw new ExternalServiceException("Error checking if project exists", e);
        }
    }

    private boolean handleHttpStatus(HttpStatusCode status, String context) {
        if (status == HttpStatus.OK) {
            return true;
        }

        if (status.is4xxClientError()) {
            return handle4xxError(status, context);
        }

        if (status.is5xxServerError()) {
            return handle5xxError(status, context);
        }

        throw new ExternalServiceException("Unexpected HTTP status: " + status, new Throwable(context));
    }

    private boolean handle4xxError(HttpStatusCode status, String context) {
        throw new ExternalServiceException("Client error: " + status, new Throwable(context));
    }

    private boolean handle5xxError(HttpStatusCode status, String context) {
        throw new ExternalServiceException("Server error: " + status, new Throwable(context));
    }
}
