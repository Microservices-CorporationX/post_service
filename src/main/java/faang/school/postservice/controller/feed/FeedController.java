package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedResponse;
import faang.school.postservice.exception.FeedAccessDeniedException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.service.feed.FeedService;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
@Slf4j
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;

    @GetMapping
    public ResponseEntity<FeedResponse> getFeed(
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(required = false, defaultValue = "20") @Max(50) int pageSize
    ) {
        try {
            Long currentUserId = userContext.getUserId();
            return ResponseEntity.ok(feedService.getFeed(currentUserId, lastPostId, pageSize));
        } catch (UserNotFoundException e) {
            log.error("User not found while getting feed", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error while getting feed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<FeedResponse> getUserFeed(
            @PathVariable Long userId,
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(required = false, defaultValue = "20") @Max(50) int pageSize
    ) {
        try {
            Long currentUserId = userContext.getUserId();
            return ResponseEntity.ok(feedService.getFeed(currentUserId, lastPostId, pageSize));
        } catch (UserNotFoundException e) {
            log.error("User not found while getting feed for userId: {}", userId, e);
            return ResponseEntity.notFound().build();
        } catch (FeedAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error while getting feed for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .badRequest()
                .body("Invalid request parameters: " +
                        ex.getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(ObjectError::getDefaultMessage)
                                .collect(Collectors.joining(", "))
                );
    }
}