package org.example.mycrud.Service;

import org.example.mycrud.Entity.User;

import java.util.List;

public interface UserService {
    List<User> getListUser();

    User getByID(Integer id);

    void deleteById(Integer id);

    User saveUser(User user);

    List<User> search(String keyword);

    User getByEmail(String email);

    User getByUsername(String username);

    Boolean checkUserName(String username);

    Boolean oldPasswordValidity(String oldPassword, User user);

    Boolean newPasswordValidity(String newPassword, String confirmPassword);

    void changePassword(User user, String newPassword);
}
