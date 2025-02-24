package ru.corporationx.postservice.service.album;

import ru.corporationx.postservice.dto.album.AlbumDto;
import ru.corporationx.postservice.exception.AlbumException;
import ru.corporationx.postservice.mapper.album.AlbumMapperImpl;
import ru.corporationx.postservice.model.Album;
import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.repository.AlbumRepository;
import ru.corporationx.postservice.service.post.PostService;
import ru.corporationx.postservice.validator.album.AlbumValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @InjectMocks
    private AlbumService albumService;

    @Mock
    private PostService postService;
    @Mock
    private AlbumValidator albumValidator;
    @Mock
    private AlbumRepository albumRepository;

    @Spy
    private AlbumMapperImpl albumMapper;


    @Test
    public void testCreatePositive() {
        AlbumDto albumDto = dtoSetUp();

        albumService.create(albumDto);

        Mockito.verify(albumRepository).save(any());
    }

    @Test
    public void testCreateNegative() {
        AlbumDto albumDto = dtoSetUp();
        Mockito.doThrow(new AlbumException("Существующий с таким именем альбом"))
                .when(albumValidator).validateByTitleAndAuthorId(anyString(), anyLong());

        assertThrows(AlbumException.class, () -> albumService.create(albumDto));
        Mockito.verify(albumRepository, times(0)).save(any());
    }

    @Test
    public void testAddPost() {
        Album album = albumSetUp();
        when(postService.findEntityById(anyLong())).thenReturn(new Post());
        when(albumRepository.findByIdWithPosts(1L)).thenReturn(Optional.of(album));
        albumService.addPost(1L, 1L);

        Mockito.verify(albumRepository, times(1)).save(any());
    }

    @Test
    public void testAddPostToNotExistingAlbum() {
        when(postService.findEntityById(1L)).thenReturn(new Post());

        assertThrows(AlbumException.class, () -> albumService.addPost(1L, 1L));
    }

    @Test
    public void testDeletePost() {
        Album album = albumSetUp();
        when(albumRepository.findByIdWithPosts(1L)).thenReturn(Optional.of(album));

        albumService.deletePost(1L, 1L);

        Mockito.verify(albumRepository, times(1)).save(any());
    }

    private AlbumDto dtoSetUp() {
        return new AlbumDto(1L, "title", "description", 1L);
    }

    private Album albumSetUp() {
        List<Post> posts = new ArrayList<>();
        return Album.builder()
                .id(1L)
                .posts(posts)
                .build();
    }
}
