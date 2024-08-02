package org.example.mycrud.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.mycrud.entity.Role;
import org.example.mycrud.entity.User;
import org.example.mycrud.exception.ErrorCode;
import org.example.mycrud.mapper.UserMapper;
import org.example.mycrud.model.UserDto;
import org.example.mycrud.service.RoleService;
import org.example.mycrud.service.UserService;
import org.example.mycrud.model.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RestController
@RequestMapping("users")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> index(@Param("name") String name) {
        List<User> users = userService.getListUser();
//        if (name != null) {
//            users = userService.search(name);
//        }
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(userMapper.convertToUserDto(user));
        }
        return ok().body(BaseResponse.builder()
                .code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).content(usersDto).build());
    }
    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody UserDto userDto){
        User user = userMapper.convertToUser(userDto);
        userService.saveUser(user);
        return ResponseEntity.ok(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).content(userDto).build());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        User user = userService.getByID(id);
        BaseResponse<UserDto> response = new BaseResponse<>();
        if (user != null) {
            response.setCode(0);
            response.setMessage("Success");
            response.setContent(userMapper.convertToUserDto(user));
        } else {
            response.setCode(ErrorCode.USER_NOT_FOUND.getCode());
            response.setMessage(ErrorCode.USER_NOT_FOUND.getMessage());
        }
        return ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (userService.getByID(id) != null) {
            userService.deleteById(id);
            return ok(new BaseResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null));
        } else {
            return ok(BaseResponse.builder().code(ErrorCode.USER_NOT_FOUND.getCode()).message(ErrorCode.USER_NOT_FOUND.getMessage()).build());
        }
    }

    @PutMapping(value = "/edit/{id}")
    @ResponseBody
    public ResponseEntity<?> editUser(@PathVariable Integer id, @RequestBody UserDto userdto) {
        BaseResponse response = new BaseResponse();
        User existingUser = userService.getByID(id);
        if (existingUser != null) {
            existingUser.setUserName(userdto.getUsername());
            existingUser.setEmail(userdto.getEmail());
            existingUser.setPhone(userdto.getPhone());
            Set<Role> roles = new HashSet<>();
            userdto.getRoles().forEach(roleName ->{
                Optional<Role> role = roleService.getRoleByName(roleName);
                role.ifPresent(roles::add);
            });
            existingUser.setRoles(roles);
            userService.saveUser(existingUser);

            response.setCode(ErrorCode.SUCCESS.getCode());
            response.setMessage(ErrorCode.SUCCESS.getMessage());
            response.setContent(userMapper.convertToUserDto(existingUser));
        }
        return ok(response);
    }
}
