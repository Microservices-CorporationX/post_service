package faang.school.postservice.handler;

import faang.school.postservice.exception.ExternalErrorException;
import faang.school.postservice.exception.PostBadRequestException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.ServiceNotAvailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new PostBadRequestException("Bad request");
            case 404 -> new PostNotFoundException("Endpoint not found");
            case 503 -> new ServiceNotAvailableException("Product Api is unavailable");
            case 500 -> new ExternalErrorException(String.format("Unexpected error %s", methodKey));
            default -> new Exception("Exception while getting response");
        };
    }
}