package com.example.global.rsData;

public record RsData<T>(
        String status,
        String message,
        T Data
) {
    public RsData (String status, String message){
        this(status,message,null);
    }
}
