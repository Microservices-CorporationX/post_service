package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController implements AlbumControllerOas {

    private final AlbumService albumService;

    @PostMapping
    public AlbumDto create(@Valid @RequestBody AlbumDto albumDto) {
        return albumService.create(albumDto);
    }

    @PutMapping("/{albumId}/add")
    public AlbumDto addPost(@PathVariable Long albumId, @RequestParam("post") Long postId) {
        return albumService.addPost(albumId, postId);
    }

    @DeleteMapping("/{albumId}/delete")
    public AlbumDto deletePost(@PathVariable Long albumId, @RequestParam("post") Long postId) {
        return albumService.deletePost(albumId, postId);
    }

    @PostMapping("/toFavorites")
    public void addAlbumToFavorites(@RequestParam("album") Long albumId,
                                    @RequestParam("user") Long userId) {
        albumService.addAlbumToFavorites(albumId, userId);
    }

    @PostMapping("/fromFavorites")
    public void removeAlbumFromFavorites(@RequestParam("album") Long albumId,
                                         @RequestParam("user") Long userId) {
        albumService.removeAlbumFromFavorites(albumId, userId);
    }

    @GetMapping("/{albumId}")
    public AlbumDto get(@PathVariable Long albumId) {
        return albumService.get(albumId);
    }

    @GetMapping("/all")
    public List<AlbumDto> getAll(@RequestParam("user") Long userId,
                                 @RequestBody AlbumFilterDto filter) {
        return albumService.getAlbums(userId, filter);
    }

    @GetMapping("/favorites")
    public List<AlbumDto> getFavorites(@RequestParam("user") Long userId,
                                       @RequestBody AlbumFilterDto filter) {
        return albumService.getFavorites(userId, filter);
    }

    @PostMapping("/save")
    public AlbumDto update(@Valid @RequestBody AlbumDto albumDto) {
        return albumService.update(albumDto);
    }

    @DeleteMapping
    public void delete(@RequestParam("album") Long albumId,
                       @RequestParam("user") Long userId) {
        albumService.delete(albumId, userId);
    }
}
