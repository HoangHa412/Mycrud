package org.example.mycrud.Controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DisplayController {
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
}
