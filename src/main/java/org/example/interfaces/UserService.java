package org.example.interfaces;

import org.example.dto.UserInputDTO;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void save(User user);

    List<User> findAll();

    Optional<User> findById(Long id);

    void update(User existingUserOpt, UserInputDTO updatedUser);

    Boolean deleteById(Long id);
}
