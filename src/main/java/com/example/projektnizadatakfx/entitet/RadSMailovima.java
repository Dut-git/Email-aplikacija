package com.example.projektnizadatakfx.entitet;

import java.util.Set;

public sealed interface RadSMailovima permits Racun{

    void posaljiMail(Email email);

    void proslijediMail(Email email, Set<String> primatelji);

    void dodajMail(Email email);

}
