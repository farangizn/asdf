package org.example.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.example.dto.UserInputDTO;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

@QuarkusTest
class UserServiceImplTest {

    @Inject
    UserServiceImpl userService;

    @InjectMock
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user", "user", "user@example.com");
    }

    @Test
    void save() {

        Mockito.doNothing().when(userRepository).persist(user);

        userService.save(user);

        verify(userRepository, times(1)).persist(user);
    }

    @Test
    void findAll() {
        List<User> users = List.of(new User("user", "user", "user@example.com"));
        Mockito.when(userRepository.listAll()).thenReturn(users);

        List<User> foundUsers = userService.findAll();

        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals("user", foundUsers.get(0).getName());
        verify(userRepository, times(1)).listAll();
    }

    @Test
    void findById() {
        Mockito.when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(user));

        Optional<User> userOptional = userService.findById(1L);

        assertTrue(userOptional.isPresent(), "User should be found");
        assertEquals(user.getName(), userOptional.get().getName());
        verify(userRepository, times(1)).findByIdOptional(1L);
    }

    @Test
    void findById_UserNotFound() {
        Mockito.when(userRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

        Optional<User> userOptional = userService.findById(1L);

        assertFalse(userOptional.isPresent(), "User should not be found");
        verify(userRepository, times(1)).findByIdOptional(1L);
    }

    @Test
    void update() {
        UserInputDTO userInputDTO = UserInputDTO.builder()
                .email("newuser@example.com")
                .username("newuser")
                .name("newname")
                .build();

        Mockito.when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(user));

        userService.update(user, userInputDTO);

        assertEquals("newname", user.getName());
        assertEquals("newuser", user.getUsername());
        assertEquals("newuser@example.com", user.getEmail());
        verify(userRepository, times(1)).persist(user);
    }

    @Test
    void deleteById() {
        Mockito.when(userRepository.deleteById(1L)).thenReturn(true);

        Boolean deleted = userService.deleteById(1L);

        assertTrue(deleted, "The user should be successfully deleted");
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_UserNotFound() {
        Mockito.when(userRepository.deleteById(1L)).thenReturn(false);

        Boolean deleted = userService.deleteById(1L);

        assertFalse(deleted, "The user should not be deleted since they do not exist");
        verify(userRepository, times(1)).deleteById(1L);
    }

}
