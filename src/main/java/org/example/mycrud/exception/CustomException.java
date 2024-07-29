package org.example.mycrud.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends Exception {

    private ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }
}
