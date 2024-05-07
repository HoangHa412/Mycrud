package org.example.mycrud.Service;

import org.example.mycrud.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getListUser();

    Optional<User> searchByID(Long id);

    void deleteById(Long id);

    void saveUser(User user);

}
