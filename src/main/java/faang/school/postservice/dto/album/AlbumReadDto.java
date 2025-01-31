package faang.school.postservice.dto.album;

import faang.school.postservice.dto.post.PostReadDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AlbumReadDto {
    private long id;
    private String title;
    private String description;
    private List<PostReadDto> posts;
}
