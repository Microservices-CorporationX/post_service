package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.post.PostAlreadyDeletedException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWOAuthorException;
import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.filter.post.filterImpl.PostFilterProjectDraftNonDeleted;
import faang.school.postservice.filter.post.filterImpl.PostFilterProjectPostNonDeleted;
import faang.school.postservice.filter.post.filterImpl.PostFilterUserDraftNonDeleted;
import faang.school.postservice.filter.post.filterImpl.PostFilterUserPostNonDeleted;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.publisher.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.CachePostRepository;
import faang.school.postservice.service.UserCacheService;
import faang.school.postservice.util.container.PostContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Value("${spring.data.redis.redisson_client.name_version}")
    private String version;
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostDataPreparer preparer;
    @Mock
    private PostValidator validator;
    @Mock
    private KafkaPostProducer kafkaPostProducer;
    @Mock
    private KafkaPostViewProducer kafkaPostViewProducer;
    @Spy
    private UserServiceClient userClient;
    @Mock
    private CachePostRepository redisPostRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RMap<Object, Object> versionMap;
    @Mock
    private UserCacheService userCacheService;
    @Spy
    private PostMapperImpl mapper;
    @Spy
    private PostFilterUserDraftNonDeleted userDraftNonDeleted;
    @Spy
    private PostFilterUserPostNonDeleted userPostNonDeleted;
    @Spy
    private PostFilterProjectDraftNonDeleted projectDraftNonDeleted;
    @Spy
    private PostFilterProjectPostNonDeleted projectPostNonDeleted;
    @Captor
    private ArgumentCaptor<Post> captorPost;
    private PostContainer container = new PostContainer();
    private Post entity;
    private PostDto dto;

    @BeforeEach
    void setUp() {
        entity = container.entity();
        dto = container.dto();
        List<PostFilter> postFilters = List.of(userDraftNonDeleted, userPostNonDeleted, projectDraftNonDeleted,
                projectPostNonDeleted);

        postService = new PostService(postRepository, validator, mapper, preparer, postFilters,
                kafkaPostProducer, kafkaPostViewProducer, userClient, redisPostRepository, userCacheService, redissonClient);
    }

    @Test
    void testCreateWithInvalidAuthor() {
        // given
        PostDto dtoWOAuthors = PostDto.builder()
                .id(container.postId())
                .build();

        PostDto dtoWithTwoAuthors = PostDto.builder()
                .id(container.postId() + 1)
                .build();

        // when
        doThrow(PostWOAuthorException.class).when(validator).validateBeforeCreate(dtoWOAuthors);
        doThrow(PostWithTwoAuthorsException.class).when(validator).validateBeforeCreate(dtoWithTwoAuthors);

        // then
        assertThrows(PostWOAuthorException.class, () -> postService.create(dtoWOAuthors));
        assertThrows(PostWithTwoAuthorsException.class, () -> postService.create(dtoWithTwoAuthors));
    }

    @Test
    void testCreateSuccessfully() {
        // given
        PostDto inputDto = PostDto.builder()
                .authorId(container.authorId())
                .content(container.content())
                .resourceIds(container.resourceIds())
                .build();
        UserDto userDto = UserDto.builder()
                .menteesIds(List.of(1L, 3L))
                .build();

        Post postEntity = mapper.toEntity(inputDto);

        Post preparedPost = Post.builder()
                .authorId(container.authorId())
                .content(container.content())
                .resources(container.resources())
                .build();

        Post createdPost = Post.builder()
                .id(container.postId())
                .authorId(container.authorId())
                .content(container.content())
                .resources(container.resources())
                .build();

        when(preparer.prepareForCreate(inputDto, postEntity)).thenReturn(preparedPost);
        when(postRepository.save(preparedPost)).thenReturn(createdPost);
        when(userClient.getUser(anyLong())).thenReturn(userDto);
        when(redissonClient.getMap(version)).thenReturn(versionMap);

        // when
        postService.create(inputDto);

        // then
        verify(postRepository, times(1)).save(preparedPost);
    }

    @Test
    void testPublishTwice() {
        // given
        Long postId = container.postId();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
        doThrow(PostAlreadyPublishedException.class).when(validator).validatePublished(entity);

        // then
        assertThrows(PostAlreadyPublishedException.class, () -> postService.publish(postId));
    }

    @Test
    void testPublish() {
        // given
        Long postId = entity.getId();
        boolean isNotPublished = container.published();

        Post entity = Post.builder()
                .id(postId)
                .published(isNotPublished)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));

        Post publishedPost = Post.builder()
                .id(postId)
                .publishedAt(container.publishedAt())
                .published(!isNotPublished)
                .build();
        when(preparer.prepareForPublish(entity)).thenReturn(publishedPost);

        // when
        postService.publish(postId);

        // then
        verify(postRepository, times(1)).save(publishedPost);
    }

    @Test
    void testUpdateWithInvalidData() {
        // given
        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        doThrow(PostWithTwoAuthorsException.class).when(validator).validateBeforeUpdate(dto, entity);

        // then
        assertThrows(PostWithTwoAuthorsException.class, () -> postService.update(dto));
    }

    @Test
    void testUpdateSuccessfully() {
        // given
        Post updatedEntity = Post.builder()
                .id(container.postId())
                .build();

        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        when(preparer.prepareForUpdate(dto, entity)).thenReturn(updatedEntity);

        // when
        postService.update(dto);

        // then
        verify(postRepository, times(1)).save(updatedEntity);
    }

    @Test
    void testAlreadyDeleted() {
        // given
        Long postId = container.postId();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
        doThrow(PostAlreadyDeletedException.class).when(validator).validateDeleted(entity);

        // then
        assertThrows(PostAlreadyDeletedException.class, () -> postService.delete(postId));
    }

    @Test
    void testDelete() {
        // given
        Long postId = container.postId();
        boolean isNotDeleted = container.deleted();

        Post entity = Post.builder()
                .id(postId)
                .deleted(isNotDeleted)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));

        Post deletedEntityExp = Post.builder()
                .id(postId)
                .deleted(!isNotDeleted)
                .build();

        // when
        postService.delete(postId);

        // then
        verify(postRepository, times(1)).save(captorPost.capture());
        Post deletedEntity = captorPost.getValue();
        deletedEntityExp.setUpdatedAt(deletedEntity.getUpdatedAt());
        assertEquals(deletedEntityExp, deletedEntity);
    }

    @Test
    void testGetPost() {
        // given
        Long postId = dto.getId();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));

        // when
        PostDto actualDto = postService.getPost(postId);

        // then
        assertEquals(dto, actualDto);
        verify(kafkaPostViewProducer, times(1)).publish(mapper.toKafkaPostViewDto(dto));
    }

    @Test
    void testGetPostNotFound() {
        // given
        Long postId = dto.getId();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // then
        assertThrows(RuntimeException.class, () -> postService.getPost(postId));
    }

    @Test
    void testGetFilteredPosts() {
        // given
        List<Post> posts = createPosts();
        PostFilterDto filters = new PostFilterDto(3L, null, false, false);
        when(postRepository.findAll()).thenReturn(posts);
        int expSize = 1;
        PostDto expDto = mapper.toDto(posts.get(4));

        // when
        List<PostDto> filteredPosts = postService.getFilteredPosts(filters);

        // then
        assertEquals(expSize, filteredPosts.size());
        assertEquals(expDto, filteredPosts.get(0));

    }

    @Test
    void testGetPostsByAuthorIds() {
        //given
        List<Long> authorIds = List.of(1L, 2L);
        long startPostId = 3L;
        int batchSize = 2;
        List<Post> posts = createPosts();
        when(postRepository.findPostsByAuthorIds(authorIds, startPostId, PageRequest.of(0, batchSize)))
                .thenReturn(posts.subList(0, 2));
        List<PostDto> expDtos = List.of(mapper.toDto(posts.get(0)), mapper.toDto(posts.get(1)));

        //when
        List<PostDto> actualDtos = postService.getPostsByAuthorIds(authorIds, startPostId, batchSize);

        //then
        assertEquals(expDtos, actualDtos);
    }

    @Test
    void testGetPostsByAuthorIdsEmpty() {
        //given
        List<Long> authorIds = List.of(1L, 2L);
        long startPostId = 3L;
        int batchSize = 2;
        when(postRepository.findPostsByAuthorIds(authorIds, startPostId, PageRequest.of(0, batchSize)))
                .thenReturn(List.of());

        //when
        List<PostDto> actualDtos = postService.getPostsByAuthorIds(authorIds, startPostId, batchSize);

        //then
        assertEquals(List.of(), actualDtos);
    }

    private List<Post> createPosts() {
        Long userId = container.authorId();
        Long projectId = container.projectId();
        boolean isNotDeleted = container.deleted();
        boolean isNotPublished = container.published();
        Long postId = container.postId();
        List<Like> likes = container.likes();

        Post postFirst = createPost(postId++, userId, likes, null, isNotDeleted, isNotPublished, null);
        Post postSecond = createPost(postId++, userId, likes, null, isNotDeleted, !isNotPublished, LocalDateTime.now());
        Post postThird = createPost(postId++, userId, likes, null, !isNotDeleted, isNotPublished, null);
        Post postForth = createPost(postId++, userId++, likes, null, !isNotDeleted, !isNotPublished, LocalDateTime.now());
        Post postFifth = createPost(postId++, userId, likes, null, isNotDeleted, isNotPublished, null);
        Post postSixth = createPost(postId++, null, likes, projectId++, isNotDeleted, isNotPublished, null);
        Post postSeventh = createPost(postId, null, likes, projectId, isNotDeleted, isNotPublished, null);
        return List.of(postFirst, postSecond, postThird, postForth, postFifth, postSixth, postSeventh);
    }

    private Post createPost(Long postId, Long authorId, List<Like> likes, Long projectId, boolean deleted, boolean published, LocalDateTime publishedAt) {
        return Post.builder()
                .id(postId)
                .authorId(authorId)
                .projectId(projectId)
                .deleted(deleted)
                .published(published)
                .publishedAt(publishedAt)
                .likes(likes)
                .build();
    }

}
