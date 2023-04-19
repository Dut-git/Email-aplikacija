package com.example.projektnizadatakfx;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.entitet.Korisnik;
import com.example.projektnizadatakfx.entitet.Racun;
import com.example.projektnizadatakfx.entitet.Spol;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserNoviKorisnikController {

    @FXML
    private TextField imeTF;
    @FXML
    private TextField prezimeTF;
    @FXML
    private DatePicker datumRodenjaDP;
    @FXML
    private ChoiceBox<Spol> spolCB;
    @FXML
    private TextField brojMobitelaTF;
    @FXML
    private TextField mailAdresaTF;
    @FXML
    private TextField lozinkaTF;

    private static final Logger logger = LoggerFactory.getLogger(UserNoviKorisnikController.class);

    public void initialize() {
        spolCB.getItems().add(Spol.MALE);
        spolCB.getItems().add(Spol.FEMALE);
        spolCB.getItems().add(Spol.NON_BINARY);
    }

    public void kreirajKorisnika() {
        List<String> prazniTextFieldovi = new ArrayList<>();

        if (imeTF.getText().isBlank()) {
            prazniTextFieldovi.add("Trebate unijeti svoje ime!");
        }
        if (prezimeTF.getText().isBlank()) {
            prazniTextFieldovi.add("Trebate unijeti svoje prezime!");
        }
        if (spolCB.getSelectionModel().isEmpty()) {
            prazniTextFieldovi.add("Trebate unijeti svoj spol!");
        }
        LocalDate datumRodenja = null;
        if (datumRodenjaDP.getValue() == null) {
            prazniTextFieldovi.add("Trebate unijeti svoj datum rođenja!");
        }
        else {
            try {
                datumRodenja = datumRodenjaDP.getValue();
                datumRodenja.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            } catch (Exception e) {
                prazniTextFieldovi.add("Krivo unesen datum rođenja");
            }
        }
        if (mailAdresaTF.getText().isBlank()) {
            prazniTextFieldovi.add("Trebate unijeti svoju mail adresu!");
        } else {
            if (mailAdresaTF.getText().contains("@email.com") || mailAdresaTF.getText().contains("@")){
                prazniTextFieldovi.add("Mail adresu trebate napisati bez '@email.com'!");
            }
            else {
                try {
                    Set<Korisnik> korisnici = BazaPodataka.dohvatiKorisnike();
                    for (Korisnik korisnik : korisnici) {
                        if (korisnik.getMailAdresa().equals(mailAdresaTF.getText())) {
                            prazniTextFieldovi.add("Mail adresa koju ste unijeli vec postoji!");
                        }
                    }
                } catch (BazaPodatakaException e) {
                    String poruka = "Došlo je do pogreške u radu s bazom podataka";
                    logger.error(poruka, e);
                    throw new RuntimeException(e);
                }
            }
        }
        if (lozinkaTF.getText().isBlank()) {
            prazniTextFieldovi.add("Trebate unijeti svoju lozinku!");
        } else {
            Pattern lozinkaPattern = Pattern.compile("^[a-zA-Z0-9]{3,}$");
            Matcher matcher;
            matcher = lozinkaPattern.matcher(lozinkaTF.getText());
            if (!matcher.matches()) {
                prazniTextFieldovi.add("Lozinka mora sadržavati minimalno 3 znaka i sastojati se od engleskih znakova i brojeva 1-9!");
            }
        }
        if (!brojMobitelaTF.getText().isEmpty()) {
            Pattern brojPattern = Pattern.compile("^[0-9]+$");
            Matcher matcher;
            matcher = brojPattern.matcher(brojMobitelaTF.getText());
            if (!matcher.matches()) {
                prazniTextFieldovi.add("Broj mobitela se mora sastojati samo od brojeva!");
            }
        }
        if (!prazniTextFieldovi.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, String.join("\n", prazniTextFieldovi));
            alert.setTitle("Error");
            alert.setHeaderText("Neispravno uneseni podaci!");
            alert.show();
        } else {
            Korisnik korisnik = new Korisnik.KorisnikBuilder(imeTF.getText(), prezimeTF.getText(), datumRodenja, spolCB.getSelectionModel().getSelectedItem(), lozinkaTF.getText(), mailAdresaTF.getText()).saBrojemMobitela(brojMobitelaTF.getText()).createKorisnik();
            Racun racun = new Racun(korisnik);
            try {
                BazaPodataka.spremiKorisnika(korisnik);
                BazaPodataka.spremiNoviRacun(racun);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Korisnik je uspješno kreiran!");
                alert.setTitle("");
                alert.setHeaderText("Informacija o kreiranju korisnika.");
                alert.show();

                List<String> podaci = new ArrayList<>();
                try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\korisnik-promjene.dat"));) {
                    podaci = (ArrayList<String>) in.readObject();
                } catch (ClassNotFoundException e) {
                    String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                    logger.error(poruka, e);
                    throw new RuntimeException(e);
                } catch (EOFException ignored){}
                podaci.add("Kreiran novi korisnik i racun: MAIL ADRESA = " + korisnik.getMailAdresa() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
                try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat\\korisnik-promjene.dat"));) {
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
            }
        }
    }

    public void prikaziLoginScreen() {
        Main.prikazi("login-screen.fxml");
    }
}
