package org.example.mycrud.security;

import org.example.mycrud.Entity.User;
import org.example.mycrud.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository repository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findUsersByName(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userObj.getName())
                    .password(userObj.getPassword())
                    .roles(userObj.getRole().name())
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

//    private String[] getRoles(@NotNull User user) {
//        if (user.getRole() == null) {
//            return new String[]{"USER"};
//        }
//        return user.getRole().split(",");
//    }
}
