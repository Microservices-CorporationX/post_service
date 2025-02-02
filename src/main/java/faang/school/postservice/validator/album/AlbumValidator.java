package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.db_repository.AlbumRepository;
import faang.school.postservice.repository.db_repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlbumValidator {

    private final UserServiceClient userServiceClient;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;

    public void validateAuthor(long authorId) {
        UserDto author = userServiceClient.getUser(authorId);
        if (author == null) {
            log.error("Author {} not found", authorId);
            throw new EntityNotFoundException("Author with id " + authorId + " not found");
        }
    }

    public void validateAlbumWithSameTitleExists(long authorId, String title) {
        boolean result = albumRepository.existsByTitleAndAuthorId(title, authorId);
        if (result) {
            throw new DataValidationException("Title " + title + " already exists");
        }
    }

    public Post validatePost(long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            log.error("Post with id {} not found", postId);
            throw new EntityNotFoundException("Post with id " + postId + " not found");
        }
        return post.get();
    }

    public void validateAuthorHasThisAlbum(long albumId, long authorId) {
        Album album = validateAlbum(albumId);
        if (authorId != album.getAuthorId()) {
            log.error("AuthorId {} is not the same as albumId {}", authorId, albumId);
            throw new DataValidationException("AuthorId " + authorId +
                    " is not the same as albumsAuthorId " + album.getAuthorId());
        }
    }

    public Album validateAlbum(long id) {
        Optional<Album> album = albumRepository.findById(id);
        if (album.isEmpty()) {
            log.error("Album with id {} not found", id);
            throw new EntityNotFoundException("Album with id " + id + " not found");
        }
        return album.get();
    }

    public boolean validateFavoritesHasThisAlbum(long albumId, long authorId) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(authorId);
        return albums.anyMatch(album -> albumId == album.getId());
    }

}
