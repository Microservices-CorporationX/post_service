package faang.school.postservice.validator;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.NotUniqueAlbumException;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AlbumValidatorTest {
    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AlbumValidator albumValidator;

    private AlbumDto albumDto;
    private long id;

    @BeforeEach
    void setUp() {
        albumDto = AlbumDto.builder()
                .title("Album1")
                .description("album about spring")
                .authorId(1)
                .build();
        id = 1L;
    }

    @Test
    public void testWhenAlbumExistsByTitleAndAuthorId() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId()))
                .thenReturn(false);

        AlbumDto result = albumValidator.albumExistsByTitleAndAuthorId(albumDto);

        assertEquals(result, albumDto);
        verify(albumRepository, times(1))
                .existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId());
    }

    @Test
    public void albumExistsByTitleAndAuthorId_AlbumExists_ThrowsNotUniqueAlbumException() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId()))
                .thenReturn(true);

       assertThrows(NotUniqueAlbumException.class, () -> {
            albumValidator.albumExistsByTitleAndAuthorId(albumDto);
        });
        verify(albumRepository, times(1)).existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId());
    }
}
