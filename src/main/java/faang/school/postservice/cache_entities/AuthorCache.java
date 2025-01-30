package faang.school.postservice.cache_entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

@RedisHash(value = "Author")
@Data
public class AuthorCache implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private long userId;
    private String username;
}