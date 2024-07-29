package org.example.mycrud.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mycrud.entity.User;
import org.example.mycrud.repository.UserRepository;
import org.example.mycrud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
     private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String redirect = null;
        if(authentication.getPrincipal() instanceof DefaultOAuth2User){
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String username = oAuth2User.getAttribute("email") != null?oAuth2User.getAttribute("email"):oAuth2User.getAttribute("login")+"@gmail.com";
            if(userRepository.findByEmail(username).isEmpty()){
                User user = new User();
                user.setEmail(username);
                user.setUserName(oAuth2User.getAttribute("email") != null?oAuth2User.getAttribute("email"):oAuth2User.getAttribute("login"));
                user.setPassword("@Sonhabn123");
//                user.setRole("USER");
                userRepository.save(user);
            }
        }
        redirect = "/index";
        new DefaultRedirectStrategy().sendRedirect(request, response, redirect);
    }
}
