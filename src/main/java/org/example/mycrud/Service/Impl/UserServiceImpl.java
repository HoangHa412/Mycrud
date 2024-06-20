package org.example.mycrud.Service.Impl;

import jakarta.security.auth.message.AuthException;
import org.example.mycrud.Dto.UserDto;
import org.example.mycrud.Entity.User;
import org.example.mycrud.Mapper.UserMapper;
import org.example.mycrud.Repository.UserRepository;
import org.example.mycrud.Service.JwtService;
import org.example.mycrud.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;


    @Override
    public List<UserDto> getListUser() {
        return userRepository.findAll().stream()
                .map(userMapper::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getByID(Long id) {
        return userRepository.findById(id).map(userMapper::convertToUserDto);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = userMapper.convertToUser(userDto);
        User saveUser = userRepository.save(user);
        return userMapper.convertToUserDto(saveUser);

    }


    @Override
    public List<UserDto> search(String keyword) {
        return userRepository.search(keyword).stream()
                .map(userMapper::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> editUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(existingUser ->{
                    existingUser.setName(userDto.getName());
                    existingUser.setEmail(userDto.getEmail());
                    existingUser.setPhone(userDto.getPhone());
                    existingUser.setRole(userDto.getRole());
                    return userMapper.convertToUserDto(userRepository.save(existingUser));
                });
    }
}