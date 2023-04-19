package com.example.projektnizadatakfx.iznimke;

public class PogresnaLozinkaException extends Exception{

    public PogresnaLozinkaException() {
    }

    public PogresnaLozinkaException(String message) {
        super(message);
    }

    public PogresnaLozinkaException(String message, Throwable cause) {
        super(message, cause);
    }

    public PogresnaLozinkaException(Throwable cause) {
        super(cause);
    }

    public PogresnaLozinkaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
