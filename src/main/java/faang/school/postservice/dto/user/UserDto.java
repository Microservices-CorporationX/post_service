package faang.school.postservice.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    @NotNull(message = "ID should not be null")
    @Positive(message = "ID must be positive")
    private Long id;

    @NotBlank(message = "Username should not be blank")
    @Size(max = 255, message = "Username should not exceed 255 characters")
    private String username;

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email should be valid")
    @Size(max = 254, message = "Email should not exceed 254 characters")
    private String email;
}
