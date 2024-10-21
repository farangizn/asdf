package org.example.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import org.example.dto.PostDTO;
import org.example.dto.PostInputPTO;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.interfaces.UserService;
import org.example.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@QuarkusTest
public class PostServiceImplTest {

    @Inject
    PostServiceImpl postService;

    @InjectMock
    PostRepository postRepository;

    Post post;
    Post post1;
    User user;
    PostDTO postDTO;
    PostInputPTO postInputPTO;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setTitle("post title");
        post.setBody("post body");

        post1 = new Post();
        post1.setId(2L);
        post1.setTitle("post 2 title");
        post1.setBody("post 2 body");

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email");

        post.setUser(user);
        post1.setUser(user);

        postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setTitle("post title");
        postDTO.setBody("post body");
        postDTO.setUserId(1L);

    }

    @Test
    void findAll() {
        Mockito.when(postRepository.listAll()).thenReturn(List.of(post, post1));

        List<Post> posts = postService.findAll();

        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertEquals(posts.get(0).getTitle(), post.getTitle());
        assertEquals(posts.get(0).getBody(), post.getBody());
        assertEquals(posts.get(0).getUser().getUsername(), post.getUser().getUsername());
        assertEquals(posts.get(1).getTitle(), post1.getTitle());
        assertEquals(posts.get(1).getBody(), post1.getBody());
        verify(postRepository, times(1)).listAll();
    }
//
//    @Inject
//    PostServiceImpl postService;
//
//    @InjectMock
//    PostRepository postRepository;
//
//    @InjectMock
//    UserService userService;
//
//    @Test
//    public void testFindAllPosts() {
//        Post post = new Post();
//        post.setId(1L);
//        post.setTitle("Test Title");
//        post.setBody("Test Body");
//
//        Mockito.when(postRepository.listAll()).thenReturn(List.of(post));
//
//        List<Post> posts = postService.findAll();
//
//        assertNotNull(posts);
//        assertEquals(1, posts.size());
//        assertEquals("Test Title", posts.get(0).getTitle());
//    }
//
//    @Test
//    public void testFindPostById_Found() {
//        Post post = new Post();
//        post.setId(1L);
//        post.setTitle("Test Title");
//        post.setBody("Test Body");
//
//        Mockito.when(postRepository.findByIdOptional(1L)).thenReturn(Optional.of(post));
//
//        Optional<Post> foundPost = postService.findById(1L);
//
//        assertTrue(foundPost.isPresent());
//        assertEquals("Test Title", foundPost.get().getTitle());
//        Mockito.verify(postRepository, times(1)).findByIdOptional(1L);
//    }
//
//    @Test
//    public void testFindPostById_NotFound() {
//
//        Mockito.when(postRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
//
//        Optional<Post> foundPost = postService.findById(1L);
//
//        assertFalse(foundPost.isPresent());
//        Mockito.verify(postRepository, times(1)).findByIdOptional(1L);
//    }
//
//    @Test
//    public void testSavePost_Success() {
//        User user = new User();
//        user.setId(1L);
//        user.setName("John Doe");
//
//        PostInputPTO postInputPTO = new PostInputPTO();
//        postInputPTO.setTitle("Test Title");
//        postInputPTO.setBody("Test Body");
//        postInputPTO.setUserId(1L);
//
//        Mockito.when(userService.findById(1L)).thenReturn(Optional.of(user));
//
//        Post post = new Post();
//        post.setTitle(postInputPTO.getTitle());
//        post.setBody(postInputPTO.getBody());
//        post.setUser(user);
//
//        Post savedPost = postService.save(postInputPTO);
//
//        assertNotNull(savedPost);
//        assertEquals("Test Title", savedPost.getTitle());
//        Mockito.verify(userService, times(1)).findById(1L);
//        Mockito.verify(postRepository, times(1)).persist(savedPost);
//    }
//
//    @Test
//    public void testSavePost_UserNotFound() {
//        PostInputPTO postInputPTO = new PostInputPTO();
//        postInputPTO.setTitle("Test Title");
//        postInputPTO.setBody("Test Body");
//        postInputPTO.setUserId(1L);
//
//        Mockito.when(userService.findById(1L)).thenReturn(Optional.empty());
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> postService.save(postInputPTO));
//        assertEquals("User with ID 1 not found.", exception.getMessage());
//        Mockito.verify(userService, times(1)).findById(1L);
//        Mockito.verify(postRepository, never()).persist(Mockito.any(Post.class));
//    }
//
//    @Test
//    public void testUpdatePost_Success() {
//        User user = new User();
//        user.setId(1L);
//        user.setName("John Doe");
//
//        Post post = new Post();
//        post.setId(1L);
//        post.setTitle("Old Title");
//        post.setBody("Old Body");
//        post.setUser(user);
//
//        PostInputPTO postInputPTO = new PostInputPTO();
//        postInputPTO.setTitle("New Title");
//        postInputPTO.setBody("New Body");
//        postInputPTO.setUserId(1L);
//
//        Mockito.when(postRepository.findByIdOptional(1L)).thenReturn(Optional.of(post));
//        Mockito.when(userService.findById(1L)).thenReturn(Optional.of(user));
//
//        postService.update(post, postInputPTO);
//
//        assertEquals("New Title", post.getTitle());
//        assertEquals("New Body", post.getBody());
//        verify(postRepository, times(1)).persist(post);
//    }
//
//    @Test
//    public void testUpdatePost_UserNotFound() {
//        Post post = new Post();
//        post.setId(1L);
//        post.setTitle("Old Title");
//        post.setBody("Old Body");
//        PostInputPTO postInputPTO = new PostInputPTO();
//        postInputPTO.setTitle("New Title");
//        postInputPTO.setBody("New Body");
//        postInputPTO.setUserId(1L);
//
//        Mockito.when(postRepository.findByIdOptional(1L)).thenReturn(Optional.of(post));
//        Mockito.when(userService.findById(1L)).thenReturn(Optional.empty());
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> postService.update(post, postInputPTO));
//        assertEquals("User not found", exception.getMessage());
//        verify(postRepository, never()).persist(post);
//    }
//
//    @Test
//    public void testDeletePost_Success() {
//        Mockito.when(postRepository.deleteById(1L)).thenReturn(true);
//
//        Boolean deleted = postService.deleteById(1L);
//
//        assertTrue(deleted);
//        verify(postRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    public void testDeletePost_NotFound() {
//        Mockito.when(postRepository.deleteById(1L)).thenReturn(false);
//
//        Boolean deleted = postService.deleteById(1L);
//
//        assertFalse(deleted);
//        verify(postRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    public void testFindAllPostsByUserId() {
//        Post post = new Post();
//        post.setId(1L);
//        post.setTitle("Test Title");
//        post.setBody("Test Body");
//
//        Mockito.when(postRepository.findAllByUserId(1L)).thenReturn(List.of(post));
//
//        List<Post> posts = postService.findAllByUserId(1L);
//
//        assertNotNull(posts);
//        assertEquals(1, posts.size());
//        assertEquals("Test Title", posts.get(0).getTitle());
//        verify(postRepository, times(1)).findAllByUserId(1L);
//    }
//
//    @Test
//    public void testDeleteAllPostsByUserId() {
//        postService.deleteAllByUserId(1L);
//
//        verify(postRepository, times(1)).deleteAllByUserId(1L);
//    }
//
}