package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AlbumController {

    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping("/albums")
    public AlbumReadDto createAlbum(
            @Valid @RequestBody AlbumCreateDto albumCreateDto) {
        return albumService.createAlbum(userContext.getUserId(), albumCreateDto);
    }

    @GetMapping("/albums/{albumId}")
    public AlbumReadDto getAlbumById(
            @PathVariable long albumId){

        return albumService.getAlbumById(albumId);
    }

    @GetMapping("/albums")
    public List<AlbumReadDto> getAllAlbums(@Valid @RequestBody AlbumFilterDto filter){
        return albumService.getAllAlbums(filter);
    }

    @GetMapping("/users/albums")
    public List<AlbumReadDto> getUserAlbums(@Valid @RequestBody AlbumFilterDto filter){
        return albumService.getUserAlbums(userContext.getUserId(), filter);
    }

    @PutMapping("/albums/{albumId}")
    public AlbumReadDto updateAlbum(
            @Valid @RequestBody AlbumUpdateDto albumUpdateDto,
            @PathVariable long albumId) {
        return albumService.updateAlbum(userContext.getUserId(), albumId, albumUpdateDto);
    }

    @DeleteMapping("/albums/{albumId}")
    public void deleteAlbum(
            @PathVariable long albumId ){
        albumService.deleteAlbum(userContext.getUserId(), albumId);
    }

    @PostMapping("/albums/{albumId}/posts/{postId}")
    public AlbumReadDto addPostToAlbum(
            @PathVariable long albumId,
            @PathVariable long postId ){
        return albumService.addPostToAlbum(userContext.getUserId(), albumId, postId);
    }

    @DeleteMapping("/albums/{albumId}/posts/{postId}")
    public AlbumReadDto removePostFromAlbum(
            @PathVariable long albumId,
            @PathVariable long postId ){
        return albumService.removePostFromAlbum(userContext.getUserId(), albumId, postId);
    }

    @PostMapping("/albums/{albumId}/favorites")
    public void addAlbumToFavorites(
            @PathVariable long albumId){
        albumService.addAlbumToFavorites(userContext.getUserId(), albumId);
    }

    @DeleteMapping("/albums/{albumId}/favorites")
    public void removeAlbumFromFavorites(
            @PathVariable long albumId ){
        albumService.removeAlbumFromFavorites(userContext.getUserId(), albumId);
    }

    @GetMapping("/users/favorites")
    public List<AlbumReadDto> getFavoriteAlbums(AlbumFilterDto filter){
        return albumService.getFavoriteAlbums(userContext.getUserId(), filter);
    }


}
