package com.vi5hnu.codesprout.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends Exception{
    private final HttpStatus httpStatus;
    public ApiException(HttpStatus httpStatus, String msg){
        super(msg);
        this.httpStatus=httpStatus;
    }

    public ApiException(String msg,HttpStatus httpStatus){
        super(msg);
        this.httpStatus=httpStatus;
    }
}
