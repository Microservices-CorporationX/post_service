package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.album.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.PostService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final PostMapper postMapper;
    private final List<AlbumFilter> albumFilters;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    private final UserContext userContext;

    public AlbumDto createAlbum(AlbumDto albumDto, long authorId) {
        validateUserExist(authorId);
        Album album = albumMapper.toEntity(albumDto);
        album.setAuthorId(authorId);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto addPostToAlbum(long postId, long albumId, long authorId) {
        validateUserExist(authorId);
        Album album = albumRepository.findById(albumId).orElseThrow(EntityNotFoundException::new);
        Post post = postService.getPost(postId);
        album.addPost(post);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public void addToFavorites(long albumId, long authorId) {
        validateUserExist(authorId);
        albumRepository.addAlbumToFavorites(albumId, authorId);
    }

    public void removeFromFavorites(long albumId, long authorId) {
        validateUserExist(authorId);
        albumRepository.deleteAlbumFromFavorites(albumId, authorId);
    }

    public AlbumDto getAlbum(long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album with id %s not found".formatted(albumId)));

        if (userAlbumPermission(album)) {
            return albumMapper.toDto(album);
        }

        throw new IllegalArgumentException("User with id %s dont have permission to album id %s".formatted(userContext.getUserId(), albumId));
    }

    public List<AlbumDto> getAlbums(long authorId) {
        validateUserExist(authorId);
        return albumRepository.findByAuthorId(authorId)
                .filter(this::userAlbumPermission)
                .map(albumMapper::toDto).toList();
    }


    public List<AlbumDto> getAlbumsWithFilter(long authorId, AlbumFilterDto albumFilterDto) {
        validateUserExist(authorId);
        if (albumFilterDto == null) {
            return albumRepository.findByAuthorId(authorId)
                    .filter(this::userAlbumPermission)
                    .map(albumMapper::toDto).toList();
        }

        Stream<Album> albums = albumRepository.findByAuthorId(authorId)
                .filter(this::userAlbumPermission);
        Stream<Album> filteredAlbums = filteredStream(albums, albumFilterDto);

        return filteredAlbums.map(albumMapper::toDto).toList();
    }

    public List<AlbumDto> getAllAlbumsWithFilter(AlbumFilterDto albumFilterDto) {
        if (albumFilterDto == null) {
            return albumRepository.findAll().stream()
                    .filter(this::userAlbumPermission)
                    .map(albumMapper::toDto).toList();
        }

        List<Album> albums = albumRepository.findAll().stream()
                .filter(this::userAlbumPermission)
                .toList();
        Stream<Album> filteredAlbums = filteredStream(albums.stream(), albumFilterDto);

        return filteredAlbums.map(albumMapper::toDto).toList();
    }

    public List<AlbumDto> getAllAlbums() {
        return albumRepository.findAll().stream()
                .filter(this::userAlbumPermission)
                .map(albumMapper::toDto).toList();
    }

    public List<AlbumDto> getFavoriteFilteredAlbums(long authorId, AlbumFilterDto albumFilterDto) {
        validateUserExist(authorId);
        if (albumFilterDto == null) {
            return albumRepository.findFavoriteAlbumsByUserId(authorId)
                    .filter(this::userAlbumPermission)
                    .map(albumMapper::toDto).toList();
        }

        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(authorId)
                .filter(this::userAlbumPermission);
        Stream<Album> filteredAlbums = filteredStream(albums, albumFilterDto);

        return filteredAlbums.map(albumMapper::toDto).toList();
    }

    public AlbumDto update(AlbumDto albumDto) {
        Album album = getAlbumById(albumDto.getId());

        albumMapper.update(albumDto, album);

        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto updateVisibility(Long albumId, AlbumVisibility visibility, @Nullable List<Long> userIds) {
        Album album = getAlbumById(albumId);
        album.setVisibility(visibility);
        if (visibility.equals(AlbumVisibility.SELECTED_USERS) && userIds != null) {
            album.setFavouriteUserIds(userIds);
        }
        return albumMapper.toDto(albumRepository.save(album));
    }

    public void remove(long albumId) {
        albumRepository.deleteById(albumId);
    }

    private void validateUserExist(Long id) {
        userServiceClient.getUserById(id);
    }

    private Stream<Album> filteredStream(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albumFilters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(albums,
                        (stream, filter) -> filter.apply(stream, albumFilterDto),
                        (s1, s2) -> s1);
    }

    private Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album with id %s not found".formatted(albumId)));
    }

    private boolean userAlbumPermission(Album album) {
        if (userContext.getUserId() != null && userContext.getUserId().equals(album.getAuthorId())) {
            return true;
        }
        if (album.getVisibility().equals(AlbumVisibility.SELECTED_USERS) && userContext.getUserId() != null) {
            return album.getFavouriteUserIds().contains(userContext.getUserId());
        }
        if (album.getVisibility().equals(AlbumVisibility.SUBSCRIBERS) && userContext.getUserId() != null) {
            return userServiceClient.getFollowers(album.getAuthorId()).stream()
                    .anyMatch(subscriptionUserDto -> subscriptionUserDto.getId().equals(userContext.getUserId()));
        }

        return album.getVisibility().equals(AlbumVisibility.PUBLIC);
    }
}
