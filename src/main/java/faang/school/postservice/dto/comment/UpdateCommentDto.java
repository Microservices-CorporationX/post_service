package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Positive;

public class UpdateCommentDto {

    @Positive
    Long id;

    @Positive
    Long authorId;
}
