package org.example.repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.example.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserRepositoryTest {

    @Inject
    UserRepository userRepository;

    @Test
    void findByUserId() {

        List<User> users = userRepository.findByUserId(1L);

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(users.get(0).id, 1L);
        assertEquals(users.get(0).getName(), "user");
        assertEquals(users.get(0).getUsername(), "user");
        assertEquals(users.get(0).getEmail(), "user@icloud.com");

    }
}