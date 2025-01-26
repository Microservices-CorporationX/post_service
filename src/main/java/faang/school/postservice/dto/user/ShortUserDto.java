package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortUserDto {
    private Long id;
    private String username;
    private String email;
}
