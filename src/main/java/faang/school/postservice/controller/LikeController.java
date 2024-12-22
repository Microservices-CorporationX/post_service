package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@Tag(name = "Контроллер для управления лайками")
public class LikeController {
    private final LikeService likeService;

    @Operation(summary = "Получение лайка по id")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Like retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LikeDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Like not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/{id}")
    public LikeDto getLikeById(@PathVariable long id) {
        return likeService.getLikeById(id);
    }

    @Operation(summary = "Получение всех лайков")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Likes retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LikeDto.class))}),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping
    public List<LikeDto> getAllLikes() {
        return likeService.getAllLikes();
    }

    @Operation(summary = "Добавление лайка")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Like added",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LikeDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public LikeDto addLike(@RequestBody LikeDto likeDto) {
        return likeService.addLike(likeDto);
    }

    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Like deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LikeDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Like not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @Operation(summary = "Удаление лайка")
    @DeleteMapping
    public void deleteLike(@RequestBody @Valid LikeDto likeDto) {
        likeService.deleteLike(likeDto);
    }

    @GetMapping("/users/{id}/post")
    public List<UserDto> getAllLikedByPostId(@PathVariable Long id) {
        return likeService.getAllLikedByPostId(id);
    }

    @GetMapping("/users/{id}/comment")
    public List<UserDto> getAllLikedByCommentId(@PathVariable Long id) {
        return likeService.getAllLikedByCommentId(id);
    }

}
