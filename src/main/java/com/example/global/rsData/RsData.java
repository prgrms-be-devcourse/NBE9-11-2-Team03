package com.example.global.rsData;

public record RsData<T>(
        String resultCode,
        String message,
        T data
) {
    public RsData (String status, String message){
        this(status,message,null);
    }
}
