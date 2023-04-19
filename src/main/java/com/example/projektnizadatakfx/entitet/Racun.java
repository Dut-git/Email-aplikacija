package com.example.projektnizadatakfx.entitet;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public non-sealed class Racun implements RadSMailovima, Serializable {

    private Korisnik korisnik;
    private List<Email> poslaniMailovi;
    private List<Email> primljeniMailovi;
    private List<Email> emailoviSaZvjezdicom;

    public Racun(Korisnik korisnik) {
        this.korisnik = korisnik;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }

    public List<Email> getPoslaniEmailovi() {
        return poslaniMailovi;
    }

    public String getPoslaniEmailoviString(){
        if (poslaniMailovi.isEmpty()){
            return "";
        }
        else if (poslaniMailovi.size() == 1){
            return poslaniMailovi.get(0).getId().toString();
        }
        return poslaniMailovi.stream().map(email -> String.valueOf(email.getId())).collect(Collectors.joining(" "));
    }

    public List<Email> getPrimljeniEmailovi() {
        return primljeniMailovi;
    }

    public String getPrimljeniEmailoviString(){
        if (primljeniMailovi.isEmpty()){
            return "";
        }
        else if (primljeniMailovi.size() == 1){
            return primljeniMailovi.get(0).getId().toString();
        }
        return primljeniMailovi.stream().map(email -> String.valueOf(email.getId())).collect(Collectors.joining(" "));
    }

    public List<Email> getEmailoviSZvjezdicom() {
        return emailoviSaZvjezdicom;
    }

    public String getEmailoviSZvjezdicomString(){
        if (emailoviSaZvjezdicom.isEmpty()){
            return "";
        }
        else if (emailoviSaZvjezdicom.size() == 1){
            return emailoviSaZvjezdicom.get(0).getId().toString();
        }
        return emailoviSaZvjezdicom.stream().map(email -> String.valueOf(email.getId())).collect(Collectors.joining(" "));
    }

    public void setPoslaniMailovi(List<Email> poslaniMailovi) {
        this.poslaniMailovi = poslaniMailovi;
    }

    public void setPrimljeniMailovi(List<Email> primljeniMailovi) {
        this.primljeniMailovi = primljeniMailovi;
    }

    public void setEmailoviSaZvjezdicom(List<Email> emailoviSaZvjezdicom) {
        this.emailoviSaZvjezdicom = emailoviSaZvjezdicom;
    }

    @Override
    public void posaljiMail(Email email) {
        poslaniMailovi.add(email);
        try {
            BazaPodataka.spremiMail(email);
        } catch (BazaPodatakaException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void proslijediMail(Email email, Set<String> primatelji) {
        try {
            BazaPodataka.dodajNovePrimateljeUMail(email, primatelji);
        } catch (BazaPodatakaException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dodajMail(Email email) {
        this.primljeniMailovi.add(email);
    }
}
