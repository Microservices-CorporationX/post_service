package faang.school.postservice.dto.resource;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class ResourceInfoDto {
    private String name;
    private String type;
    @NonNull
    private String key;
    @NonNull
    private byte[] bytes;
}


