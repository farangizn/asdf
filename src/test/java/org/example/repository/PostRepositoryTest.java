package org.example.repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.entity.Post;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PostRepositoryTest {

    @Inject
    PostRepository postRepository;

    @Test
    void findAllByUserId() {
        List<Post> posts = postRepository.findAllByUserId(1L);

        assertNotNull(posts);
        assertEquals(posts.get(0).getTitle(), "post 1");
        assertEquals(posts.get(0).getBody(), "body 1");
        assertEquals(posts.get(0).getUser().id, 1L);
        assertEquals(posts.get(1).getTitle(), "post 2");
        assertEquals(posts.get(1).getBody(), "body 2");
        assertEquals(posts.get(1).getUser().id, 1L);
    }

    @Test
    void findAllByUserIdEmpty() {
        List<Post> posts = postRepository.findAllByUserId(0L);
        assertNotNull(posts);
        assertEquals(0, posts.size());
    }

    @Test
    @Transactional
    void deleteAllByUserId() {
        postRepository.deleteAllByUserId(1L);
        assertEquals(0, postRepository.findAllByUserId(1L).size());
    }

}