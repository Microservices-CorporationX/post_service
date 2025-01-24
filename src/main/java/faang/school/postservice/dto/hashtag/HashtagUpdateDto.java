package faang.school.postservice.dto.hashtag;

import lombok.Builder;

import java.util.List;

//TODO Рассмотреть варианты использования, при возможности исключить
@Builder
public record HashtagUpdateDto(
        long id,
        String name,
        List<Long> postIds
) {
}
