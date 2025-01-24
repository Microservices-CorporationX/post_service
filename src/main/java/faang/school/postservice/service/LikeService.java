package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.like.LikeValidator;
import faang.school.postservice.validator.like.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeValidator likeValidator;
    private final UserValidator userValidator;

    public Like findById(@NotNull Long likeId) {
        return likeRepository.findById(likeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Лайка по такому %d не существует", likeId))
                );
    }

    public LikePostDto userLikeThePost(LikePostDto dto) {
        likeValidator.validateLikePost(dto);
        userValidator.validateUser(dto.userId());

        return likeThePost(dto);
    }

    public LikeCommentDto userLikeTheComment(LikeCommentDto dto) {
        likeValidator.validateLikeComment(dto);
        userValidator.validateUser(dto.userId());

        return likeTheComment(dto);
    }

    @Transactional
    public LikePostDto removeLikePost(Long likeId, LikePostDto dto) {
        likeValidator.validateLikePost(dto);
        userValidator.validateUser(dto.userId());

        if (!likeMapper.toPostDto(findById(likeId)).equals(dto)) {
            throw new BusinessException("Пользователь не может удалить лайк на этом посту");
        }

        likeRepository.deleteByPostIdAndUserId(dto.postId(), dto.userId());

        return dto;
    }

    @Transactional
    public LikeCommentDto removeLikeComment(Long likeId, LikeCommentDto dto) {
        likeValidator.validateLikeComment(dto);
        userValidator.validateUser(dto.userId());

        if (!likeMapper.toCommentDto(findById(likeId)).equals(dto)) {
            throw new BusinessException("Пользователь не может удалить лайк на этом комментарии");
        }

        likeRepository.deleteByCommentIdAndUserId(dto.commentId(), dto.userId());

        return dto;
    }

    private LikePostDto likeThePost(LikePostDto dto) {
        if (likeRepository
                .findByPostIdAndUserId(dto.postId(), dto.userId())
                .isPresent()) {
            throw new BusinessException("Под постом у юзера уже стоит лайк");
        }

        Like like = likeMapper.toEntity(dto);
        like.setPost(postService.findById(dto.postId()));

        return likeMapper.toPostDto(likeRepository.save(like));
    }

    private LikeCommentDto likeTheComment(LikeCommentDto dto) {
        if (likeRepository
                .findByCommentIdAndUserId(dto.commentId(), dto.userId())
                .isPresent()) {
            throw new BusinessException("Под комментом у юзера уже стоит лайк");
        }

        Like like = likeMapper.toEntity(dto);
        like.setComment(commentService.findById(dto.commentId()));

        return likeMapper.toCommentDto(likeRepository.save(like));
    }
}
