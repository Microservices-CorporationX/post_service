package faang.school.postservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PostErrorMessages {
    public static final String EXTERNAL_ERROR_MESSAGE = "Error occurred from external service: ";

    public static final String POST_CANNOT_BE_NULL = "postId can't be null";
    public static final String POST_WITH_ID_NOT_FOUND = "Post with id %s not found";
    public static final String POST_WITH_ID_ALREADY_PUBLISHED = "Post with ID %s is already published";
    public static final String POSTS_BY_USER_ID_NOT_FOUND = "Posts by user ID: %s not found";
    public static final String POSTS_BY_PROJECT_ID_NOT_FOUND = "Posts by project ID: %s not found";
    public static final String POSTS_MUST_HAVE_ONE_AUTHOR = "Post must have exactly one author (either user or project).";

    public static final String PROJECT_WITH_ID_NOT_FOUND = "Project with ID %d not found";

    public static final String DRAFTS_BY_USER_ID_NOT_FOUND = "Drafts by user ID: %s not found";
    public static final String DRAFTS_BY_PROJECT_ID_NOT_FOUND = "Drafts by project ID: %s not found";

    public static final String USER_WITH_ID_NOT_FOUND = "User with ID %d not found";

}
