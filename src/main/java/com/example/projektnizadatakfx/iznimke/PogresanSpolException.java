package com.example.projektnizadatakfx.iznimke;

public class PogresanSpolException extends Exception {

    public PogresanSpolException() {
    }

    public PogresanSpolException(String message) {
        super(message);
    }

    public PogresanSpolException(String message, Throwable cause) {
        super(message, cause);
    }

    public PogresanSpolException(Throwable cause) {
        super(cause);
    }

    public PogresanSpolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
