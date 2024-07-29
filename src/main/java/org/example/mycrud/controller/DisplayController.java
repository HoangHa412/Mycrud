package org.example.mycrud.controller;


import org.example.mycrud.entity.User;
import org.example.mycrud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class DisplayController {

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/index/add")
    public String addUser(){
        return "addUser";
    }

    @GetMapping("/index/edit")
    public String editUser(){
        return "editUser";
    }

    @GetMapping("/login")
    public String handleLogin() {
        return "Custom_login";
    }

    @GetMapping("/index/chat")
    public String handleChat() {
        return "ChatIndex";
    }

    @MessageMapping("/user.addUser")
    @SendTo("/user/public")
    public User addUser(@Payload User user) {
        userService.addUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public User disconnectUser(@Payload User user) {
        userService.disConnect(user);
        return user;
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findAllUsersConnect());
    }
}
