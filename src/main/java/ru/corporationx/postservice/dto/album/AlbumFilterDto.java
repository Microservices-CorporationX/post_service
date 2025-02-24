package ru.corporationx.postservice.dto.album;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlbumFilterDto {

    private String title;
    private LocalDateTime createdAt;
}
