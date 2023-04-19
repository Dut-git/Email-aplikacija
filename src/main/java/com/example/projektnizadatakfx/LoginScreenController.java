package com.example.projektnizadatakfx;

import com.example.projektnizadatakfx.entitet.Datoteke;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class LoginScreenController {

    @FXML
    private TextField korisnickoImeTF;
    @FXML
    private TextField lozinkaTF;
    public static String mailAdresa;

    public void login(){
        String korisnik = korisnickoImeTF.getText();
        String lozinka = lozinkaTF.getText();
        if(Datoteke.provjeriKorisnikaILozinku(korisnik, lozinka)){
            if (korisnik.equals("admin")){
                Main.prikazi("admin-racun.fxml");
            }
            else {
                mailAdresa = korisnik;
                if (mailAdresa.contains("@email.com")){
                    mailAdresa = mailAdresa.substring(0,mailAdresa.lastIndexOf("@"));
                }
                Main.prikazi("user-main-screen.fxml");
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Korisnik kojega ste unijeli ne postoji ili ste unijeli krive podatke!");
            alert.setTitle("Error");
            alert.setHeaderText("Pogresni podaci.");
            alert.show();
        }
    }

    public void kreirajNovogKorisnika(){
        Main.prikazi("user-novi-korisnik.fxml");
    }
    public void prikaziPromjene(){
        Main.prikazi("promjene.fxml");
    }
}