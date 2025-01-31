package faang.school.postservice.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@RedisHash(value = "author", timeToLive = 86400)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCache implements Serializable {
    @Id
    private long id;
    private String username;
    private String email;
}
