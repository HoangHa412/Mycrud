package org.example.mycrud.Controller;

import jakarta.validation.Valid;
import org.example.mycrud.Dto.UserDto;
import org.example.mycrud.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> index(@Param("name") String name){
        List<UserDto> users = userService.getListUser();
        if (name !=null){
            users=userService.search(name);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        return userService.getByID(id)
                .map(ResponseEntity :: ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    @ResponseBody
    public UserDto saveUser(@Valid @RequestBody UserDto userDto){
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userService.saveUser(userDto);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(userService.getByID(id).isPresent()){
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/edit/{id}")
    @ResponseBody
    public ResponseEntity<?> editUser(@Valid @PathVariable Long id, @RequestBody UserDto userDto){
        return userService.editUser(id, userDto)
                .map(ResponseEntity :: ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
