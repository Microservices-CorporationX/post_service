package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserNFDto {
    private Long id;
    private String username;
    private String email;
    private Locale locale;
}
