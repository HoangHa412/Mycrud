package org.example.mycrud.mapper;

import org.example.mycrud.entity.Role;
import org.example.mycrud.model.UserDto;
import org.example.mycrud.entity.User;
import org.example.mycrud.service.RoleService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    @Autowired
    private RoleService roleService;

    public UserDto convertToUserDto(@NotNull User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUserName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return userDto;
    }

    public User convertToUser(@NotNull UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUserName(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setPassword((userDto.getPassword()));
        Set<Role> roles = new HashSet<>();
        userDto.getRoles().forEach(roleName ->{
            Optional<Role> role = roleService.getRoleByName(roleName);
            role.ifPresent(roles::add);
        });
        user.setRoles(roles);
        return user;
    }


}
