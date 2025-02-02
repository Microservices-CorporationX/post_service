package faang.school.postservice.util;

import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.model.post.Post;

import java.util.List;

public class HashtagPrepareData {

    public static HashtagRequestDto buildNewHashtagRequestDto() {
        return HashtagRequestDto.builder()
                .postId(1L)
                .hashtag("new")
                .build();
    }

    public static Post getPost() {
        return Post.builder()
                .id(1L)
                .hashtags(List.of(buildHashtag()))
                .build();
    }

    public static Hashtag buildNewHashtag() {
        return Hashtag
                .builder()
                .id(1L)
                .name("new")
                .build();
    }

    public static Hashtag buildHashtag() {
        return Hashtag
                .builder()
                .id(1L)
                .name("hashtag")
                .build();
    }

}
