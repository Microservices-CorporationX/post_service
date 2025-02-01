package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDTO;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
@Validated
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public AlbumDTO createAlbum(@Valid @RequestBody AlbumDTO albumDTO) {
        return albumService.save(albumDTO);
    }

    @PutMapping("/{albumId}/add-post/{postId}")
    public void addPostToAlbum(@PathVariable Long albumId,
                               @PathVariable Long postId,
                               @RequestHeader(name = "x-user-id") Long userId) {
        albumService.addPostToAlbum(albumId, postId, userId);
    }

    @DeleteMapping("/{albumId}/delete-post/{postId}")
    public void deletePostToAlbum(@PathVariable Long albumId,
                                  @PathVariable Long postId,
                                  @RequestHeader(name = "x-user-id") Long userId) {
        albumService.deletePostToAlbum(albumId, postId, userId);
    }

    @PutMapping("/{albumId}/add-favourite")
    public void addAlbumToFavourite(@PathVariable Long albumId,
                                    @RequestHeader(name = "x-user-id") Long userId) {
        albumService.addAlbumToFavourite(albumId, userId);
    }

    @DeleteMapping("/{albumId}/delete-favourite")
    public void deleteAlbumToFavourite(@PathVariable Long albumId,
                                       @RequestHeader(name = "x-user-id") Long userId) {
        albumService.deleteAlbumToFavourite(albumId, userId);
    }

    @GetMapping("/{albumId}")
    public AlbumDTO getById(@PathVariable Long albumId) {
        return albumService.getById(albumId);
    }

    @GetMapping
    public List<AlbumDTO> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDate createdAt,
            @RequestParam(required = false) Long authorId) {
        return albumService.getAll(title, createdAt, authorId);
    }

    @GetMapping("/my")
    public List<AlbumDTO> getAllMyAlbums(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDate createdAt,
            @RequestHeader(name = "x-user-id") Long userId) {
        return albumService.getAllMyAlbums(title, createdAt, userId);
    }

    @GetMapping("/my/favourites")
    public List<AlbumDTO> getAllMyFavouriteAlbums(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDate createdAt,
            @RequestHeader(name = "x-user-id") Long userId) {
        return albumService.getFavoriteAlbums(title, createdAt, userId);
    }

    @PutMapping("/{albumId}")
    public AlbumDTO update(@PathVariable Long albumId,
                           @RequestBody @Valid AlbumDTO albumDTO,
                           @RequestHeader(name = "x-user-id") Long userId) {
        return albumService.update(albumDTO, userId, albumId);
    }

    @DeleteMapping("/{albumId}")
    public void delete(@PathVariable Long albumId,
                           @RequestHeader(name = "x-user-id") Long userId) {
        albumService.delete(userId, albumId);
    }
}