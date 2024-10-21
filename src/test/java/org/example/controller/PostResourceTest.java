package org.example.controller;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import org.example.dto.PostDTO;
import org.example.dto.PostInputPTO;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.handling.ErrorResponse;
import org.example.repository.PostRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PostResourceTest {

    @InjectMock
    PostRepository postRepository;

    @InjectMock
    UserRepository userRepository;

    @Inject
    PostResource postResource;

    User user;
    Post post;
    PostDTO postDTO;
    PostInputPTO postInputPTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setName("user");
        user.setEmail("email");

        post = new Post();
        post.setUser(user);
        post.setTitle("title");
        post.setBody("body");

        postDTO = new PostDTO();
        postDTO.setTitle("title");
        postDTO.setBody("body");
        postDTO.setUserId(user.id);

        postInputPTO = new PostInputPTO();
        postInputPTO.setUserId(1L);
        postInputPTO.setTitle("title");
        postInputPTO.setBody("body");
    }

    @Test
    void getAllPosts() {
        Mockito.when(postRepository.listAll()).thenReturn(List.of(post));

        Response response = postResource.getAllPosts();

        assertNotNull(response);
        assertEquals(response.getStatus(), 200);

        List<PostDTO> entity = (List<PostDTO>) response.getEntity();

        assertNotNull(entity);
        assertEquals(entity.get(0).getTitle(), post.getTitle());
        assertEquals(entity.get(0).getBody(), post.getBody());
        assertEquals(entity.get(0).getUserId(), user.id);
    }

    @Test
    void getPostById() {
        Mockito.when(postRepository.findByIdOptional(any())).thenReturn(Optional.of(post));

        Response response = postResource.getPostById(1L);

        assertNotNull(response);
        assertEquals(response.getStatus(), 200);

        PostDTO entity = (PostDTO) response.getEntity();

        assertNotNull(entity);
        assertEquals(entity.getId(), post.id);
        assertEquals(entity.getTitle(), post.getTitle());
        assertEquals(entity.getBody(), post.getBody());
        assertEquals(entity.getUserId(), user.id);
    }

    @Test
    void getPostByIdNotFound() {
        Mockito.when(postRepository.findByIdOptional(any())).thenReturn(Optional.empty());

        Response response = postResource.getPostById(1L);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());

        ErrorResponse entity = (ErrorResponse) response.getEntity();

        assertNotNull(entity);
        assertEquals(entity.getMessage(), "Post not found for ID: " + 1L);
    }

    @Test
    void createPost() {
        Mockito.doNothing().when(postRepository).persist(ArgumentMatchers.any(Post.class));
        Mockito.when(userRepository.findByIdOptional(any())).thenReturn(Optional.of(user));

        Response response = postResource.createPost(postInputPTO);

        assertNotNull(response);
        assertEquals(response.getStatus(), 201);

        PostDTO entity = (PostDTO) response.getEntity();
        assertNotNull(entity);
        assertEquals(entity.getTitle(), postInputPTO.getTitle());
        assertEquals(entity.getBody(), postInputPTO.getBody());
        assertEquals(entity.getUserId(), postInputPTO.getUserId());
    }

    @Test
    void createPostUserNotFound() {
        postInputPTO.setUserId(2L);

        Mockito.doNothing().when(postRepository).persist(ArgumentMatchers.any(Post.class));
        Mockito.when(userRepository.findByIdOptional(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postResource.createPost(postInputPTO));

    }

    @Test
    void createPostTitleNotFound() {
        PostInputPTO invalidPostInput = PostInputPTO.builder().body("body").userId(1L).build();

        Mockito.doNothing().when(postRepository).persist(ArgumentMatchers.any(Post.class));

        Response response = postResource.createPost(invalidPostInput);

        assertNotNull(response);

        ErrorResponse entity = (ErrorResponse) response.getEntity();

        assertNotNull(entity);
        assertEquals(entity.getMessage(), "Post title, body and user ID must not be null");
    }

    @Test
    void updatePost() {
        PostInputPTO updatedPost = PostInputPTO.builder().title("newtitle").build();

        Mockito.when(postRepository.findByIdOptional(any())).thenReturn(Optional.of(post));
        Mockito.when(userRepository.findByIdOptional(any())).thenReturn(Optional.of(user));


        Response response = postResource.updatePost(post.id, updatedPost);

        assertNotNull(response);
        assertEquals(201, response.getStatus());

        PostDTO entity = (PostDTO) response.getEntity();

        assertNotNull(entity);
        assertEquals(entity.getTitle(), updatedPost.getTitle());
        assertEquals(entity.getBody(), post.getBody());
        assertEquals(entity.getUserId(), post.getUser().id);

    }

    @Test
    void updatePostNotFound() {
        PostInputPTO updatedPost = PostInputPTO.builder().title("newtitle").build();

        Mockito.when(postRepository.findByIdOptional(any())).thenReturn(Optional.empty());

        Response response = postResource.updatePost(post.id, updatedPost);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());

        ErrorResponse entity = (ErrorResponse) response.getEntity();

        assertNotNull(entity);
        assertEquals(entity.getMessage(), "Post not found for ID: " + post.id);

    }

    @Test
    void updatePostUserNotFound() {

        PostInputPTO postInputPTO = PostInputPTO.builder().userId(1L).build();

        Mockito.when(userRepository.findByIdOptional(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postResource.updatePost(post.id, postInputPTO));

    }

    @Test
    void deletePost() {
        Mockito.when(postRepository.deleteById(any())).thenReturn(true);

        Response response = postResource.deletePost(post.id);

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertNull(response.getEntity());
    }

    @Test
    void deletePostNotFound() {
        Mockito.when(postRepository.deleteById(any())).thenReturn(false);

        Response response = postResource.deletePost(post.id);

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        ErrorResponse entity = (ErrorResponse) response.getEntity();

        assertNotNull(entity);
        assertEquals(entity.getMessage(), "Post not found for ID: " + post.id);
    }

    @Test
    void getPostsByUserId() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        Mockito.when(postRepository.findAllByUserId(any())).thenReturn(posts);
        Response response = postResource.getPostsByUserId(user.id);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<PostDTO> entity = (List<PostDTO>) response.getEntity();
        assertNotNull(entity);
        assertEquals(entity.size(), 1);
        assertEquals(entity.get(0).getTitle(), post.getTitle());
        assertEquals(entity.get(0).getBody(), post.getBody());
        assertEquals(entity.get(0).getUserId(), user.id);

    }

}