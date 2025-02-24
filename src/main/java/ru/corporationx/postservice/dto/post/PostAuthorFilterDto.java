package ru.corporationx.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAuthorFilterDto {
    private Long projectId;
    private Long authorId;
    private boolean published;
}
