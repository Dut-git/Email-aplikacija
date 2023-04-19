package com.example.projektnizadatakfx.entitet;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public non-sealed class Email implements InformacijeOMailu, Serializable {

    private final Long id;
    private final Set<String> primatelji;
    private final String naslov;
    private final String tijelo;

    private final VrijemeSlanja vrijemeSlanja;

    private Email(EmailBuilder builder) {
        this.primatelji = builder.primatelji;
        this.naslov = builder.naslov;
        this.tijelo = builder.tijelo;
        vrijemeSlanja = new VrijemeSlanja(LocalDateTime.now());
        try {
            this.id = BazaPodataka.zadnjiEmailId() + 1;
        } catch (BazaPodatakaException e) {
            throw new RuntimeException(e);
        }
    }

    public Email(Long id, Set<String> primatelji, String naslov, String tijelo, VrijemeSlanja vrijemeSlanja){
        this.id = id;
        this.primatelji = primatelji;
        this.naslov = naslov;
        this.tijelo = tijelo;
        this.vrijemeSlanja = vrijemeSlanja;
    }

    @Override
    public void ispisiInformacijeOMailu() {
        System.out.println("Primatelji:");
        primatelji.forEach(primatelj -> System.out.println(primatelji));
        System.out.println("Naslov:\n  " + naslov);
        System.out.println("Tijelo:\n  " + tijelo);
    }

    public static class EmailBuilder {
        private final Set<String> primatelji;
        private String naslov;
        private String tijelo;

        public EmailBuilder(Set<String> primatelji) {
            this.primatelji = primatelji;
        }

        public EmailBuilder saNaslovom(String naslov) {
            this.naslov = naslov;
            return this;
        }

        public EmailBuilder saTijelom(String tijelo) {
            this.tijelo = tijelo;
            return this;
        }

        public Email build() {
            return new Email(this);
        }
    }

    public Set<String> getPrimatelji() {
        return primatelji;
    }

    public String getPrimateljiString(){
        if (primatelji.size() == 1){
            return primatelji.stream().toList().get(0);
        } else if (primatelji.size() > 1) {
            return primatelji.stream().map(String::valueOf).collect(Collectors.joining(" "));
        }
        return null;
    }

    public static Set<String> stringToSetOfPrimatelji(String stringPrimatelja){
        Set<String> primatelji = new HashSet<>();
        if (StringUtils.countMatches(stringPrimatelja, " ") > 0){
            return Arrays.stream(stringPrimatelja.split(" ")).collect(Collectors.toSet());
        } else if (stringPrimatelja.isBlank()) {
            return primatelji;
        }
        primatelji.add(stringPrimatelja);
        return primatelji;
    }

    public String getNaslov() {
        return naslov;
    }

    public String getTijelo() {
        return tijelo;
    }

    public VrijemeSlanja getVrijemeSlanja() {
        return vrijemeSlanja;
    }

    public Long getId() {
        return id;
    }

    public static List<Long> stringToListOfLongs(String ids){
        ArrayList<Long> list = new ArrayList<>();
        if (ids.isBlank() || ids.equals("")){
            return list;
        }
        else if (StringUtils.countMatches(ids, " ") > 0) {
            return Arrays.stream(ids.split(" ")).map(Long::valueOf).collect(Collectors.toList());
        }
        else {
            list.add(Long.parseLong(ids));
            return list;
        }
    }
}
