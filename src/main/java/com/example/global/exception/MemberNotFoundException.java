package com.example.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException{
    String status;
    String message;

    public MemberNotFoundException(String status,String message) {
        super(message);
        this.message=message;
        this.status=status;

    }

}
