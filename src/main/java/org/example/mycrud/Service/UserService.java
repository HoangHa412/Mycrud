package org.example.mycrud.Service;

import org.example.mycrud.Entity.User;

import java.util.List;

public interface UserService {
    List<User> getListUser();

    User getByID(Long id);

    void deleteById(Long id);

    User saveUser(User user);

    List<User> search(String keyword);

    void forgetPassword(String email, String password);

    User getByEmail(String email);


    User getByUsername(String username);

    Boolean checkUserName(String username);

    Boolean oldPasswordValidity(String oldPassword, User user);

    Boolean newPasswordValidity(String newPassword, String confirmPassword);

    void changePassword(User user, String newPassword);
}
