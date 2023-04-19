package com.example.projektnizadatakfx;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.entitet.Email;
import com.example.projektnizadatakfx.entitet.Racun;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AdminRacunController {

    @FXML
    private TextField korisnikTF;
    @FXML
    private TextField poslaniEmailoviTF;
    @FXML
    private TextField primljeniEmailoviTF;
    @FXML
    private TextField emailoviSZvjezdicomTF;
    @FXML
    private TableView<Racun> racuniTV;
    @FXML
    private TableColumn<Racun, String> korisnikTC;
    @FXML
    private TableColumn<Racun, String> poslaniEmailoviTC;
    @FXML
    private TableColumn<Racun, String> primljeniEmailoviTC;
    @FXML
    private TableColumn<Racun, String> emailoviSZvjezdicomTC;

    private static final Logger logger = LoggerFactory.getLogger(AdminRacunController.class);

    @FXML
    public void initialize(){
        List<Racun> racuni;
        try {
            racuni = BazaPodataka.dohvatiRacune().stream().toList();
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }


        korisnikTC.setCellValueFactory(cellData -> {
            if (cellData.getValue().getKorisnik() != null) {
                return new SimpleStringProperty(cellData.getValue().getKorisnik().getMailAdresa());
        }
        return new SimpleStringProperty(null);});
        poslaniEmailoviTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPoslaniEmailoviString()));
        primljeniEmailoviTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrimljeniEmailoviString()));
        emailoviSZvjezdicomTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmailoviSZvjezdicomString()));
        racuniTV.setItems(FXCollections.observableList(racuni));
    }

    public void dohvatiRacune(){
        String korisnik = korisnikTF.getText();
        String poslaniEmailovi = poslaniEmailoviTF.getText();
        String primljeniEmailovi = primljeniEmailoviTF.getText();
        String emailoviSZvjezdicom = emailoviSZvjezdicomTF.getText();

        Set<Racun> racuni = null;
        Racun racun = new Racun(null);
        try {
            racun.setPoslaniMailovi(BazaPodataka.dohvatiEmailovePoIdu(Email.stringToListOfLongs(poslaniEmailovi)));
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }
        try {
            racun.setPrimljeniMailovi(BazaPodataka.dohvatiEmailovePoIdu(Email.stringToListOfLongs(primljeniEmailovi)));
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }
        try {
            racun.setEmailoviSaZvjezdicom(BazaPodataka.dohvatiEmailovePoIdu(Email.stringToListOfLongs(emailoviSZvjezdicom)));
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }
        try {
            racuni = BazaPodataka.dohvatiRacunePremaKriterijima(korisnik, racun);
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }

        racuniTV.setItems(FXCollections.observableList(racuni.stream().toList()));
    }

    public void obrisiRacun(){
        if (!racuniTV.getSelectionModel().isEmpty()) {
            Racun racun = racuniTV.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Da li ste sigurni da zelite obrisati odabrani podatak?");
            alert.setTitle("Potvrda");
            alert.setHeaderText("Potvrda brisanja podatka.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (Optional.ofNullable(racun).isPresent()) {
                    try {
                        BazaPodataka.obrisiRacun(racun);
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "Podatak je uspjesno obrisan!");
                        alert2.setTitle("");
                        alert2.setHeaderText("Informacija o brisanju podatka.");
                        alert2.show();

                        List<String> podaci = new ArrayList<>();
                        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\korisnik-promjene.dat"));) {
                            podaci = (ArrayList<String>) in.readObject();
                        } catch (ClassNotFoundException e) {
                            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                            logger.error(poruka, e);
                            throw new RuntimeException(e);
                        } catch (EOFException ignored){}
                        podaci.add("admin - obrisan korisnik: MAIL ADRESA = " + racun.getKorisnik().getMailAdresa() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
                        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat\\korisnik-promjene.dat"));) {
                            out.writeObject(podaci);
                        } catch (IOException e) {
                            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                            logger.error(poruka, e);
                            throw new RuntimeException(e);
                        }
                        podaci = new ArrayList<>();

                        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\racun-promjene.dat"));) {
                            podaci = (ArrayList<String>) in.readObject();
                        } catch (ClassNotFoundException e) {
                            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                            logger.error(poruka, e);
                            throw new RuntimeException(e);
                        } catch (EOFException ignored){}
                        podaci.add("admin - obrisan racun: MAIL ADRESA = " + racun.getKorisnik().getMailAdresa() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
                        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat\\racun-promjene.dat"));) {
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
            } else if (result.get() == ButtonType.CANCEL) {
                Alert alert3 = new Alert(Alert.AlertType.INFORMATION, "Podatak nije obrisan.");
                alert3.setTitle("");
                alert3.setHeaderText("Informacija o brisanju podatka.");
                alert3.show();
            }
        }
    }

    public void prikaziRacun(){
        Main.prikazi("admin-racun.fxml");
    }
    public void prikaziKorisnik(){
        Main.prikazi("admin-korisnik.fxml");
    }
    public void prikaziEmail(){
        Main.prikazi("admin-email.fxml");
    }
    public void prikaziLoginScreen(){
        Main.prikazi("login-screen.fxml");
    }
}
