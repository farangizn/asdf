package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.dto.PostInputPTO;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.interfaces.PostService;
import org.example.interfaces.UserService;
import org.example.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Inject
    public PostServiceImpl(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public List<Post> findAll() {
        return postRepository.listAll();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findByIdOptional(id);
    }

    @Override
    @Transactional
    public Post save(Post post) {
        postRepository.persist(post);
        return post;
    }

    @Override
    @Transactional
    public void update(Post post, PostInputPTO postInputPTO) {
        if (postInputPTO.getTitle() != null) post.setTitle(postInputPTO.getTitle());
        if (postInputPTO.getBody() != null) post.setBody(postInputPTO.getBody());
        if (postInputPTO.getUserId() != null) {
            Optional<User> userOptional = userService.findById(postInputPTO.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                post.setUser(user);
            } else {
                throw new EntityNotFoundException("User not found");
            }
        }
        postRepository.persist(post);
    }

    @Override
    @Transactional
    public Boolean deleteById(Long id) {
        return postRepository.deleteById(id);
    }

    @Override
    public List<Post> findAllByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteAllByUserId(Long userId) {
        postRepository.deleteAllByUserId(userId);
    }

    @Override
    public Post save(PostInputPTO postInputPTO) {
        Post post = Post.builder().body(postInputPTO.getBody()).title(postInputPTO.getTitle()).build();
        Optional<User> userOptional = userService.findById(postInputPTO.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            post.setUser(user);
        } else {
            throw new EntityNotFoundException("User with ID " + postInputPTO.getUserId() + " not found.");
        }
        postRepository.persist(post);
        return post;
    }
}
