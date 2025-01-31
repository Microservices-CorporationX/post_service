package faang.school.postservice.service.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.album.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.entity.Album;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.entity.Visibility;
import faang.school.postservice.repository.entity.AlbumRepository;
import faang.school.postservice.repository.entity.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AlbumServiceImpl implements AlbumService {

  private final AlbumRepository albumRepository;
  private final PostRepository postRepository;
  private final UserServiceClient userServiceClient;
  private final AlbumMapper albumMapper;
  private final List<AlbumFilter> albumFilters;

  @Override
  public List<Album> getAlbumsByIds(List<Long> albumsIds) {
    if (albumsIds == null) {
      return null;
    }
    return albumRepository.findAllById(albumsIds);
  }

  @Transactional
  @Override
  public AlbumDto add(AlbumCreateDto albumDto, Long userId) {
    validateAlbumAuthorTitle(albumDto, userId);
    validateUser(userId);
    Album album = albumMapper.toEntityFromCreateDto(albumDto);
    album.setAuthorId(userId);
    album.setPosts(new ArrayList<>());
    album.setVisibility(Visibility.OWNER);
    album.setFavorites("[]");
    album = albumRepository.save(album);
    log.info("New album with ID {} was created", album.getId());
    return albumMapper.toDto(album);
  }

  @Override
  public AlbumDto update(long userId, AlbumDto albumDto) {
    validateUserAccess(albumDto.getAuthorId(), userId);
    validateAlbumAuthorTitle(albumDto);

    Album album = findAlbumById(albumDto.getId());
    album.setTitle(albumDto.getTitle());
    album.setDescription(albumDto.getDescription());
    album.setFavorites(albumDto.getFavorites());
    album.setVisibility(albumDto.getVisibility());
    album.setUpdatedAt(LocalDateTime.now());

    return albumMapper.toDto(albumRepository.save(album));
  }

  @Override
  public void remove(long userId, AlbumDto albumDto) {
    validateUserAccess(albumDto.getAuthorId(), userId);
    Album album = findAlbumById(albumDto.getId());
    albumRepository.delete(album);
  }

  @Override
  public AlbumDto getAlbumById(long userId, long id) {
    validateUser(userId);
    Album album = findAlbumById(id);
    if (!isVisible(album, userId)) {
      throw new DataValidationException("Sorry, you have no access to this album");
    }
    return albumMapper.toDto(album);
  }

  @Override
  public Album findAlbumById(Long id) {
    return albumRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Album does not exist"));
  }

  @Transactional
  @Override
  public void addAlbumToFavorites(long albumId, long userId) {
    validateUser(userId);
    String title = findAlbumById(albumId).getTitle();
    albumRepository.addAlbumToFavorites(albumId, userId);
    log.info("The album {} was added to favorites", title);
  }

  @Transactional
  @Override
  public void removeAlbumFromFavorites(long albumId, long userId) {
    Album album = findAlbumById(albumId);
    validateUser(userId);
    validateUserAccess(album.getAuthorId(), userId);
    albumRepository.deleteAlbumFromFavorites(albumId, userId);
    log.info("The album {} was removed from favorites", findAlbumById(albumId).getTitle());
  }

  @Override
  public void addPost(long albumId, long postId, long userId) {
    Album album = findAlbumById(albumId);
    Post post = findPostById(postId);
    validateUserAccess(album.getAuthorId(), userId);
    album.addPost(post);
    albumRepository.save(album);
    log.info("The post {} was added to the album {}", post.getContent(), album.getTitle());
  }

  @Override
  public void removePost(long albumId, long postId, long userId) {
    Album album = findAlbumById(albumId);
    Post post = findPostById(postId);
    validateUserAccess(album.getAuthorId(), userId);
    album.removePost(postId);
    albumRepository.save(album);
    log.info("The post {} was deleted from the album {}", post.getContent(), album.getTitle());
  }

  @Override
  public Post findPostById(Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Post does not exist"));
  }

  @Transactional
  @Override
  public List<AlbumDto> getUserAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto) {
    Stream<Album> albums = albumRepository.findByAuthorId(userId);
    return getAlbumsWithFilter(albums, albumFilterDto);
  }

  @Transactional
  @Override
  public List<AlbumDto> getUserFavoriteAlbumsWithFilters(Long userId,
      AlbumFilterDto albumFilterDto) {
    Stream<Album> albums = getFavoriteAlbumsByUserId(userId);
    return getAlbumsWithFilter(albums, albumFilterDto);
  }

  @Override
  public Stream<Album> getFavoriteAlbumsByUserId(Long userId) {
    validateUser(userId);
    return albumRepository.findFavoriteAlbumsByUserId(userId);
  }

  @Override
  public List<AlbumDto> getAllAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto) {
    Stream<Album> albums = albumRepository.findAll().stream()
        .filter(album -> isVisible(album, userId));
    return getAlbumsWithFilter(albums, albumFilterDto);
  }

  @Override
  public AlbumDto addFavoriteUser(long albumId, long favoriteUserId, long userId) {
    AlbumDto albumDto = getAlbumById(userId, albumId);
    Album album = findAlbumById(albumId);
    String favoriteUsers = albumDto.getFavorites();
    favoriteUsers = addToFavoriteUsers(favoriteUsers, favoriteUserId);
    album.setFavorites(favoriteUsers);
    return albumMapper.toDto(albumRepository.save(album));
  }

  @Override
  public AlbumDto removeFavoriteUser(long albumId, long favoriteUserId, long userId) {
    AlbumDto albumDto = getAlbumById(userId, albumId);
    Album album = findAlbumById(albumId);
    String favoriteUsers = albumDto.getFavorites();
    favoriteUsers = removerFromFavoriteUsers(favoriteUsers, favoriteUserId);
    album.setFavorites(favoriteUsers);
    return albumMapper.toDto(albumRepository.save(album));
  }

  public List<AlbumDto> getAlbumsWithFilter(Stream<Album> albums,
      AlbumFilterDto albumFilterDto) {
    return albumFilters.stream()
        .filter(albumFilter -> albumFilter.isApplicable(albumFilterDto))
        .reduce(albums, (stream,
                albumFilter) -> albumFilter.apply(stream, albumFilterDto),
            (s1, s2) -> s1)
        .map(albumMapper::toDto)
        .toList();
  }

  boolean isVisible(Album album, long userId) {
    return switch (album.getVisibility()) {
      case ALL -> true;
      case SUBSCRIBERS ->
          isUserSubscriber(album.getAuthorId(), userId) || isUserAlbumAuthor(album, userId);
      case FAVORITES -> isUserFavorite(album, userId) || isUserAlbumAuthor(album, userId);
      case OWNER -> isUserAlbumAuthor(album, userId);
    };
  }

  private boolean isUserAlbumAuthor(Album album, long userId) {
    return album.getAuthorId() == userId;
  }

  private boolean isUserSubscriber(long authorId, long userId) {
    return userServiceClient.getUserSubscribers(authorId).stream()
        .map(UserDto::getId)
        .anyMatch(f -> f == userId);
  }

  private boolean isUserFavorite(Album album, long userId) {
    List<Long> favorites = getListFromJsonArray(album.getFavorites());
    return favorites.stream()
        .anyMatch(f -> f == userId);
  }

  private List<Long> getListFromJsonArray(String jsonArray) {
    ObjectMapper objectMapper = new ObjectMapper();
    TypeFactory typeFactory = objectMapper.getTypeFactory();
    try {
      return objectMapper.readValue(jsonArray,
          typeFactory.constructCollectionType(List.class, Long.class));
    } catch (JsonProcessingException e) {
      throw new DataValidationException(String.format("STRING '%s' is not JSON Array", jsonArray));
    }
  }

  private void validateUserAccess(long albumAuthorId, long userId) {
    if (albumAuthorId != userId) {
      throw new DataValidationException("Only owner can add or delete post from this album");
    }
  }

  private void validateAlbumAuthorTitle(AlbumDto albumDto) {
    long authorId = albumDto.getAuthorId();
    String title = albumDto.getTitle();
    if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
      throw new DataValidationException(
          String.format("User with ID %d already has album with Title %s", authorId, title));
    }
  }

  private void validateAlbumAuthorTitle(AlbumCreateDto albumDto, Long userId) {
    String title = albumDto.getTitle();
    if (albumRepository.existsByTitleAndAuthorId(title, userId)) {
      throw new DataValidationException(
          String.format("User with ID %d already has album with Title %s", userId, title));
    }
  }

  private void validateUser(long userId) {
    if (userServiceClient.getUser(userId) == null) {
      throw new EntityNotFoundException(String.format("User with ID %d not found", userId));
    }
  }

  private String addToFavoriteUsers(String stringArray, Long userIdToAdd) {
    List<Long> favorites = getListFromJsonArray(stringArray);
    if (!favorites.contains(userIdToAdd)) {
      favorites.add(userIdToAdd);
      stringArray = favorites.toString();
    }
    return stringArray;
  }

  private String removerFromFavoriteUsers(String stringArray, Long userIdToRemove) {
    List<Long> favorites = getListFromJsonArray(stringArray);
    if (favorites.contains(userIdToRemove)) {
      favorites.remove(userIdToRemove);
      stringArray = favorites.toString();
    }
    return stringArray;
  }

}
