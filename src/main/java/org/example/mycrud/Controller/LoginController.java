package org.example.mycrud.Controller;


import jakarta.validation.Valid;
import org.example.mycrud.Entity.User;
import org.example.mycrud.Exception.CustomException;
import org.example.mycrud.Exception.ErrorCode;
import org.example.mycrud.Repository.UserRepository;
import org.example.mycrud.Security.UserDetailServiceImpl;
import org.example.mycrud.Security.UserDetailsImpl;
import org.example.mycrud.Service.JwtService;
import org.example.mycrud.model.BaseResponse;
import org.example.mycrud.model.LoginRequest;
import org.example.mycrud.model.LoginResponse;
import org.example.mycrud.model.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController()
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;


    @PostMapping(value = "/signup")
    public ResponseEntity<?> signUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsUserByUsername(signupRequest.getUsername())) {
            return ResponseEntity.ok().body(BaseResponse.builder().code(1).message("Error: Username already exists"));
        }

        // Validate password
//        String password = signupRequest.getPassword();
//        if (!isValidPassword(password)) {
//            throw new BadCredentialsException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter,");
//        }

        //Create new User account
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(signupRequest.getPassword());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(0).message("User successfully register").content(user).build());
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getUsername()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> permission = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());

        String accessToken = jwtService.generateToken(userDetails, permission);
        LoginResponse loginResponse = LoginResponse.builder().accessToken(accessToken).build();
        loginResponse.setCode(0);
        return ResponseEntity.ok().body(loginResponse);
    }

    private boolean isValidPassword(String password) {
        // Check character length
        if (password.length() < 8) {
            return false;
        }
        // Check character must least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        // Check character must least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        // Check character must least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        // Check character must least one special character
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return false;
        }
        return true;
    }
}
