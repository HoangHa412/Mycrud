package org.example.mycrud.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;

    @NotBlank(message = "Name is mandatory")
    private String username;

    //@Email
    private String email;

    //@Pattern(regexp = "^[0-9]{10,10}$", message = "Phone number minimun 10 digits")
    private String phone;

    //@Size(min = 8, max = 20, message = "Password must be between 8")
    private String password;


}
