package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.OutboxEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.OutboxEventRepository;
import faang.school.postservice.utils.Helper;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private static final int BATCH_SIZE = 100;

    private final String AGGREGATE_TYPE = "Post";
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;
    private final PostService postService;
    private final CommentService commentService;
    private final CommentValidator commentValidator;
    private final PostValidator postValidator;
    private final UserValidator userValidator;
    private final OutboxEventRepository outboxEventRepository;
    private final Helper helper;

    public List<UserDto> getUsersWhoLikePostByPostId(long Id) {
        List<Like> usersWhoLikedPost = likeRepository.findByPostId(Id);
        return mapLikesToUserDtos(usersWhoLikedPost);
    }

    public List<UserDto> getUsersWhoLikeComments(long id) {
        List<Like> usersWhoLikedComment = likeRepository.findByCommentId(id);
        return mapLikesToUserDtos(usersWhoLikedComment);
    }

    @Transactional
    public LikeResponseDto addLikeToPost(Long postId, LikeDto likeDto) {
        postValidator.validatePostExistsById(postId);
        userValidator.checkUserExistence(likeDto.getUserId());
        commentValidator.validateCommentExistsById(likeDto.getCommentId());

        log.info("Adding like to post: {} by user: {}", postId, likeDto.getUserId());

        Post post = postService.getPostById(postId);

        Like like = Like.builder()
                .userId(likeDto.getUserId())
                .post(post)
                .comment(commentService.getCommentById(likeDto.getCommentId()))
                .build();

        likeRepository.save(like);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(postId)
                .aggregateType(AGGREGATE_TYPE)
                .eventType(LikeEvent.class.getSimpleName())
                .payload(createAndSerializeLikeEvent(likeDto, post.getAuthorId(), postId))
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();

        outboxEventRepository.save(outboxEvent);

        return likeMapper.toDto(like);
    }

    private List<UserDto> mapLikesToUserDtos(List<Like> usersWhoLiked) {
        List<Long> userIds = usersWhoLiked.stream()
                .map(Like::getUserId)
                .toList();

        List<List<Long>> userIdBatches = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            userIdBatches.add(userIds.subList(i, endIndex));
        }

        return userIdBatches.parallelStream()
                .flatMap(batch -> fetchUserDtos(batch).stream())
                .collect(Collectors.toList());
    }

    private List<UserDto> fetchUserDtos(List<Long> batch) {
        try {
            return userServiceClient.getUsersByIds(batch);
        } catch (Exception ex) {
            log.error("Error fetching users for batch {}: {}", batch, ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    private LikeEvent createEvent(LikeDto likeDto, Long authorId, Long postId) {
        return LikeEvent.builder()
                .likeAuthorId(likeDto.getUserId())
                .postId(postId)
                .postAuthorId(authorId)
                .build();
    }

    private String createAndSerializeLikeEvent(LikeDto likeDto, Long authorId, Long postId) {
        LikeEvent event = createEvent(likeDto, authorId, postId);
        return helper.serializeToJson(event);
    }
}
