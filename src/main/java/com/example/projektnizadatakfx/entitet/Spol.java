package com.example.projektnizadatakfx.entitet;

import com.example.projektnizadatakfx.iznimke.PogresanSpolException;

import java.io.Serializable;

public enum Spol implements Serializable {
    MALE("male", "M"),
    FEMALE("female", "F"),
    NON_BINARY("non-binary", "N");

    private final String spol;
    private final String simbol;

    Spol(String spol, String simbol) {
        this.spol = spol;
        this.simbol = simbol;
    }

    public String getSpol() {
        return spol;
    }

    public String getSimbol(){
        return simbol;
    }

    public static Spol vratiSpol(String input) throws PogresanSpolException {
        return switch (input){
            case "M", "male" -> Spol.MALE;
            case "F", "female" -> Spol.FEMALE;
            case "N", "non-binary" -> Spol.NON_BINARY;
            default -> throw new PogresanSpolException("Unesen je pogresan simbol za spol!");
        };
    }
}
