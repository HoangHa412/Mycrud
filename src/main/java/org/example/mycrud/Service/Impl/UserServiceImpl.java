package org.example.mycrud.Service.Impl;

import org.example.mycrud.Entity.User;
import org.example.mycrud.Repository.UserRepository;
import org.example.mycrud.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public List<User> getListUser() {
        return userRepository.findAll();
    }

    @Override
    public User getByID(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    @Override
    public List<User> search(String keyword) {
        return userRepository.search(keyword);
    }


    @Override
    public User getByEmail(String email) {
        return userRepository.findUsersByEmail(email.trim());
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findUsersByUsername(username).orElse(null);
    }

    @Override
    public Boolean checkUserName(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Override
    public Boolean oldPasswordValidity(String oldPassword, User user) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public Boolean newPasswordValidity(String newPassword, String confirmPassword) {
        return passwordEncoder.matches(newPassword, confirmPassword);
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void forgetPassword(String email, String password) {
        //find email
        User oCurrentMail = userRepository.findUsersByEmail(email.trim());

        if (oCurrentMail != null) {
            //set new password
            oCurrentMail.setPassword(passwordEncoder.encode(password));

            //save new password
            userRepository.save(oCurrentMail);
        } else {
            throw new RuntimeException("User not found");
        }

    }

}