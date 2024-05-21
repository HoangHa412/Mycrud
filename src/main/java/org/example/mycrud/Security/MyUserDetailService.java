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
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUsersByName(username);
        if(user.isPresent()){
            var userobj = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userobj.getName())
                    .password(userobj.getPassword())
                    .roles(getRoles(userobj))
                    .build();
        }else {
            throw new UsernameNotFoundException("Khong tim thay ten nguoi dung" + username);
        }
    }

    private String[] getRoles(User user) {
        if(user == null){
            return new String[] {"USER"};
        }else{
            return user.getRole().split(",");
        }
    }
}
