package faang.school.postservice.config.verification.content;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "moderation.targets")
@Data
public class ModerationTargets {
    public static double SEXUAL;
    public static double DISCRIMINATORY;
    public static double INSULTING;
    public static double VIOLENT;
    public static double TOXIC;
}
