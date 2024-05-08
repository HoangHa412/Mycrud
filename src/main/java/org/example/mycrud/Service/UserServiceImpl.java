package org.example.mycrud.Service;

import org.example.mycrud.Entity.User;
import org.example.mycrud.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
     private UserRepository userRepository;

    @Override
    public List<User> getListUser() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public Optional<User> searchByID(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> searchUser(String name) {
        return userRepository.searhUser(name);
    }


}
