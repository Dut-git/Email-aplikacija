package com.example.projektnizadatakfx.iznimke;

public class BazaPodatakaException extends Exception{

    public BazaPodatakaException() {
    }

    public BazaPodatakaException(String message) {
        super(message);
    }

    public BazaPodatakaException(String message, Throwable cause) {
        super(message, cause);
    }

    public BazaPodatakaException(Throwable cause) {
        super(cause);
    }

    public BazaPodatakaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
