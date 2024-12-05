package faang.school.postservice.exception;

import feign.FeignException;

public class FeignCustomException extends FeignException {
    public FeignCustomException(int status, String message) {
        super(status, message);
    }
}
