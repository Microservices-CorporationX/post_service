package ru.corporationx.postservice.dto.resource;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private String type;
    private Long size;
    private Long postId;
    private LocalDateTime createdAt;
}
