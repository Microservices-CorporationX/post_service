package faang.school.postservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PostErrorMessages {
    public static final String POST_CANNOT_BE_NULL = "postId can't be null";
    public static final String POST_WITH_ID_NOT_FOUND = "Post with id %s not found";
    public static final String USER_WITH_ID_NOT_FOUND = "User with ID %d not found";
}
