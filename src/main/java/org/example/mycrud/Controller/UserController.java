package org.example.mycrud.Controller;

import org.example.mycrud.Entity.User;
import org.example.mycrud.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "")
    public String index(Model model, @Param("name") String name){
        List<User> users = userService.getListUser();
        if (name !=null){
            users=userService.search(name);
            model.addAttribute("name", name);
        }
        model.addAttribute("users", users);
        return "index";
    }

    @RequestMapping(value = "add")
    public String addUser(Model model){
        model.addAttribute("user", new User());
        return "addUser";
    }

    @PostMapping(value = "save")
    public String saveUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping(value = "/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }

    @GetMapping(value = "/edit")
    public String editUser(@RequestParam("id") Long userId, Model model){
        Optional<User> userEdit = userService.searchByID(userId);
        userEdit.ifPresent(user -> model.addAttribute("user", user));
        return "editUser";
    }
}
