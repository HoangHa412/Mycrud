package org.example.mycrud.Controller;


import jakarta.servlet.http.HttpServletRequest;
import org.example.mycrud.Entity.PasswordResetToken;
import org.example.mycrud.Entity.User;
import org.example.mycrud.Exception.ErrorCode;
import org.example.mycrud.Security.UserDetailsImpl;
import org.example.mycrud.Service.JwtService;
import org.example.mycrud.Service.MailService;
import org.example.mycrud.Service.PasswordResetTokenService;
import org.example.mycrud.Service.UserService;
import org.example.mycrud.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController()
public class AuthController {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Value("${forgetpassword.token.urlVerifyToken}")
    String verifyToken;

    @Value("${forgetpassword.token.expired}")
    private long EXPIRE_MINUTES;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginRequest loginRequest) throws UsernameNotFoundException {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> permission = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String accessToken = jwtService.generateToken(userDetails, permission);

        String refreshToken = jwtService.generateRefreshToken(userDetails);

        LoginResponse loginResponse = LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        loginResponse.setCode(0);
        return ResponseEntity.ok().body(loginResponse);
    }


    @PostMapping(value = "/signup")
    public ResponseEntity<?> signUser(@RequestBody SignupRequest signupRequest) {
        if (userService.checkUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(BaseResponse.builder().code(1).message("Error: Username already exists").build());
        }

        // Validate password
        String password = signupRequest.getPassword();
        if (isValidPassword(password)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(1).message("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter").build());
        }

        //Create new User account
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(signupRequest.getPassword());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userService.saveUser(user);
        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message("User successfully register").content(user).build());
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        Integer userIdFromRefreshToken = jwtService.getIdFromJwtToken(refreshTokenDTO.getRefreshToken());
        if (userIdFromRefreshToken == null) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.UNAUTHORIZED.getCode()).message(ErrorCode.UNAUTHORIZED.getMessage()).build());
        }

        User account = userService.getByID(userIdFromRefreshToken);
        if (account == null) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.UNAUTHORIZED.getCode()).message(ErrorCode.UNAUTHORIZED.getMessage()).build());
        }


        UserDetailsImpl userDetails = UserDetailsImpl.build(account);

        List<String> perrmissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String accessToken = jwtService.generateToken(userDetails, perrmissions);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        LoginResponse loginResponse = LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        loginResponse.setCode(0);

        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = userService.getByUsername(changePasswordRequest.getUsername());
        if (!userService.oldPasswordValidity(changePasswordRequest.getOldPassword(), user)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.INVALID_CREDENTIALS.getCode()).message(ErrorCode.INVALID_CREDENTIALS.getMessage()).build());
        }

        String password = changePasswordRequest.getNewPassword();
        if (isValidPassword(password)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(1).message("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter").build());
        }

        if (userService.newPasswordValidity(changePasswordRequest.getNewPassword(), changePasswordRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.INVALID_CREDENTIALS.getCode()).message("Please enter again your password!").build());
        }

        userService.changePassword(user, changePasswordRequest.getNewPassword());

        return ResponseEntity.ok().body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO, HttpServletRequest request) {
        User userEmail = userService.getByEmail(forgotPasswordDTO.getEmail());

        if (userEmail == null) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.NOT_FOUND.getCode()).message(ErrorCode.NOT_FOUND.getMessage()).build());
        }

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(userEmail);
        token.setToken(UUID.randomUUID().toString());
        token.setExprationtime(Date.from(Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(EXPIRE_MINUTES))));
        token = passwordResetTokenService.save(token);

        Map<String, Object> mailModel = new HashMap<>();
        mailModel.put("token", token);
        mailModel.put("user", userEmail);
        mailModel.put("signature", verifyToken);
        mailModel.put("reset URL", this.verifyToken + "/reset-Password?token=" + token.getToken());

        MailBody mailBody = MailBody.builder()
                .to(forgotPasswordDTO.getEmail())
                .subject("Password reset request")
                .text(mailModel)
                .build();

        mailService.sendEmail(mailBody);
        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordReset passwordReset, @RequestParam(name = "token") String theToken) {
        PasswordResetToken token = passwordResetTokenService.getByToken(theToken);
        User user = token.getUser();
        if (token.getExprationtime().before(Date.from(Instant.now()))) {
            passwordResetTokenService.deleteTokenById(token.getToken_id());
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.UNAUTHORIZED.getCode()).message("OTP has Expired").build());
        }

        if (userService.newPasswordValidity(passwordReset.getNewPassword(), passwordReset.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.INVALID_CREDENTIALS.getCode()).message("Please enter again your password!").build());
        }
        user.setPassword(passwordEncoder.encode(passwordReset.getNewPassword()));
        userService.saveUser(user);
        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).build());
    }


    private boolean isValidPassword(String password) {
        // Check character length
        if (password.length() < 8) {
            return true;
        }
        // Check character must least one digit
        if (!password.matches(".*\\d.*")) {
            return true;
        }
        // Check character must least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return true;
        }
        // Check character must least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return true;
        }
        // Check character must least one special character
        return !password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
