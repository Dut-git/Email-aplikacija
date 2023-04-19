package com.example.projektnizadatakfx.entitet;

import java.time.LocalDate;

public class Korisnik extends Osoba{

    private String brojMobitela;
    private String lozinka;
    private String mailAdresa;

    private Korisnik(KorisnikBuilder builder) {
        super(builder.ime, builder.prezime, builder.datumRodenja, builder.spol);
        this.brojMobitela = builder.brojMobitela;
        this.lozinka = builder.lozinka;
        this.mailAdresa = builder.mailAdresa;
    }

    public static class KorisnikBuilder {
        public Spol spol;
        private final String ime;
        private final String prezime;
        private final LocalDate datumRodenja;
        private String brojMobitela;
        private final String lozinka;
        private final String mailAdresa;

        public KorisnikBuilder(String ime, String prezime, LocalDate datumRodenja, Spol spol, String lozinka, String mailAdresa) {
            this.ime = ime;
            this.prezime = prezime;
            this.datumRodenja = datumRodenja;
            this.spol = spol;
            this.lozinka = lozinka;
            this.mailAdresa = mailAdresa;
        }

        public KorisnikBuilder saBrojemMobitela(String brojMobitela) {
            this.brojMobitela = brojMobitela;
            return this;
        }

        public Korisnik createKorisnik() {
            return new Korisnik(this);
        }
    }

    public String getBrojMobitela() {
        return brojMobitela;
    }

    public void setBrojMobitela(String brojMobitela) {
        this.brojMobitela = brojMobitela;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getMailAdresa() {
        return mailAdresa;
    }

    public void setMailAdresa(String mailAdresa) {
        this.mailAdresa = mailAdresa;
    }
}
