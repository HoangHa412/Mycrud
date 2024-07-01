package org.example.mycrud.Controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import org.example.mycrud.Exception.ErrorCode;
import org.example.mycrud.model.UserDto;
import org.example.mycrud.Service.UserService;
import org.example.mycrud.model.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@JsonInclude(JsonInclude.Include.NON_NULL)
@RestController
@RequestMapping("users")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @GetMapping(value = "")
    @ResponseBody
    public BaseResponse<List<UserDto>> index(@Param("name") String name) {
        List<UserDto> users = userService.getListUser();
        BaseResponse<List<UserDto>> userResponse = new BaseResponse<>(0, "Success", users);
//        if (name != null) {
//            users = userService.search(name);
//        }
        return userResponse;
    }

    @GetMapping("{id}")
    @ResponseBody
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<UserDto> user = userService.getByID(id);
        BaseResponse<UserDto> response = new BaseResponse<>();
        if (user.isPresent()) {
            response.setCode(0);
            response.setMessage("Success");
            response.setContent(user.get());
        } else {
            response.setCode(ErrorCode.USER_NOT_FOUND.getCode());
            response.setMessage(ErrorCode.USER_NOT_FOUND.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @ResponseBody
    public BaseResponse<UserDto> saveUser(@Valid @RequestBody UserDto userDto) {
        BaseResponse<UserDto> response = new BaseResponse<>(0, "User saved successfully", userService.saveUser(userDto));
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return response;
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (userService.getByID(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.ok(new BaseResponse<>(0, "User deleted successfully", null));
        } else {
            return ResponseEntity.ok(BaseResponse.builder().code(ErrorCode.USER_NOT_FOUND.getCode()).message(ErrorCode.USER_ALREADY_EXISTS.getMessage()));
        }
    }

    @PutMapping(value = "/edit/{id}")
    @ResponseBody
    public ResponseEntity<?> editUser(@Valid @PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.editUser(id, userDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
