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

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;

    @Transactional
    public void createPostLike(LikeDto dto) {
        likeValidator.verifyDtoParams(dto);
        Like entity = likeMapper.toEntity(dto);
        likeRepository.save(entity);

        log.info("Лайк успешно создан: пользователь ID = {}, пост ID = {}",
                dto.userId(), dto.postId());
    }

    @Transactional
    public void removePostLike(long idPost) {
        Like like = likeValidator.findPostById(idPost)
                .getLikes()
                .stream()
                .filter(likeItem -> likeItem.getPost().getId().equals(idPost))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Лайк к посту с ID " + idPost + " не найден"));
        likeRepository.delete(like);

        log.info("Лайк к посту с ID = {} - удален!", idPost);
    }

    @Transactional
    public void createCommentLike(LikeDto dto) {
        likeValidator.verifyDtoParams(dto);
        Like like = likeMapper.toEntity(dto);
        likeRepository.save(like);

        log.info("Лайк успешно создан: пользователь ID = {}, комментарий ID = {}",
                dto.userId(), dto.commentId());
    }

    @Transactional
    public void removeCommentLike(long idComment) {
        Like like = likeValidator.findCommentById(idComment)
                .getLikes()
                .stream()
                .filter(commentItem -> commentItem.getComment().getId().equals(idComment))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Лайк к комментарию с ID " + idComment + " не найден"));
        likeRepository.delete(like);

        log.info("Лайк к комментарию с ID = {} - удален!", idComment);
    }
}
