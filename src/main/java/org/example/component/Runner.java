package org.example.component;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.interfaces.PostService;
import org.example.interfaces.UserService;

@ApplicationScoped
public class Runner {

    @Inject
    UserService userService;

    @Inject
    PostService postService;

    @PostConstruct
    public void init() {
        try {
            System.out.println("Runner initialization started...");

            User user1 = User.builder()
                    .name("John Doe")
                    .username("johndoe")
                    .email("john.doe@example.com")
                    .build();

            User user2 = User.builder()
                    .name("Jane Smith")
                    .username("janesmith")
                    .email("jane.smith@example.com")
                    .build();

            userService.save(user1);
            userService.save(user2);

            Post post1 = Post.builder()
                    .title("Understanding Quarkus")
                    .body("A deep dive into Quarkus and its benefits for microservices.")
                    .user(user1)
                    .build();

            Post post2 = Post.builder()
                    .title("Spring Boot vs. Quarkus")
                    .body("Comparing Spring Boot and Quarkus for Java backend development.")
                    .user(user2)
                    .build();

            // Persist posts
            postService.save(post1);
            postService.save(post2);

            System.out.println("Runner initialization completed.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred during Runner initialization: " + e.getMessage());
        }
    }
}
