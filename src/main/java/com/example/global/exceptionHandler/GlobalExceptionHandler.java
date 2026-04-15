package com.example.global.exceptionHandler;

import com.example.global.exception.MemberNotFoundException;
import com.example.global.rsData.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<RsData<Void>> handleException(MemberNotFoundException e) {
        RsData<Void> res = new RsData<>(e.getStatus(),e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }
}
