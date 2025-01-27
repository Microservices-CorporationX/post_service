package faang.school.postservice.util;

import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.model.post.Post;

import java.util.List;
import java.util.UUID;

public class HashtagPrepareData {

    public static HashtagRequestDto buildNewHashtagRequestDto() {
        return HashtagRequestDto.builder().postId(1L).hashtag("new").build();
    }

    public static Post getPost(UUID uuid) {
        return Post.builder()
                .id(1L)
                .hashtags(List.of(buildHashtag(uuid)))
                .build();
    }

    public static Hashtag buildNewHashtag(UUID uuid) {
        return Hashtag
                .builder()
                .id(uuid)
                .name("new")
                .build();
    }

    public static Hashtag buildHashtag(UUID uuid) {
        return Hashtag
                .builder()
                .id(uuid)
                .name("hashtag")
                .build();
    }

}
