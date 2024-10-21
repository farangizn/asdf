package org.example.component;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.repository.PostRepository;
import org.example.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class MockDataLoader {

    @Inject
    UserRepository userRepository;

    @Inject
    PostRepository postRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (isDatabaseGenerationCreate()) {
            insertMockData();
        }
    }

    private boolean isDatabaseGenerationCreate() {
        return "create".equals(System.getProperty("quarkus.hibernate-orm.database.generation"));
    }

    private void insertMockData() {
        // Creating 5 Users
        User user1 = new User("Alice", "alice123", "alice@example.com");
        User user2 = new User("Bob", "bob456", "bob@example.com");
        User user3 = new User("Charlie", "charlie789", "charlie@example.com");
        User user4 = new User("David", "david101", "david@example.com");
        User user5 = new User("Eve", "eve202", "eve@example.com");

        List<User> users = Arrays.asList(user1, user2, user3, user4, user5);
        userRepository.persist(users);

        List<Post> posts = Arrays.asList(
            new Post("Post 1 by Alice", "Content for post 1", user1),
            new Post("Post 2 by Alice", "Content for post 2", user1),
            new Post("Post 1 by Bob", "Content for post 1", user2),
            new Post("Post 2 by Bob", "Content for post 2", user2),
            new Post("Post 1 by Charlie", "Content for post 1", user3),
            new Post("Post 2 by Charlie", "Content for post 2", user3),
            new Post("Post 1 by David", "Content for post 1", user4),
            new Post("Post 2 by David", "Content for post 2", user4),
            new Post("Post 1 by Eve", "Content for post 1", user5),
            new Post("Post 2 by Eve", "Content for post 2", user5)
        );

        postRepository.persist(posts);
    }
}
