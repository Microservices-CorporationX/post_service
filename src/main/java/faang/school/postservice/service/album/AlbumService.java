package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.db_repository.AlbumRepository;
import faang.school.postservice.service.album.album_filter.AlbumFilter;
import faang.school.postservice.validator.album.AlbumValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper mapper;
    private final AlbumValidator validator;
    private final List<AlbumFilter> filters;

    public AlbumResponseDto createAlbum(AlbumRequestDto albumRequestDto, long authorId) {
        String title = albumRequestDto.getTitle();
        validator.validateAuthor(authorId);
        validator.validateAlbumWithSameTitleExists(authorId, title);
        Album album = mapper.toAlbum(albumRequestDto);
        album.setPosts(new ArrayList<>());
        albumRepository.save(album);
        log.info("Album created: {}", album.getId());
        return mapper.toAlbumResponseDto(album);
    }

    public AlbumResponseDto addPost(long albumId, long postId, long authorId) {
        validator.validateAuthorHasThisAlbum(albumId, authorId);
        Post post = validator.validatePost(postId);
        Album album = validator.validateAlbum(albumId);
        album.addPost(post);
        albumRepository.save(album);
        log.info("The post {} has been added to the album {}", postId, albumId);
        return mapper.toAlbumResponseDto(album);
    }

    public void deletePost(long albumId, long postId, long authorId) {
        validator.validateAuthorHasThisAlbum(albumId, authorId);
        Post post = validator.validatePost(postId);
        Album album = validator.validateAlbum(albumId);
        album.removePost(post.getId());
        albumRepository.save(album);
        log.info("The post {} has been deleted from the album {}", postId, albumId);
    }

    @Transactional
    public void addAlbumToFavoriteAlbums(long albumId, long authorId) {
        validator.validateAuthorHasThisAlbum(albumId, authorId);
        validator.validateAlbum(albumId);
        validator.validateAuthor(authorId);
        boolean result = validator.validateFavoritesHasThisAlbum(albumId, authorId);
        if (result) {
            throw new DataValidationException("Favorite album with id " + albumId + " already exists");
        }
        albumRepository.addAlbumToFavorites(albumId, authorId);
        log.info("The album {} has been added to favorites", albumId);
    }

    public void deleteAlbumFromFavoriteAlbums(long albumId, long authorId) {
        validator.validateAuthorHasThisAlbum(albumId, authorId);
        validator.validateAlbum(albumId);
        validator.validateAuthor(authorId);
        boolean result = validator.validateFavoritesHasThisAlbum(albumId, authorId);
        if (!result) {
            throw new DataValidationException("Favorite album with id " + albumId + " does not exist");
        }
        albumRepository.deleteAlbumFromFavorites(albumId, authorId);
        log.info("The album {} has been deleted from favorites", albumId);
    }

    public AlbumResponseDto getAlbum(long albumId) {
        Album album = validator.validateAlbum(albumId);
        log.info("The album {} has been found", albumId);
        return mapper.toAlbumResponseDto(album);
    }

    public List<AlbumResponseDto> getAlbumsByFilter(AlbumFilterDto albumFilter) {
        Stream<Album> albums = albumRepository.findAllAlbums();
        filter(albumFilter, albums);
        log.info("Retrieved an albums using filters");
        return mapper.toAlbumResponseDtoList(albums.toList());
    }

    public List<AlbumResponseDto> getAllFavoriteAlbumsByFilter(AlbumFilterDto albumFilter, long authorId) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(authorId);
        filter(albumFilter, albums);
        log.info("Retrieved albums from the user's {} favorite albums list using filters", authorId);
        return mapper.toAlbumResponseDtoList(albums.toList());
    }

    public AlbumResponseDto updateAlbum(AlbumRequestUpdateDto albumRequestUpdateDto, long authorId) {
        String title = albumRequestUpdateDto.getTitle();
        validator.validateAuthor(authorId);
        validator.validateAlbumWithSameTitleExists(authorId, title);
        Album oldAlbum = validator.validateAlbum(albumRequestUpdateDto.getId());
        Album newAlbum = mapper.toAlbum(albumRequestUpdateDto);
        List<Post> posts = getPosts(albumRequestUpdateDto.getPostsIds());
        newAlbum.setPosts(posts);
        newAlbum.setAuthorId(oldAlbum.getAuthorId());
        albumRepository.save(newAlbum);
        log.info("Album updated: {}", newAlbum.getId());
        return mapper.toAlbumResponseDto(newAlbum);
    }

    @Transactional
    public void deleteAlbum(long albumId, long authorId) {
        validator.validateAuthorHasThisAlbum(albumId, authorId);
        validator.validateAlbum(albumId);
        validator.validateAuthor(authorId);
        albumRepository.deleteById(albumId);
        log.info("The album {} has been deleted", albumId);
    }

    private List<Post> getPosts(List<Long> postsIds) {
        return postsIds == null ? null : postsIds.stream().map(validator::validatePost).toList();
    }

    private void filter(AlbumFilterDto albumFilter, Stream<Album> albums) {
        filters.stream()
                .filter(filter -> filter.isApplicable(albumFilter))
                .forEach(filter -> filter.apply(albums, albumFilter));
    }

}
