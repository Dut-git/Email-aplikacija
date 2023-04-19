package com.example.projektnizadatakfx;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.entitet.Email;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;
import com.example.projektnizadatakfx.iznimke.KrivaMailAdresaException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserNoviEmailController {

    @FXML
    private TextField primateljiTF;
    @FXML
    private TextField naslovTF;
    @FXML
    private TextArea tijeloTA;

    private static final Logger logger = LoggerFactory.getLogger(UserNoviEmailController.class);

    public void posaljiEmail(){
        if (primateljiTF.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Email treba imati barem jednom primatelja!");
            alert.setTitle("");
            alert.setHeaderText("Greška pri kreiranju emaila.");
            alert.show();
        }
        else {
            Set<String> primatelji = Email.stringToSetOfPrimatelji(primateljiTF.getText());
            Email email = new Email.EmailBuilder(primatelji).saNaslovom(naslovTF.getText()).saTijelom(tijeloTA.getText()).build();
            try {
                BazaPodataka.spremiIPosaljiMail(email, BazaPodataka.dohvatiKorisnikaPremaMailAdresi(LoginScreenController.mailAdresa));
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Email je uspjesno kreiran i poslan!");
                alert.setTitle("");
                alert.setHeaderText("Kreiranje emaila.");
                alert.show();

                List<String> podaci = new ArrayList<>();
                try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\email-promjene.dat"));) {
                    podaci = (ArrayList<String>) in.readObject();
                } catch (ClassNotFoundException e) {
                    String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                    logger.error(poruka, e);
                    throw new RuntimeException(e);
                } catch (EOFException ignored){}
                podaci.add(LoginScreenController.mailAdresa + " - kreiran email: ID = " + email.getId() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

                try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat\\email-promjene.dat"));) {
                    out.writeObject(podaci);
                } catch (IOException e) {
                    String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                    logger.error(poruka, e);
                    throw new RuntimeException(e);
                }
            } catch (BazaPodatakaException | IOException e) {
                String poruka = "Došlo je do pogreške u radu s bazom podataka";
                logger.error(poruka, e);
                throw new RuntimeException(e);
            } catch (KrivaMailAdresaException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Jedna od email adresa koju ste unijeli ne postoji u bazi!");
                alert.setTitle("");
                alert.setHeaderText("Email nije proslijeđen upisanim adresama.");
                alert.show();
                String poruka = "Došlo je do pogreške kod provjere email adrese";
                logger.error(poruka, e);
            }
        }
    }

    public void prikaziUserMainScreen(){
        Main.prikazi("user-main-screen.fxml");
    }
}
