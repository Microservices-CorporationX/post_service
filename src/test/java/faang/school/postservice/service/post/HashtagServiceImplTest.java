package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.mapper.post.HashtagMapper;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.repository.post.HashtagRepository;
import faang.school.postservice.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static faang.school.postservice.util.HashtagPrepareData.buildNewHashtag;
import static faang.school.postservice.util.HashtagPrepareData.buildNewHashtagRequestDto;
import static faang.school.postservice.util.HashtagPrepareData.getPost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashtagServiceImplTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @Spy
    private HashtagMapper hashtagMapper;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private HashtagServiceImpl hashtagService;

    @Test
    public void testGetAllHashtags() {
        List<Hashtag> hashtags = List.of(Hashtag.builder().name("hashtag").build());
        when(hashtagRepository.findAll()).thenReturn(hashtags);

        List<HashtagResponseDto> actualResult = hashtagService.getAllHashtags();

        assertEquals(getExpectedResult(hashtags), actualResult);
    }

    @Test
    public void testGetTopHashtags() {
        List<Hashtag> hashtags = List.of(Hashtag.builder().name("hashtag").build());
        when(hashtagRepository.findAll()).thenReturn(hashtags);

        List<HashtagResponseDto> actualResult = hashtagService.getTopHashtags();

        assertEquals(getExpectedResult(hashtags), actualResult);
    }

    @Test
    public void testAddHashtagToPost() {
        when(postRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getPost()));
        when(hashtagRepository.findByName(eq("new"))).thenReturn(Optional.ofNullable(buildNewHashtag()));
        when(hashtagRepository.save(any())).thenReturn(buildNewHashtag());
        doNothing().when(hashtagRepository).addHashtagToPost(eq(1L), eq(1L));

        hashtagService.addHashtagToPost(buildNewHashtagRequestDto());

        verify(postRepository).findById(eq(1L));
        verify(hashtagRepository).findByName(eq("new"));
        verify(hashtagRepository).addHashtagToPost(eq(1L), eq(1L));
    }

    private List<HashtagResponseDto> getExpectedResult(List<Hashtag> hashtags) {
        return hashtags.stream()
                .map(hashtagMapper::toDto)
                .toList();
    }

}