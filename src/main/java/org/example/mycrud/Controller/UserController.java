package org.example.mycrud.Controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import org.example.mycrud.Entity.User;
import org.example.mycrud.Exception.ErrorCode;
import org.example.mycrud.Mapper.UserMapper;
import org.example.mycrud.model.UserDto;
import org.example.mycrud.Service.UserService;
import org.example.mycrud.model.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@RestController
@RequestMapping("users")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> index(@Param("name") String name) {
        List<User> users = userService.getListUser();
//        if (name != null) {
//            users = userService.search(name);
//        }
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(userMapper.convertToUserDto(user));
        }
        return ResponseEntity.ok().body(BaseResponse.builder()
                .code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).content(usersDto));
    }

    @GetMapping("{id}")
    @ResponseBody
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
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserDto userdto) {
        userService.saveUser(userMapper.convertToUser(userdto));
        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.USER_ALREADY_EXISTS.getMessage()).content(userdto).build());
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (userService.getByID(id) != null) {
            userService.deleteById(id);
            return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null));
        } else {
            return ResponseEntity.ok(BaseResponse.builder().code(ErrorCode.USER_NOT_FOUND.getCode()).message(ErrorCode.USER_NOT_FOUND.getMessage()));
        }
    }

    @PutMapping(value = "/edit/{id}")
    @ResponseBody
    public ResponseEntity<?> editUser(@PathVariable Integer id, @RequestBody UserDto userdto) {
        BaseResponse response = new BaseResponse();
        User existingUser = userService.getByID(id);
        if (existingUser != null) {
            existingUser.setUsername(userdto.getUsername());
            existingUser.setEmail(userdto.getEmail());
            existingUser.setPhone(userdto.getPhone());
            userService.saveUser(existingUser);
            response.setCode(ErrorCode.SUCCESS.getCode());
            response.setMessage(ErrorCode.SUCCESS.getMessage());
            response.setContent(userMapper.convertToUserDto(existingUser));
        }
        return ResponseEntity.ok(response);
    }
}
