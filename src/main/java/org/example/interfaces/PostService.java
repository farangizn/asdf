package org.example.interfaces;

import org.example.dto.PostInputPTO;
import org.example.entity.Post;

import java.util.List;
import java.util.Optional;


public interface PostService {

    List<Post> findAll();

    Optional<Post> findById(Long id);

    Post save(Post post);

    void update(Post post, PostInputPTO postInputPTO);

    Boolean deleteById(Long id);

    List<Post> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    Post save(PostInputPTO post);
}
