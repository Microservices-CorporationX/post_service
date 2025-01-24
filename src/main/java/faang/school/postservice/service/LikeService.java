package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostService postService;
    private final CommentService commentService;

    private final UserService userService;

    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;

    @Transactional
    public LikeDto createPostLike(long postId, LikeDto dto) {
        likeValidator.verifyDtoParams(dto);
        Like entity = likeMapper.toEntity(dto);
        likeRepository.save(entity);
        return (likeMapper.toDto(entity));
    }

    @Transactional
    public void removePostLike(long idPost, LikeDto dto) {
        userService.getUserDtoById(dto.userId()); //проверка наличия юзера - при необходимости возвращает объект
        Like like = postService.getPostById(idPost).getLikes()
                .stream()
                .filter(user -> user.getUserId().equals(dto.userId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Лайк к посту с ID " + idPost + " не найден"));
        likeRepository.delete(like);
    }

    @Transactional
    public LikeDto createCommentLike(long commentId, LikeDto dto) {
        likeValidator.verifyDtoParams(dto);
        Like like = likeMapper.toEntity(dto);
        likeRepository.save(like);
        return likeMapper.toDto(like);
    }

    @Transactional
    public void removeCommentLike(long idComment, LikeDto dto) {
        userService.getUserDtoById(dto.userId()); //проверка наличия юзера - при необходимости возвращает объект
        Like like = commentService.getCommentById(idComment).getLikes()
                .stream()
                .filter(user -> user.getUserId().equals(dto.userId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Лайк к комментарию с ID " + idComment + " не найден"));

        likeRepository.delete(like);
    }

}
