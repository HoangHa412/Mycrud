package org.example.mycrud.Service;

import org.example.mycrud.Dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getListUser();

    Optional<UserDto> getByID(Long id);

    void deleteById(Long id);

    UserDto saveUser(UserDto userDto);

    List<UserDto> search(String keyword);

    Optional<UserDto> editUser(Long id, UserDto userDto);

}
