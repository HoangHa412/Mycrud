package org.example.mycrud.Security;


import org.example.mycrud.Entity.User;
import org.example.mycrud.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUsersByName(username);
        if (user.isPresent()){
            var userObj = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userObj.getName())
                    .password(userObj.getPassword())
                    .roles("USER")
                    .build();
        }else {
            throw new UsernameNotFoundException("Khong tim thay ten nguoi dung"+username);
        }
    }
    public UserDetailsService userDetailsService(){
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("admin")
                .password("$2a$12$Gd1y0W1RUA85NS4ZqS9m/umD6oXHDRZv7ZZLJkFv80Ak6vCqSbbK2")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }
}
