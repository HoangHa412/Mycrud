package org.example.mycrud.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    SERVER_ERROR(9999, "Internal Server Error"),
    SUCCESS(0, "SUCCESS"),
    NOT_FOUND(1, "Not found"),
    UNAUTHORIZED(2, "Token expired"),
    UNCATEGORIZED_EXCEPTION(3, "Uncategorized error"),
    USER_NOT_FOUND(4, "User not found"),
    USER_ALREADY_EXISTS(5, "User already exists"),
    INVALID_CREDENTIALS(6, "Invalid credentials"),
    FORBIDDEN(7, "You do not have permission");

    private Integer code;
    private String message;


}
