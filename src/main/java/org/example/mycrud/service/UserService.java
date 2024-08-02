package org.example.mycrud.service;

import org.example.mycrud.entity.User;
import org.example.mycrud.model.request.SignupRequest;

import java.util.List;
import java.util.Set;

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

    void disConnect(User user);

    void addUser(User user);

    List<User> findAllUsersConnect();
}
