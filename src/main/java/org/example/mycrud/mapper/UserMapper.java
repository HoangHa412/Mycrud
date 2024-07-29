package org.example.mycrud.mapper;

import org.example.mycrud.model.UserDto;
import org.example.mycrud.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto convertToUserDto(@NotNull User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUserName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        return userDto;
    }

    public User convertToUser(@NotNull UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUserName(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setPassword((userDto.getPassword()));
        return user;
    }


}
