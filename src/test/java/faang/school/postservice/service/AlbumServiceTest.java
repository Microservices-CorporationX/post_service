package faang.school.postservice.service;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.AlbumUpdateDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotUniqueAlbumException;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.filter.albumfilter.AlbumDateFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.AlbumValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    private AlbumMapper albumMapper;

    @Spy
    private PostMapper postMapper;

    @Mock
    private AlbumValidator albumValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PostValidator postValidator;

    @Mock
    private AlbumDateFilter albumDateFilter;


    private AlbumService albumService;

    private long userId;
    private long albumId;
    private long postId;
    private String title;
    private String description;
    private String month;
    private AlbumDto albumDto;
    private AlbumDto albumDto1;
    private AlbumUpdateDto albumUpdateDto;
    private Album album;
    private Album album1;
    private UserDto userDto;
    private PostDto postDto;
    private Post post;
    private AlbumFilterDto albumFilterDto;
    private List<Filter<Album, AlbumFilterDto>> filters;

    @BeforeEach
    void setUp() {
        userId = 2L;
        albumId = 1L;
        postId = 3L;
        userDto = new UserDto(1L, "Author", "email");
        postDto = new PostDto();
        postDto.setId(1L);
        post = new Post();
        post.setId(postId);
        title = "Filter";
        description = "Java the best";
        month = "march";
        albumDto = AlbumDto.builder()
                .id(albumId)
                .title("Album1")
                .description("album about spring")
                .authorId(userId)
                .build();
        albumDto1 = AlbumDto.builder()
                .id(2L)
                .title("Filter")
                .description("album about spring")
                .authorId(userId)
                .build();
        albumUpdateDto = AlbumUpdateDto.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated Description")
                .build();
        album = Album.builder()
                .id(albumId)
                .title("Album1")
                .description("album about spring")
                .authorId(userId)
                .posts(new ArrayList<>())
                .build();
        album1 = Album.builder()
                .id(2L)
                .title("Filter")
                .description("Not content")
                .createdAt(LocalDateTime.of(2023, Month.MARCH, 10, 12, 0))
                .build();
        albumFilterDto = AlbumFilterDto.builder()
                .title("Filter")
                .description("Java the best")
                .month(Month.MARCH)
                .build();
        filters = List.of(albumDateFilter);
        albumService = new AlbumService(
                albumRepository,
                postValidator,
                userValidator,
                albumValidator,
                albumMapper,
                postMapper,
                postRepository,
                filters
        );
    }

    @Test
    void testCreateAlbumSucсesfully() {
        doNothing().when(userValidator).checkUserExistence(albumDto.getAuthorId());
        when(albumValidator.albumExistsByTitleAndAuthorId(albumDto)).thenReturn(albumDto);
        when(albumMapper.toEntity(albumDto)).thenReturn(album);
        when(albumMapper.toDto(album)).thenReturn(albumDto);
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDto result = albumService.createAlbum(albumDto);

        assertEquals(result, albumDto);
        verify(userValidator, times(1)).checkUserExistence(albumDto.getAuthorId());
        verify(albumMapper, times(1)).toDto(album);
        verify(albumValidator, times(1)).albumExistsByTitleAndAuthorId(albumDto);
        verify(albumMapper, times(1)).toEntity(albumDto);
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    void testCreateAlbumWhenAlbumExist() {
        doNothing().when(userValidator).checkUserExistence(albumDto.getAuthorId());
        when(albumValidator.albumExistsByTitleAndAuthorId(albumDto))
                .thenThrow(new NotUniqueAlbumException("Album with the same title and author already exists."));

        assertThrows(NotUniqueAlbumException.class, () -> albumService.createAlbum(albumDto));

        verify(userValidator, times(1)).checkUserExistence(albumDto.getAuthorId());
        verify(albumValidator, times(1)).albumExistsByTitleAndAuthorId(albumDto);
        verify(albumRepository, times(0)).save(album);
    }

    @Test
    void testCreateAlbumWhenUserNotFound() {
        doThrow(new EntityNotFoundException("User not found with id: " + albumDto.getAuthorId()))
                .when(userValidator).checkUserExistence(albumDto.getAuthorId());

        assertThrows(EntityNotFoundException.class, () -> albumService.createAlbum(albumDto),
                String.format("User not found with id: " + albumDto.getAuthorId()));

        verify(userValidator, times(1)).checkUserExistence(albumDto.getAuthorId());
        verify(albumRepository, times(0)).save(album);
    }

    @Test
    void testAddPostToAlboomSucсesfully() {
        doNothing().when(postValidator).validatePostExistsById(postId);
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of(album));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(albumRepository.save(album)).thenReturn(album);
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.addPostToAlbum(userDto.getId(), albumDto.getId(), postId);

        assertEquals(result, albumDto);
        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(postRepository, times(1)).findById(postId);
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    void testAddPostToAlbumPostDoesNotExist() {
        doThrow(new EntityNotFoundException("Post does not exist"))
                .when(postValidator).validatePostExistsById(postId);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                albumService.addPostToAlbum(1L, 1L, postId));

        assertEquals("Post does not exist", exception.getMessage());
        verify(postValidator).validatePostExistsById(postId);
        verify(albumRepository, times(0)).save(album);
    }

    @Test
    void testAddPostToAlbumWhenAlbumNotFound() {
        doNothing().when(postValidator).validatePostExistsById(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                albumService.addPostToAlbum(1L, 1L, postId));

        assertEquals("Album not found or doesn't belong to the user ", exception.getMessage());
        verify(postValidator).validatePostExistsById(postId);
        verify(albumRepository, times(0)).save(album);
    }

    @Test
    void testRemovePostToAlboomSucсesfully() {
        doNothing().when(postValidator).validatePostExistsById(postId);
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of(album));
        when(albumRepository.save(album)).thenReturn(album);
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.removePost(userDto.getId(), albumDto.getId(), postId);

        assertEquals(result, albumDto);
        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    void testRemovePostToAlbumPostDoesNotExist() {
        doThrow(new EntityNotFoundException("Post does not exist"))
                .when(postValidator).validatePostExistsById(postId);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                albumService.removePost(1L, 1L, postId));

        assertEquals("Post does not exist", exception.getMessage());
        verify(postValidator).validatePostExistsById(postId);
        verify(albumRepository, times(0)).save(album);
    }

    @Test
    void testAddAlbumToFavoritesSuccessfully() {
        when(albumRepository.findById(albumDto.getId())).thenReturn(Optional.of(album));
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.addAlbumToFavorites(userId, albumId);

        verify(albumRepository, times(1)).addAlbumToFavorites(albumDto.getId(), userId);
        assertEquals(albumDto, result);
    }

    @Test
    void testAddAlbumToFavoritesWhenAlbumNotFound() {
        when(albumRepository.findById(albumDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.addAlbumToFavorites(userId, albumId));

        verify(albumRepository, times(0)).addAlbumToFavorites(anyLong(), anyLong());
    }

    @Test
    void testDeleteAlbumFromFavoritesSuccessfully() {
        doNothing().when(albumRepository).deleteAlbumFromFavorites(albumId, userId);

        albumService.deleteAlbumFromFavorites(userId, albumId);

        verify(albumRepository, times(1)).deleteAlbumFromFavorites(albumId, userId);
    }

    @Test
    void testFindByAlbumIdThrowsEntityNotFoundExceptionWhenAlbumNotFound() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.findByAlbumId(albumId));

        verify(albumRepository, times(1)).findById(albumId);
        verify(albumMapper, never()).toDto(any(Album.class));
    }

    @Test
    void testFindByAlbumIdSuccessfully() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.findByAlbumId(albumId);

        assertEquals(albumDto, result);
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumMapper, times(1)).toDto(album);
    }

    @Test
    void testFindByAlbumIdWhenAlbumNotFound() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.findByAlbumId(albumId));

        verify(albumRepository, times(1)).findById(albumId);
        verify(albumMapper, never()).toDto(any(Album.class));
    }

    @Test
    void testGetAlbumsForUserByFilterWtisFilter() {
        when(albumRepository.findByAuthorId(userId)).thenReturn(Stream.of(album, album1));
        when(albumDateFilter.isApplicable(albumFilterDto)).thenReturn(true);
        when(albumDateFilter.apply(any(), eq(albumFilterDto))).thenAnswer(invocation -> Stream.of(album));
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getAlbumsForUserByFilter(userId, albumFilterDto);

        Assertions.assertEquals(albumDto, result.get(0));
        verify(albumDateFilter, times(1)).isApplicable(any(AlbumFilterDto.class));
        verify(albumDateFilter, times(1)).apply(any(), eq(albumFilterDto));
    }

    @Test
    void testGetAlbumsForUserByFilterWithNoApplicableFilters() {
        when(albumRepository.findByAuthorId(userId)).thenReturn(Stream.of(album, album1));
        when(albumDateFilter.isApplicable(albumFilterDto)).thenReturn(false);
        when(albumMapper.toDto(album1)).thenReturn(albumDto1);
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getAlbumsForUserByFilter(userId, albumFilterDto);

        assertEquals(List.of(albumDto, albumDto1), result);
        verify(albumRepository, times(1)).findByAuthorId(userId);
        verify(albumDateFilter, times(1)).isApplicable(albumFilterDto);
        verify(albumMapper, times(1)).toDto(album1);
        verify(albumMapper, times(1)).toDto(album);
    }

    @Test
    void testGetAllAlbumsByFilterSuccessfully() {
        when(albumRepository.findAll()).thenReturn(List.of(album, album1));
        when(albumDateFilter.isApplicable(albumFilterDto)).thenReturn(true);
        when(albumDateFilter.apply(any(), eq(albumFilterDto))).thenAnswer(invocation -> Stream.of(album));
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getAllAlbumsByFilter(albumFilterDto);

        assertEquals(albumDto, result.get(0));
        verify(albumDateFilter, times(1)).isApplicable(any(AlbumFilterDto.class));
        verify(albumDateFilter, times(1)).apply(any(), eq(albumFilterDto));
    }

    @Test
    void testGetAllAlbumsByFilterNotFound() {
        when(albumRepository.findAll()).thenReturn(List.of(album, album1));
        when(albumDateFilter.isApplicable(albumFilterDto)).thenReturn(false);
        when(albumMapper.toDto(album)).thenReturn(albumDto);
        when(albumMapper.toDto(album1)).thenReturn(albumDto1);

        List<AlbumDto> result = albumService.getAllAlbumsByFilter(albumFilterDto);

        assertEquals(2, result.size());
        assertEquals(albumDto, result.get(0));
        verify(albumDateFilter, times(1)).isApplicable(any(AlbumFilterDto.class));
        verify(albumDateFilter, times(0)).apply(any(), eq(albumFilterDto));
    }

    @Test
    void testGetFavoritAlbumsForUserByFilterSuccessfully() {
        AlbumFilterDto filterDto = AlbumFilterDto.builder()
                .title(title)
                .description(description)
                .month(Month.valueOf(month.toUpperCase()))
                .build();
        when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(Stream.of(album, album1));
        when(albumDateFilter.isApplicable(filterDto)).thenReturn(true);
        when(albumDateFilter.apply(any(), eq(filterDto))).thenAnswer(invocation -> Stream.of(album));
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getFavoritAlbumsForUserByFilter(userId, title, description, month);

        Assertions.assertEquals(albumDto, result.get(0));
        verify(albumDateFilter, times(1)).isApplicable(any(AlbumFilterDto.class));
        verify(albumDateFilter, times(1)).apply(any(), eq(filterDto));
    }

    @Test
    void testGetFavoritAlbumsForUserByFilterWithNoApplicableFilters() {
        AlbumFilterDto filterDto = AlbumFilterDto.builder()
                .title(title)
                .description(description)
                .month(Month.valueOf(month.toUpperCase()))
                .build();
        when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(Stream.of(album, album1));
        when(albumDateFilter.isApplicable(filterDto)).thenReturn(false);
        when(albumMapper.toDto(album1)).thenReturn(albumDto1);
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        List<AlbumDto> result = albumService.getFavoritAlbumsForUserByFilter(userId, title, description, month);

        assertEquals(List.of(albumDto, albumDto1), result);
        verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(userId);
        verify(albumDateFilter, times(1)).isApplicable(filterDto);
        verify(albumMapper, times(1)).toDto(album1);
        verify(albumMapper, times(1)).toDto(album);
    }

    @Test
    void testUpdateAlbumSuccessfully() {
        when(albumRepository.findById(albumUpdateDto.getId())).thenReturn(Optional.of(album));
        when(albumMapper.toEntity(any(AlbumDto.class))).thenReturn(album);
        doNothing().when(albumMapper).update(albumUpdateDto, album);
        when(albumRepository.save(album)).thenReturn(album);
        when(albumMapper.toDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.updateAlbum(albumUpdateDto);

        verify(albumRepository, times(1)).findById(albumUpdateDto.getId());
        verify(albumMapper, times(1)).update(albumUpdateDto, album);
        verify(albumRepository, times(1)).save(album);
        assertNotNull(result);
        assertEquals(albumDto, result);
    }

    @Test
    void testUpdateAlbumNotFound() {
        when(albumRepository.findById(albumUpdateDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.updateAlbum(albumUpdateDto));

        verify(albumRepository, times(1)).findById(albumUpdateDto.getId());
        verify(albumRepository, times(0)).save(any(Album.class));
        verify(albumMapper, times(0)).toDto(any(Album.class));
    }

    @Test
    void testDeleteAlbumSuccessfully() {
        when(albumRepository.findByAuthorId(userId)).thenReturn(Stream.of(album, album1));

        albumService.deleteAlbum(userId, albumId);

        verify(albumRepository, times(1)).deleteById(albumId);
    }

    @Test
    void testDeleteAlbumNotFound() {
        when(albumRepository.findByAuthorId(userId))
                .thenThrow(new EntityNotFoundException("Album not found for user"));

        assertThrows(EntityNotFoundException.class, () -> albumService.deleteAlbum(userId, albumId));

        verify(albumRepository, times(0)).deleteById(anyLong());
    }
}
