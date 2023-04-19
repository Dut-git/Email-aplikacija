package com.example.projektnizadatakfx.iznimke;

public class KrivaMailAdresaException extends RuntimeException{

    public KrivaMailAdresaException() {
    }

    public KrivaMailAdresaException(String message) {
        super(message);
    }

    public KrivaMailAdresaException(String message, Throwable cause) {
        super(message, cause);
    }

    public KrivaMailAdresaException(Throwable cause) {
        super(cause);
    }

    public KrivaMailAdresaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
