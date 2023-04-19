package com.example.projektnizadatakfx.entitet;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record VrijemeSlanja(LocalDateTime vrijemeSlanja) implements Serializable {

    @Override
    public String toString() {
        return vrijemeSlanja.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    }
}
