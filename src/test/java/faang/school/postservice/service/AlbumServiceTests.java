package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.filter.album.CreatedAtAlbumFilter;
import faang.school.postservice.filter.album.TitleAlbumFilter;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.album.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.album.AlbumService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AlbumServiceTests {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;
    @Mock
    private UserContext userContext;
    @Spy
    private final AlbumMapper albumMapper = Mappers.getMapper(AlbumMapper.class);
    @Spy
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    private final CreatedAtAlbumFilter createdAtAlbumFilter = new CreatedAtAlbumFilter();
    private final TitleAlbumFilter titleAlbumFilter = new TitleAlbumFilter();
    private final List<AlbumFilter> albumFilters = Arrays.asList(createdAtAlbumFilter, titleAlbumFilter);

    @InjectMocks
    private AlbumService albumService;

    private Album album;
    private AlbumDto albumDto;
    private Post post;
    private UserDto userDto;
    private List<Album> albums;
    private AlbumFilterDto albumFilterDto;

    @BeforeEach
    void setUp() {

        album = new Album();
        album.setVisibility(AlbumVisibility.PUBLIC);
        albumDto = new AlbumDto();
        albumDto.setId(1L);
        albumDto.setVisibility(AlbumVisibility.PUBLIC);
        post = new Post();
        post.setId(1L);
        userDto = new UserDto(1L, "v", "@");
        albumFilterDto = new AlbumFilterDto();

        albumService = new AlbumService(
                albumRepository,
                albumMapper,
                postMapper,
                albumFilters,
                userServiceClient,
                postService,
                userContext
        );
    }

    @Test
    void testCreateAlbum() {
        prepareDtoWithTitleAndDescription();

        when(albumMapper.toEntity(any(AlbumDto.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.save(any(Album.class))).thenReturn(album);
        AlbumDto result = albumService.createAlbum(albumDto, 1L);

        assertNotNull(result);
        verify(albumRepository, times(1)).save(album);
        assertEquals(albumDto.getTitle(), result.getTitle());
        assertEquals(albumDto.getDescription(), result.getDescription());
    }

    @Test
    void testAddPostToAlbum() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumMapper.toEntity(any(AlbumDto.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(post);
        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(postService.getPost(anyLong())).thenReturn(new Post());
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        AlbumDto result = albumService.addPostToAlbum(1L, 1L, 1L);

        assertNotNull(result);
        verify(albumRepository, times(1)).findById(1L);
        verify(albumRepository, times(1)).save(album);
        verify(postService, times(1)).getPost(1L);
        assertEquals(albumDto.getTitle(), result.getTitle());
        assertEquals(albumDto.getDescription(), result.getDescription());
    }

    @Test
    void testAddPostToAlbumSaveMethodRuntimeException() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumMapper.toEntity(any(AlbumDto.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(post);
        doThrow(new RuntimeException("Database error")).when(albumRepository).save(any(Album.class));
        when(postService.getPost(anyLong())).thenReturn(new Post());
        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);

        assertThrows(RuntimeException.class, () -> albumService.addPostToAlbum(1L, 1L, 1L));
    }

    @Test
    void testAddpostToAlbumEntityAlbumIdFilterNotFoundException() {
        prepareDtoWithTitleAndDescription();
        when(albumMapper.toEntity(any(AlbumDto.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(post);
        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(postService.getPost(anyLong())).thenReturn(new Post());
        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);

        assertThrows(EntityNotFoundException.class, () -> albumService.addPostToAlbum(1L, 1L, 1L));
    }

    @Test
    void testAddToFavorites() {
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        albumService.addToFavorites(1L, 1L);

        verify(albumRepository, times(1)).addAlbumToFavorites(anyLong(), anyLong());
    }

    @Test
    void testAddToFavoritesException() {
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        doThrow(new RuntimeException("Database error")).when(albumRepository).addAlbumToFavorites(anyLong(), anyLong());

        assertThrows(RuntimeException.class, () -> albumService.addToFavorites(1L, 1L));
    }

    @Test
    void testRemoveFromFavorites() {
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        albumService.removeFromFavorites(1L, 1L);

        verify(albumRepository, times(1)).deleteAlbumFromFavorites(anyLong(), anyLong());
    }

    @Test
    void testGetAlbum() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.findById(anyLong())).thenReturn(Optional.ofNullable(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        AlbumDto result = albumService.getAlbum(1L);

        verify(albumRepository, times(1)).findById(anyLong());
        assertEquals(albumDto.getTitle(), result.getTitle());
    }

    @Test
    void testGetAlbumEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> albumService.getAlbum(1L));
    }

    @Test
    void testGetAlbums() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getAlbums(1L);

        verify(albumRepository, times(1)).findByAuthorId(anyLong());
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
    }

    @Test
    void testGetAlbumsWithEmptyFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getAlbumsWithFilter(1L, albumFilterDto);

        assertFalse(result.isEmpty());
        verify(albumRepository, times(1)).findByAuthorId(1L);
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getDescription(), result.get(0).getDescription());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
    }

    @Test
    void testGetAlbumsWithNonFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime);
        albumFilterDto = null;

        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getAlbumsWithFilter(1L, albumFilterDto);

        assertFalse(result.isEmpty());
        verify(albumRepository, times(1)).findByAuthorId(1L);
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getDescription(), result.get(0).getDescription());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
    }

    @Test
    void testGetAlbumsWithTitleFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.plusDays(1));
        albumFilterDto = new AlbumFilterDto("Title", null);

        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getAlbumsWithFilter(1L, albumFilterDto);

        verify(albumRepository, times(1)).findByAuthorId(1L);
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
    }

    @Test
    void testGetAlbumsWithOtherTitleFilterPattern() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.plusDays(1));
        albumFilterDto = new AlbumFilterDto("Wrong", null);

        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getAlbumsWithFilter(1L, albumFilterDto);

        assertTrue(result.isEmpty());
        verify(albumRepository, times(1)).findByAuthorId(1L);
    }

    @Test
    void testGetAlbumsWithFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.plusDays(1));
        albumFilterDto = new AlbumFilterDto("Title", dateTime);

        when(albumRepository.findByAuthorId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getAlbumsWithFilter(1L, albumFilterDto);

        verify(albumRepository, times(1)).findByAuthorId(1L);
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
    }

    @Test
    void testGetAllAlbumsWithEmptyFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.findAll()).thenReturn(List.of(album));
        List<AlbumDto> result = albumService.getAllAlbumsWithFilter(albumFilterDto);

        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
        verify(albumRepository, times(1)).findAll();
    }


    @Test
    void testGetAllAlbumsWithNonFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime);
        albumFilterDto = null;

        when(albumRepository.findAll()).thenReturn(List.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        List<AlbumDto> result = albumService.getAllAlbumsWithFilter(albumFilterDto);

        verify(albumRepository, times(1)).findAll();
    }

    @Test
    void testGetAllAlbumsWithTitleFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.plusDays(1));
        albumFilterDto = new AlbumFilterDto("Title", null);

        when(albumRepository.findAll()).thenReturn(List.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        List<AlbumDto> result = albumService.getAllAlbumsWithFilter(albumFilterDto);

        verify(albumRepository, times(1)).findAll();
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
    }

    @Test
    void testGetAllAlbumsWithHigherDateFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.plusDays(1));
        albumFilterDto = new AlbumFilterDto("Title", dateTime);

        when(albumRepository.findAll()).thenReturn(List.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        List<AlbumDto> result = albumService.getAllAlbumsWithFilter(albumFilterDto);

        verify(albumRepository, times(1)).findAll();
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
    }

    @Test
    void testGetAllAlbumsWithLowerDateFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.minusDays(1));
        albumFilterDto = new AlbumFilterDto("Title", dateTime);

        when(albumRepository.findAll()).thenReturn(List.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        List<AlbumDto> result = albumService.getAllAlbumsWithFilter(albumFilterDto);

        verify(albumRepository, times(1)).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllAlbums() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.findAll()).thenReturn(List.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        List<AlbumDto> result = albumService.getAllAlbums();

        verify(albumRepository, times(1)).findAll();
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getDescription(), result.get(0).getDescription());
    }

    @Test
    void testGetFavoriteFilteredAlbumsWithNonFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime);
        albumFilterDto = null;

        when(albumRepository.findFavoriteAlbumsByUserId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getFavoriteFilteredAlbums(1L, albumFilterDto);

        verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(anyLong());
    }

    @Test
    void testGetFavoriteFilteredAlbumsWithTitleFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.plusDays(1));
        albumFilterDto = new AlbumFilterDto("Title", null);

        when(albumRepository.findFavoriteAlbumsByUserId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getFavoriteFilteredAlbums(1L, albumFilterDto);

        verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(anyLong());
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
    }

    @Test
    void testGetFavoriteFilteredAlbumsWithHigherDateFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.plusDays(1));
        albumFilterDto = new AlbumFilterDto(null, dateTime);

        when(albumRepository.findFavoriteAlbumsByUserId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getFavoriteFilteredAlbums(1L, albumFilterDto);

        verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(anyLong());
        assertEquals(albumDto.getTitle(), result.get(0).getTitle());
        assertEquals(albumDto.getCreatedAt(), result.get(0).getCreatedAt());
    }

    @Test
    void testGetFavoriteFilteredAlbumsWithLowerDateFilter() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();
        LocalDateTime dateTime = LocalDateTime.now();
        album.setCreatedAt(dateTime.minusDays(1));
        albumFilterDto = new AlbumFilterDto(null, dateTime);

        when(albumRepository.findFavoriteAlbumsByUserId(anyLong())).thenReturn(Stream.of(album));
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(userServiceClient.getUserById(anyLong())).thenReturn(userDto);
        List<AlbumDto> result = albumService.getFavoriteFilteredAlbums(1L, albumFilterDto);

        verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(anyLong());
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdate() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        AlbumDto result = albumService.update(albumDto);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(albumDto.getTitle(), result.getTitle());
        assertEquals(albumDto.getDescription(), result.getDescription());
    }

    @Test
    void updateVisibilityPublic() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        AlbumDto result = albumService.updateVisibility(1L, AlbumVisibility.PUBLIC, null);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(AlbumVisibility.PUBLIC, result.getVisibility());
    }

    @Test
    void updateVisibilityPrivate() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        Album album = new Album();
        album.setVisibility(AlbumVisibility.PRIVATE);

        AlbumDto albumDto = new AlbumDto();
        albumDto.setVisibility(AlbumVisibility.PRIVATE);

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        AlbumDto result = albumService.updateVisibility(1L, AlbumVisibility.PRIVATE, null);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(AlbumVisibility.PRIVATE, result.getVisibility());
    }

    @Test
    void updateVisibilitySelectedUser() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        Album album = new Album();
        album.setVisibility(AlbumVisibility.SELECTED_USERS);
        album.setFavouriteUserIds(List.of(1L, 2L));

        AlbumDto albumDto = new AlbumDto();
        albumDto.setVisibility(AlbumVisibility.SELECTED_USERS);
        albumDto.setFavouriteUserIds(List.of(1L, 2L));

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        AlbumDto result = albumService.updateVisibility(1L, AlbumVisibility.SELECTED_USERS, null);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(AlbumVisibility.SELECTED_USERS, result.getVisibility());
        assertEquals(List.of(1L, 2L), result.getFavouriteUserIds());
    }

    @Test
    void updateVisibilitySubscribers() {
        prepareDtoWithTitleAndDescription();
        prepareAlbumEntity();

        Album album = new Album();
        album.setVisibility(AlbumVisibility.SUBSCRIBERS);

        AlbumDto albumDto = new AlbumDto();
        albumDto.setVisibility(AlbumVisibility.SUBSCRIBERS);

        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumMapper.toDto(any(Album.class))).thenReturn(albumDto);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        AlbumDto result = albumService.updateVisibility(1L, AlbumVisibility.SUBSCRIBERS, null);

        verify(albumRepository, times(1)).save(any(Album.class));
        assertEquals(AlbumVisibility.SUBSCRIBERS, result.getVisibility());
    }

    @Test
    void testRemove() {
        albumService.remove(1L);

        verify(albumRepository, times(1)).deleteById(anyLong());
    }


    private void prepareDtoWithTitleAndDescription() {
        albumDto.setTitle("Title");
        albumDto.setDescription("Description");
    }

    private void prepareAlbumEntity() {
        album.setId(1L);
        album.setTitle("Title");
        album.setAuthorId(1L);
        album.setPosts(new ArrayList<>());
    }
}
