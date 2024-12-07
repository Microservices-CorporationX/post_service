package faang.school.postservice.controller.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.album.AlbumVisibility;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
@Validated
public class AlbumController {
    private final AlbumService albumService;
    private final UserContext userContext;

    @PutMapping("/add")
    public AlbumDto create(@RequestBody @Valid AlbumDto albumDto) {
        return albumService.createAlbum(albumDto, userContext.getUserId());
    }

    @PostMapping("/addPostToAlbum/{albumId}")
    public AlbumDto addPostToAlbum(@RequestBody Long postId, @PathVariable Long albumId) {
        return albumService.addPostToAlbum(postId, albumId, userContext.getUserId());
    }

    @PostMapping("/addToFavorites/{albumId}")
    public void addToFavorites(@PathVariable Long albumId) {
        albumService.addToFavorites(albumId, userContext.getUserId());
    }

    @PostMapping("/removeFromFavorites/{albumId}")
    public void removeFromFavorites(@PathVariable Long albumId) {
        albumService.removeFromFavorites(albumId, userContext.getUserId());
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@PathVariable Long albumId) {
        return albumService.getAlbum(albumId);
    }

    @GetMapping("/albums/{authorId}")
    public List<AlbumDto> getAlbums(@PathVariable Long authorId) {
        return albumService.getAlbums(authorId);
    }

    @PostMapping("/filteredAlbums")
    public List<AlbumDto> getFilteredAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAlbumsWithFilter(userContext.getUserId(), albumFilterDto);
    }

    @GetMapping("/allAlbums")
    public List<AlbumDto> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @PostMapping("/allAlbumsWithFilter")
    public List<AlbumDto> getAllAlbumsWithFilter(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllAlbumsWithFilter(albumFilterDto);
    }

    @PostMapping("/favoriteFilteredAlbums")
    public List<AlbumDto> getFavoriteFilteredAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getFavoriteFilteredAlbums(userContext.getUserId(), albumFilterDto);
    }

    @PostMapping("/update")
    public AlbumDto update(@RequestBody @Valid AlbumDto albumDto) {
        return albumService.update(albumDto);
    }

    @PutMapping("{albumId}/visibility")
    public AlbumDto updateVisibility(
            @PathVariable("albumId") Long albumId,
            @RequestParam("visibility") AlbumVisibility visibility,
            @RequestBody(required = false) List<Long> userIds) {
        return albumService.updateVisibility(albumId, visibility, userIds);
    }

    @DeleteMapping("/{albumId}")
    public void remove(@PathVariable Long albumId) {
        albumService.remove(albumId);
    }
}

