package com.example.projektnizadatakfx.iznimke;

public class UnosNumerickihZnakovaException extends Exception{

    public UnosNumerickihZnakovaException() {
    }

    public UnosNumerickihZnakovaException(String message) {
        super(message);
    }

    public UnosNumerickihZnakovaException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnosNumerickihZnakovaException(Throwable cause) {
        super(cause);
    }

    public UnosNumerickihZnakovaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
