package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserService userService;
    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;

    @Transactional
    public LikeDto createPostLike(LikeDto dto) {
        likeValidator.validateLikeCreationParams(dto);
        Like entity = likeMapper.toEntity(dto);
        likeRepository.save(entity);
        return likeMapper.toDto(entity);
    }

    @Transactional
    public void removePostLike(LikeDto dto) {
        userService.getUserDtoById(dto.userId());
        likeValidator.checkLikeBeforeDelete(dto);
        likeRepository.deleteLikeByPostIdAndUserId(dto.postId(), dto.userId());
    }

    @Transactional
    public LikeDto createCommentLike(LikeDto dto) {
        likeValidator.validateLikeCreationParams(dto);
        Like like = likeMapper.toEntity(dto);
        likeRepository.save(like);
        return likeMapper.toDto(like);
    }

    @Transactional
    public void removeCommentLike(LikeDto dto) {
        userService.getUserDtoById(dto.userId());
        likeValidator.checkLikeBeforeDelete(dto);
        likeRepository.deleteLikeByCommentIdAndUserId(dto.commentId(), dto.userId());
    }

}
