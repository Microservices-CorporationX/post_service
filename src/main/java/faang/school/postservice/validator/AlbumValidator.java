package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.AlbumException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumValidator {
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public void validateByTitleAndAuthorId(String title, Long authorId) {
        if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
            log.error("Альбом \"{}\" уже есть у пользователя {}", title, authorId);
            throw new AlbumException("Существующий с таким именем альбом");
        }
    }

    public void validateUser(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            log.error("Пользователь {} не существует", userId);
            throw new DataValidationException("Не существующий пользователь");
        }
    }
}
