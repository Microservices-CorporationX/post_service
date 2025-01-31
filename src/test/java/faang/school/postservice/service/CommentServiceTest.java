package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentService commentService;

    private static final long USER_ID = 1L;
    private static final long COMMENT_ID = 2L;
    private static final long POST_ID = 3L;
    private final UserDto userDto = UserDto.builder().id(USER_ID).build();
    private final Post post = Post.builder().id(POST_ID).authorId(USER_ID).build();
    private final CommentCreateDto commentCreateDto = CommentCreateDto.builder()
            .authorId(USER_ID)
            .content("content")
            .build();

    @Test
    void testAddComment() {
        when(userService.getUser(anyLong())).thenReturn(userDto);
        when(postService.findById(anyLong())).thenReturn(post);

        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(post);
        CommentReadDto expected = CommentReadDto.builder()
                .authorId(USER_ID)
                .content(commentCreateDto.getContent())
                .postId(POST_ID)
                .build();

        when(commentRepository.save(comment)).thenReturn(comment);

        CommentReadDto result = commentService.addComment(anyLong(), commentCreateDto);
        assertEquals(expected, result);
    }

    @Test
    void editComment() {
    }

    @Test
    void getComments() {
    }

    @Test
    void deleteComment() {
    }
}