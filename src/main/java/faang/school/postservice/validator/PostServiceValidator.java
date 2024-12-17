package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.model.Post;


public class PostServiceValidator {
    public static void checkDtoValidAuthorOrProjectId(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new IllegalStateException("Нельзя создать пост без автора");
        }

        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new IllegalStateException("В посте не может быть 2 автора");
        }
    }

    public static void checkPostWasPosted(Post post) {
        if (post.isPublished()) {
            throw new IllegalStateException("Пост уже был опубликован");
        }
    }

    public static void checkThatUserOrProjectIsExist(
            PostDto postDto, UserServiceClient userServiceClient, ProjectServiceClient projectServiceClient) {
        if (postDto.getAuthorId() != null) {
            UserDto userDto = userServiceClient.getUserById(postDto.getAuthorId());
            int a =23;
        }
        if (postDto.getProjectId() != null) {
            projectServiceClient.getProject(postDto.getProjectId());
        }
    }

}
