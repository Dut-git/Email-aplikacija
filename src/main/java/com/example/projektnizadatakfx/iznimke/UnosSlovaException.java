package com.example.projektnizadatakfx.iznimke;

public class UnosSlovaException extends Exception{

    public UnosSlovaException() {
    }

    public UnosSlovaException(String message) {
        super(message);
    }

    public UnosSlovaException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnosSlovaException(Throwable cause) {
        super(cause);
    }

    public UnosSlovaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
