package org.example.mycrud.Service;

import org.example.mycrud.Dto.UserDto;
import org.example.mycrud.Entity.User;

import java.util.List;

public interface UserService {
    List<UserDto> getListUser();

    UserDto searchByID(Long id);

    void deleteById(Long id);

    void saveUser(User user);

    List<UserDto> search(String keyword);

}
