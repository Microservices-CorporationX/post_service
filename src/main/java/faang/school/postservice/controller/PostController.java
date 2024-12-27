package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.PostResourceService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utilities.UrlUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POSTS)
public class PostController {

    private final PostService postService;
    private final PostResourceService postResourceService;

    @PostMapping
    public ResponseEntity<Long> draftPost(@RequestBody @Valid PostDto postDto) {
        checkIdUserOrIdProjectNotNull(postDto);
        Long postId = postService.createDraftPost(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    @PatchMapping(UrlUtils.ID)
    public ResponseEntity<PostDto> publishPost(@PathVariable("id") @Min(1) Long postId) {
        PostDto postDto = postService.publishPost(postId);
        return ResponseEntity.ok().body(postDto);
    }

    @PutMapping(UrlUtils.ID)
    public ResponseEntity<PostDto> updatePost(@PathVariable("id") @Min(1) Long postId, @RequestBody @Valid PostDto postDto) {
        checkIdUserOrIdProjectNotNull(postDto);
        PostDto postDtoUpdated = postService.updatePost(postId, postDto);
        return ResponseEntity.ok().body(postDtoUpdated);
    }

    @DeleteMapping(UrlUtils.ID)
    public ResponseEntity<Long> deletePost(@PathVariable("id") @Min(1) Long postId) {
        Long postDeletedId = postService.deletePost(postId);
        return ResponseEntity.ok().body(postDeletedId);
    }

    @GetMapping(UrlUtils.ID)
    public ResponseEntity<PostDto> getPost(@PathVariable("id") @Min(1) Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping(UrlUtils.USER + UrlUtils.DRAFT)
    public ResponseEntity<List<PostDto>> getDraftPostsByUserId(@RequestParam @Min(1) Long idUser) {
        return ResponseEntity.ok(postService.getDraftPostsForUser(idUser));
    }

    @GetMapping(UrlUtils.PROJECT + UrlUtils.DRAFT)
    public ResponseEntity<List<PostDto>> getDraftPostsByProjectId(@RequestParam @Min(1) Long idProject) {
        return ResponseEntity.ok(postService.getDraftPostsForProject(idProject));
    }

    @GetMapping(UrlUtils.USER + UrlUtils.PUBLISHED)
    public ResponseEntity<List<PostDto>> getPublishedPostsByUserId(@RequestParam @Min(1) Long idUser) {
        return ResponseEntity.ok(postService.getPublishedPostsForUser(idUser));
    }

    @GetMapping(UrlUtils.PROJECT + UrlUtils.PUBLISHED)
    public ResponseEntity<List<PostDto>> getPublishedPostsByProjectId(@RequestParam @Min(1) Long idProject) {
        return ResponseEntity.ok(postService.getPublishedPostForProject(idProject));
    }


    @PutMapping(UrlUtils.ID + UrlUtils.IMAGE)
    public ResponseEntity<ResourceDto> addImage(@PathVariable("id") @Min(1) Long postId, @RequestBody @NotNull MultipartFile image) {
        ResourceDto resourceDto = postResourceService.addPostImage(postId, image);
        return ResponseEntity.ok().body(resourceDto);
    }

    @DeleteMapping
    public ResponseEntity<Long> deleteImageByKey(@RequestParam @NotBlank String key) {
        Long idImage = postResourceService.deleteImageByKey(key);
        return ResponseEntity.ok().body(idImage);
    }

    @GetMapping(UrlUtils.ID + UrlUtils.IMAGE)
    public ResponseEntity<byte[]> getImageByKey(@PathVariable("id") @Min(1) Long postId, @RequestParam @NotBlank String key) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(postResourceService.getImageByKey(key));
    }

    @GetMapping(UrlUtils.ID + UrlUtils.IMAGE + UrlUtils.ALL)
    public List<ResponseEntity<byte[]>> getAllImagesByPostId(@PathVariable("id") @Min(1) Long postId) {
        List<byte[]> images = postResourceService.getAllImagesByPostId(postId);
        return images.stream()
                .map(image -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                        .body(image))
                .toList();
    }

    @DeleteMapping(UrlUtils.ID + UrlUtils.IMAGE + UrlUtils.ALL)
    public ResponseEntity<List<Long>> deleteAllImageByPostId(@PathVariable("id") @Min(1) Long postId) {
        List<Long> idImages = postResourceService.deleteAllPostImages(postId);
        return ResponseEntity.ok().body(idImages);
    }

    private void checkIdUserOrIdProjectNotNull(PostDto postDto) {
        if (postDto.projectId() == null && postDto.userId() == null) {
            log.error("idProject or idUser are NULL");
            throw new IllegalArgumentException("idProject or idUser are NULL");
        }
        if (postDto.projectId() != null && postDto.userId() != null) {
            log.error("idProject or idUser both has value");
            throw new IllegalArgumentException("idProject or idUser both has value");
        }
    }
}


