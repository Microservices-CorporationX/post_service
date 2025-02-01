package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.db_repository.AlbumRepository;
import faang.school.postservice.service.album.album_filter.AlbumFilter;
import faang.school.postservice.validator.album.AlbumValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    private AlbumService albumService;

    @Mock
    private AlbumRepository albumRepository;
    @Spy
    private AlbumMapperImpl mapper;
    @Mock
    private AlbumValidator validator;
    @Mock
    private AlbumFilter filter;

    private List<AlbumFilter> filters;
    private AlbumRequestDto albumRequestDto;
    private AlbumRequestUpdateDto albumRequestUpdateDto;
    private AlbumResponseDto albumResponseDto;
    private Album album;
    private Post post;

    @BeforeEach
    public void setUp() {
        filters = List.of(filter);
        albumService = new AlbumService(albumRepository, mapper, validator, filters);
        post = Post.builder()
                .id(25L)
                .build();
        albumRequestDto = AlbumRequestDto.builder()
                .title("title")
                .build();
        album = Album.builder()
                .id(10L)
                .posts(new ArrayList<>())
                .build();
        albumResponseDto = AlbumResponseDto.builder()
                .id(10L)
                .authorId(0L)
                .postsIds(new ArrayList<>())
                .build();
        albumRequestUpdateDto = AlbumRequestUpdateDto.builder()
                .id(10L)
                .postsIds(List.of(25L))
                .build();

    }

    @Test
    public void testCreateAlbumSuccess() {
        albumService.createAlbum(albumRequestDto,5L);
        verify(albumRepository).save(albumCaptor.capture());
        Album album = albumCaptor.getValue();
        assertTrue(album.getPosts().isEmpty());
    }

    @Test
    public void testAddPostSuccess() {
        when(validator.validatePost(25L)).thenReturn(post);
        when(validator.validateAlbum(10L)).thenReturn(album);
        albumService.addPost(10L, 25L,5L);
        verify(albumRepository).save(album);
        assertEquals(post, album.getPosts().get(0));
    }

    @Test
    public void testDeletePostSuccess() {
        album.getPosts().add(post);
        when(validator.validatePost(25L)).thenReturn(post);
        when(validator.validateAlbum(10L)).thenReturn(album);
        albumService.deletePost(10L, 25L,5L);
        verify(albumRepository).save(album);
        assertTrue(album.getPosts().isEmpty());
    }

    @Test
    public void testAddAlbumToFavoriteAlbumsSuccess() {
        when(validator.validateFavoritesHasThisAlbum(1L, 1L)).thenReturn(false);
        albumService.addAlbumToFavoriteAlbums(1L, 1L);
        verify(albumRepository).addAlbumToFavorites(1L, 1L);
    }

    @Test
    public void testAddAlbumToFavoriteAlbumsWithException() {
        when(validator.validateFavoritesHasThisAlbum(1L, 1L)).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> albumService.addAlbumToFavoriteAlbums(1L, 1L));
    }

    @Test
    public void testDeleteAlbumFromFavoriteAlbumsSuccess() {
        when(validator.validateFavoritesHasThisAlbum(1L, 1L)).thenReturn(true);
        albumService.deleteAlbumFromFavoriteAlbums(1L, 1L);
        verify(albumRepository).deleteAlbumFromFavorites(1L, 1L);
        assertDoesNotThrow(() -> albumService.deleteAlbumFromFavoriteAlbums(1L, 1L));

    }

    @Test
    public void testDeleteAlbumFromFavoriteAlbumsWithException() {
        when(validator.validateFavoritesHasThisAlbum(1L, 1L)).thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> albumService.deleteAlbumFromFavoriteAlbums(1L, 1L));
    }

    @Test
    public void testGetAlbumSuccess() {
        when(validator.validateAlbum(10L)).thenReturn(album);
        assertEquals(albumResponseDto, albumService.getAlbum(10L));
    }

    @Test
    public void testGetAllAlbumsByFilterFilterSuccess() {
        List<Album> albums = Collections.singletonList(album);
        when(albumRepository.findAllAlbums()).thenReturn(albums.stream());
        when(filters.get(0).isApplicable(new AlbumFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(List.of(album));

        List<AlbumResponseDto> realList = albumService.getAlbumsByFilter(new AlbumFilterDto());

        verify(albumRepository).findAllAlbums();
        assertEquals(realList,mapper.toAlbumResponseDtoList(Collections.singletonList(album)));
    }

    @Test
    public void testGetAllFavoriteAlbumsByFilterSuccess() {
        List<Album> albums = Collections.singletonList(album);
        when(albumRepository.findFavoriteAlbumsByUserId(5L)).thenReturn(albums.stream());
        when(filters.get(0).isApplicable(new AlbumFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(List.of(album));

        List<AlbumResponseDto> realList = albumService.getAllFavoriteAlbumsByFilter(new AlbumFilterDto(),5L);

        verify(albumRepository).findFavoriteAlbumsByUserId(5L);
        assertEquals(realList,mapper.toAlbumResponseDtoList(Collections.singletonList(album)));
    }

    @Test
    public void testUpdateAlbumSuccess() {
        when(validator.validateAlbum(10L)).thenReturn(album);
        when(validator.validatePost(25L)).thenReturn(post);
        albumService.updateAlbum(albumRequestUpdateDto, 5L);
        verify(albumRepository).save(albumCaptor.capture());
    }

    @Test
    public void testDeleteAlbumSuccess() {
        when(validator.validateAlbum(10L)).thenReturn(album);
        albumService.deleteAlbum(10L, 5L);
        verify(albumRepository).deleteById(10L);
    }

}
