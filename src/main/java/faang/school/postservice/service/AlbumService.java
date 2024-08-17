package faang.school.postservice.service;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.filter.AlbumFilter;
import faang.school.postservice.handler.EntityHandler;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.AlbumValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumMapper albumMapper;
    private final EntityHandler entityHandler;
    private final UserValidator userValidator;
    private final PostValidator postValidator;
    private final AlbumValidator albumValidator;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilterList;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        long authorId = albumDto.getAuthorId();
        userValidator.validateUserExistence(authorId);
        albumValidator.validateAlbumTitleDoesNotDuplicatePerAuthor(authorId, albumDto.getTitle());
        Album album = albumMapper.toEntity(albumDto);
        Album savedAlbum = albumRepository.save(album);
        return albumMapper.toDto(savedAlbum);
    }

    @Transactional
    public void addPostToAlbum(long authorId, long postId, long albumId) {
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        Post postToAdd = entityHandler.getOrThrowException(Post.class, postId, () -> postRepository.findById(postId));
        albumValidator.validateAlbumBelongsToAuthor(authorId, album);
        album.addPost(postToAdd);
    }

    @Transactional
    public void removePostFromAlbum(long authorId, long postId, long albumId) {
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        postValidator.validatePostExistence(postId);
        albumValidator.validateAlbumBelongsToAuthor(authorId, album);
        album.removePost(postId);
    }

    @Transactional
    public void addAlbumToFavourites(long albumId, long userId) {
        userValidator.validateUserExistence(userId);
        albumValidator.validateAlbumExistence(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    public void removeAlbumFromFavourites(long albumId, long userId) {
        userValidator.validateUserExistence(userId);
        albumValidator.validateAlbumExistence(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    @Transactional(readOnly = true)
    public AlbumDto getAlbumById(long albumId) {
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        return albumMapper.toDto(album);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAuthorFilteredAlbums(long authorId, AlbumFilterDto albumFilterDto) {
        Stream<Album> albumStream = getFilteredAlbums(albumRepository.findByAuthorId(authorId), albumFilterDto);
        return albumStream.map(albumMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAllFilteredAlbums(AlbumFilterDto albumFilterDto) {
        Stream<Album> albumStream = getFilteredAlbums(albumRepository.findAll().stream(), albumFilterDto);
        return albumStream.map(albumMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserFavoriteAlbums(long userId, AlbumFilterDto albumFilterDto) {
        Stream<Album> albumStream = getFilteredAlbums(albumRepository.findFavoriteAlbumsByUserId(userId), albumFilterDto);
        return albumStream.map(albumMapper::toDto).toList();
    }

    @Transactional
    public AlbumDto updateAlbum(long albumId, AlbumDto albumDto) {
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        albumValidator.validateAlbumBelongsToAuthor(albumDto.getAuthorId(), album);
        album.setDescription(albumDto.getDescription());
        album.setTitle(albumDto.getTitle());
        album.setUpdatedAt(LocalDateTime.now());
        return albumMapper.toDto(album);
    }

    @Transactional
    public void deleteAlbum(long albumId, long authorId) {
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        albumValidator.validateAlbumBelongsToAuthor(authorId, album);
        albumRepository.delete(album);
    }

    private Stream<Album> getFilteredAlbums(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        for (AlbumFilter albumFilter : albumFilterList) {
            albumStream = albumFilter.filter(albumStream, albumFilterDto);
        }
        return albumStream;
    }
}