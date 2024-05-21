package org.example.mycrud.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    //@NotBlank(message = "Name is mandatory")
    //@Pattern(regexp = "^[a-zA-Z0-9]$", message = "Username must be alphanumerics")
    private String name;
    //@NotBlank(message = "Email is mandatory")
    //@Pattern(regexp = "^[a-zA-Z0-9._]+@[a-zA-Z0-9.]$")
    private String email;
    //@Pattern(regexp = "^[0-9]{10,10}$")
    private String phone;
    //@Pattern(regexp = "^[A-Z]$")
    private String role;

}
