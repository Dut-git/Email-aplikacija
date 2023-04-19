package com.example.projektnizadatakfx;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class PromjeneController {

    @FXML
    public TextArea promjeneTA;

    public void initialize(){
        DohvatiPodatkeThread thread1 = new DohvatiPodatkeThread("dat\\email-promjene.dat", promjeneTA);
        DohvatiPodatkeThread thread2 = new DohvatiPodatkeThread("dat\\racun-promjene.dat", promjeneTA);
        DohvatiPodatkeThread thread3 = new DohvatiPodatkeThread("dat\\korisnik-promjene.dat", promjeneTA);
        DohvatiPodatkeThread thread4 = new DohvatiPodatkeThread("dat\\korisnici-lozinke-promjene.dat", promjeneTA);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

    public void prikaziLoginScreen() {
        Main.prikazi("login-screen.fxml");
    }
}
