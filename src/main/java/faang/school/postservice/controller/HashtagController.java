package faang.school.postservice.controller;

import faang.school.postservice.dto.hashtag.HashtagCreateDto;
import faang.school.postservice.dto.hashtag.HashtagReadDto;
import faang.school.postservice.service.HashtagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HashtagController {
    private final HashtagService hashtagService;

    @PostMapping("/hashtags")
    public HashtagReadDto create(@Valid HashtagCreateDto createDto) {
        return hashtagService.create(createDto);
    }

    @GetMapping("/hashtags/{hashtagId}")
    public HashtagReadDto getHashtag(@PathVariable long hashtagId) {
        return hashtagService.getHashtag(hashtagId);
    }

    @GetMapping("/hashtags/posts/{postId}")
    public List<HashtagReadDto> getHashtagsByPostId(long postId) {
        return hashtagService.getHashtagsByPostId(postId);
    }

    @DeleteMapping("/hashtags/{hashtagId}")
    public void remove(@PathVariable long hashtagId) {
        hashtagService.remove(hashtagId);
    }
}
