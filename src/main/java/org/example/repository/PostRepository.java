package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Post;

import java.util.List;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {

    public List<Post> findAllByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public void deleteAllByUserId(Long userId) {
        delete("user.id", userId);
    }

}
