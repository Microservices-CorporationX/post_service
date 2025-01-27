package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.service.post.HashtagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/hashtag")
public class HashtagController {
    private final HashtagService hashtagService;

    @GetMapping
    public ResponseEntity<List<HashtagResponseDto>> getAllHashtags() {
        List<HashtagResponseDto> allHashtags = hashtagService.getAllHashtags();
        return new ResponseEntity<>(allHashtags, HttpStatus.OK);
    }

    @GetMapping("/top")
    public ResponseEntity<List<HashtagResponseDto>> getTopHashtags() {
        List<HashtagResponseDto> topHashtags = hashtagService.getTopHashtags();
        return new ResponseEntity<>(topHashtags, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> addHashtagToPost(@RequestBody @Valid HashtagRequestDto hashtagRequestDto) {
        hashtagService.addHashtag(hashtagRequestDto);
        return ResponseEntity.ok().build();
    }
}
