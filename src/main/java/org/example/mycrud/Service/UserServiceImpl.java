package org.example.mycrud.Service;

import org.example.mycrud.Dto.UserDto;
import org.example.mycrud.Entity.User;
import org.example.mycrud.Mapper.UserMapper;
import org.example.mycrud.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
     private UserRepository userRepository;

    @Autowired
    private  UserMapper userMapper;


    @Override
    public List<UserDto> getListUser() {
        return userRepository.findAll().stream()
                .map(userMapper :: convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto searchByID(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(userMapper :: convertToUserDto).orElse(null);
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
    public List<UserDto> search(String keyword) {
        return userRepository.search(keyword).stream()
                .map(userMapper :: convertToUserDto)
                .collect(Collectors.toList());
    }
}
