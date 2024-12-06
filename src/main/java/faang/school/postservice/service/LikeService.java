package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private static final int BATCH_SIZE = 100;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeEventPublisher likeEventPublisher;

    public List<UserDto> getUsersWhoLikePostByPostId(long Id) {
        List<Like> usersWhoLikedPost = likeRepository.findByPostId(Id);
        return mapLikesToUserDtos(usersWhoLikedPost);
    }

    public List<UserDto> getUsersWhoLikeComments(long id) {
        List<Like> usersWhoLikedComment = likeRepository.findByCommentId(id);
        return mapLikesToUserDtos(usersWhoLikedComment);
    }

    public ResponseLikeDto addLikeToPost(LikeDto likeDto) {
        likeValidator.validatePostId(likeDto.getPostId());
        likeValidator.validateUserId(likeDto.getUserId());
        likeValidator.validateCommentId(likeDto.getCommentId());

        log.info("Adding like to post: {} by user: {}", likeDto.getPostId(), likeDto.getUserId());

        Post post = postService.getPostById(likeDto.getPostId());

        Like like = Like.builder()
                .userId(likeDto.getUserId())
                .post(post)
                .comment(commentService.getCommentById(likeDto.getCommentId()))
                .build();

        likeRepository.save(like);

        likeEventPublisher.publishLikeEvent(createEvent(likeDto, post.getAuthorId()));

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

    private LikeEvent createEvent(LikeDto likeDto, Long authorId) {
        return LikeEvent.builder()
                .likeAuthorId(likeDto.getUserId())
                .postId(likeDto.getPostId())
                .postAuthorId(authorId)
                .build();
    }
}
