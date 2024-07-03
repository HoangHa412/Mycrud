package org.example.mycrud.Exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(0, "SUCCESS"),
    NOT_FOUND(1, "Not found"),
    UNAUTHORIZED(1, "Token expired"),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    USER_NOT_FOUND(404, "User not found"),
    USER_ALREADY_EXISTS(409, "User already exists"),
    INVALID_CREDENTIALS(401, "Invalid credentials"),
    FORBIDDEN(403, "Forbidden"),
    SERVER_ERROR(500, "Internal Server Error");

    private Integer code;
    private String message;


}
