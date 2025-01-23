package faang.school.postservice.model.redishash;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("User")
public class UserHash implements Serializable {
  @Id
  private Long id;
  @NotBlank(message = "Name should not be blank")
  private String username;
  @Email(message = "Email must be in right format")
  private String email;
}

