package org.example.mycrud.Controller;


import org.example.mycrud.Entity.ForgotPassword;
import org.example.mycrud.Entity.User;
import org.example.mycrud.Exception.ErrorCode;
import org.example.mycrud.Repository.ForgotPasswordRepository;
import org.example.mycrud.Repository.UserRepository;
import org.example.mycrud.Security.UserDetailsImpl;
import org.example.mycrud.Service.JwtService;
import org.example.mycrud.Service.MailService;
import org.example.mycrud.Service.UserService;
import org.example.mycrud.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private UserRepository userRepository;


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
        if (!userService.checkUserName(signupRequest.getUsername())) {
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

    @PostMapping("/verify/{email}")
    public ResponseEntity<?> verifyEmail(@PathVariable String email) {
        User userEmail = userService.getByEmail(email);

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request :" + otpGenerate())
                .subject("OTP for  Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otpGenerate())
                .expratetime(Date.from(Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(5))))
                .user(userEmail)
                .build();

        if (userEmail == null) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.NOT_FOUND.getCode()).message(ErrorCode.NOT_FOUND.getMessage()));
        }
        mailService.sendEmail(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message("Email sent for verification!").build());
    }

    @PostMapping("/verify/{email}/{otp}")
    public ResponseEntity<?> verifyOTP(@PathVariable String email, @PathVariable Integer otp) {
        User userEmail = userService.getByEmail(email);

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, userEmail)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email" + email));

        if (fp.getExpratetime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.UNAUTHORIZED.getCode()).message("OTP has Expired").build());
        }

        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).content("OTP verify").build());
    }

    @PostMapping("/reset-Password")
    public ResponseEntity<?> resetPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {

        User user = userService.getByEmail(forgotPasswordDTO.getEmail());

        if (isValidPassword(forgotPasswordDTO.getNewPassword())) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(1).message("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter").build());
        }

        if (userService.newPasswordValidity(forgotPasswordDTO.getNewPassword(), forgotPasswordDTO.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.builder().code(ErrorCode.INVALID_CREDENTIALS.getCode()).message("Please enter the password again!").build());
        }

        userService.changePassword(user, forgotPasswordDTO.getNewPassword());
        //userRepository.updatePassword(email, passwordEncoder.encode(forgotPasswordDTO.getNewPassword()));

        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message("Password has been changed!").build());
    }


    private Integer otpGenerate() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
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
