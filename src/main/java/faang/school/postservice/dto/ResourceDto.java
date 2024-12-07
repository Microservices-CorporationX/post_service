package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResourceDto {
    @NotNull(message = "Id must not be null")
    private Long id;
    @NotNull(message = "key must not be null")
    private String key;
    private long size;
    private LocalDateTime createdAt;
    private String name;
    private String type;
    private long postId;
}