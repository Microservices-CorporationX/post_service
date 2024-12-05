package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Controller
public class PostController {

    @GetMapping
    @Operation(summary = "получить все посты по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Посты успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Пост не найден")
    })
    public List<PostDto> getAllPosts() {
        return List.of(new PostDto(1L, 11L, 111L, "checked", false, LocalDateTime.now(), LocalDateTime.now()
                , LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пост по ID")
    public PostDto getById(@PathVariable Long id) {

        return new PostDto(id, 22L, 222L, "unchecked", false, LocalDateTime.now(),
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
    }

    @PostMapping
    @Operation(summary = "создать пост и название")
    public PostDto createPost(@RequestBody PostDto postDto) {
        postDto.setId(123L);
        postDto.setContent("Пост создан для теста");
        return postDto;
    }
}

