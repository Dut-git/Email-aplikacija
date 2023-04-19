package com.example.projektnizadatakfx.entitet;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

abstract public class Osoba implements Serializable {

    private String ime;
    private String prezime;
    private LocalDate datumRodenja;
    private Spol spol;

    public Osoba(String ime, String prezime, LocalDate datumRodenja, Spol spol) {
        this.ime = ime;
        this.prezime = prezime;
        this.datumRodenja = datumRodenja;
        this.spol = spol;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public LocalDate getDatumRodenja() {
        return datumRodenja;
    }

    public String getDatumRodenjaString(){
        return datumRodenja.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    public void setDatumRodenja(LocalDate datumRodenja) {
        this.datumRodenja = datumRodenja;
    }

    public Spol getSpol() {
        return spol;
    }

    public void setSpol(Spol spol) {
        this.spol = spol;
    }
}
