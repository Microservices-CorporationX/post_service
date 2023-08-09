package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
public class AlbumController {
    private final AlbumService service;

    @PostMapping
    public AlbumDto createAlbum(@Validated @RequestBody AlbumDto albumDto) {
        return service.createAlbum(albumDto);
    }

    @PostMapping("/{albumId}/{postId}")
    public AlbumDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        return service.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping("/{albumId}/{postIdToDelete}")
    public void deletePostFromAlbum(@PathVariable long albumId, @PathVariable long postIdToDelete) {
        service.deletePostFromAlbum(albumId, postIdToDelete);
    }

    @GetMapping("/{id}")
    public AlbumDto getAlbum(@PathVariable long id) {
        return service.getAlbum(id);
    }
}
