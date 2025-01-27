package faang.school.postservice.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class UserForPostEventDto {

    private Long id;

    private String username;
    private String email;
    private String aboutMe;
    private String phone;
    private String countryTitle;
    private Integer experience;
    private List<Long> followers;

}
