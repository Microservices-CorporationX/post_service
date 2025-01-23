package faang.school.postservice.service.impl;

import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestData {
    static List<Post> getSomePosts()
    {
        List<Post> posts = new ArrayList<>();

        Post post1 = Post.builder()
                .id(1L)
                .content("Post 1")
                .authorId(111L)
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post1);
        Post post2 = Post.builder()
                .id(2L)
                .content("Post 2")
                .authorId(111L)
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post2);
        Post post3 = Post.builder()
                .id(3L)
                .content("Post 3")
                .authorId(112L)
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post3);
        Post post4 = Post.builder()
                .id(4L)
                .content("Post 4")
                .authorId(112L)
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post4);
        Post post5 = Post.builder()
                .id(5L)
                .content("Post 5")
                .projectId(222L)
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post5);
        Post post6 = Post.builder()
                .id(6L)
                .content("Post 6")
                .projectId(222L)
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post6);
        Post post7 = Post.builder()
                .id(7L)
                .content("Post 7")
                .projectId(223L)
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post7);
        Post post8 = Post.builder()
                .id(8L)
                .content("Post 8")
                .projectId(223L)
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post8);
        Post post9 = Post.builder()
                .id(9L)
                .content("Post 9")
                .authorId(111L)
                .published(false)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post9);
        Post post10 = Post.builder()
                .id(10L)
                .content("Post 10")
                .projectId(222L)
                .published(false)
                .createdAt(LocalDateTime.now())
                .build();
        posts.add(post10);

        return posts;
    }

}
