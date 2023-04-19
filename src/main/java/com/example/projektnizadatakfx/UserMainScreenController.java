package com.example.projektnizadatakfx;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.entitet.Email;
import com.example.projektnizadatakfx.entitet.Racun;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;
import com.example.projektnizadatakfx.iznimke.KrivaMailAdresaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserMainScreenController {

    private Racun racun;
    @FXML
    private Label dobrodosliL;
    @FXML
    private TextField proslijediEmailTF;

    @FXML
    private TableView<Email> emailoviTV;
    @FXML
    private TableColumn<Email, String> idTC;
    @FXML
    private TableColumn<Email, String> primateljiTC;
    @FXML
    private TableColumn<Email, String> datumIVrijemeTC;
    @FXML
    private TableColumn<Email, String> naslovTC;
    @FXML
    private TableColumn<Email, String> tijeloTC;

    private static final Logger logger = LoggerFactory.getLogger(UserMainScreenController.class);

    public void initialize(){
        try {
            racun = BazaPodataka.dohvatiRacunPremaKorisniku(LoginScreenController.mailAdresa);
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }

        dobrodosliL.setText("Dobrodošli " + racun.getKorisnik().getIme() + " " + racun.getKorisnik().getPrezime() + "!");

        Set<Email> emailovi = new HashSet<>();
        emailovi.addAll(racun.getPoslaniEmailovi());
        emailovi.addAll(racun.getPrimljeniEmailovi());



        idTC.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        idTC.setCellFactory(tc -> {
            TableCell<Email, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(idTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        primateljiTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrimateljiString()));
        primateljiTC.setCellFactory(tc -> {
            TableCell<Email, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(primateljiTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        datumIVrijemeTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVrijemeSlanja().toString()));
        datumIVrijemeTC.setCellFactory(tc -> {
            TableCell<Email, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(datumIVrijemeTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        naslovTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaslov()));
        naslovTC.setCellFactory(tc -> {
            TableCell<Email, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(naslovTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        tijeloTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTijelo()));
        tijeloTC.setCellFactory(tc -> {
            TableCell<Email, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tijeloTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        emailoviTV.setItems(FXCollections.observableList(emailovi.stream().toList()));
    }

    public void proslijediMail(){
        if (!emailoviTV.getSelectionModel().isEmpty() && !proslijediEmailTF.getText().isBlank()){
            String primateljiString = proslijediEmailTF.getText();
            Set<String> primatelji = Email.stringToSetOfPrimatelji(primateljiString);
            Email email = emailoviTV.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Da li ste sigurni da želite proslijediti odabrani email?");
            alert.setTitle("Potvrda");
            alert.setHeaderText("Potvrda za proslijedbu emaila.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (Optional.ofNullable(email).isPresent() && !primatelji.isEmpty()) {
                    try {
                        BazaPodataka.dodajNovePrimateljeUMail(email, primatelji);
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "Email je uspješno proslijeđen!");
                        alert2.setTitle("");
                        alert2.setHeaderText("Informacija o proslijeđivanju emaila.");
                        alert2.show();

                        List<String> podaci = new ArrayList<>();
                        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\email-promjene.dat"));) {
                            podaci = (ArrayList<String>) in.readObject();
                        } catch (ClassNotFoundException e) {
                            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                            logger.error(poruka, e);
                            throw new RuntimeException(e);
                        } catch (IOException ignored){}
                        StringBuilder sb = new StringBuilder();
                        sb.append(LoginScreenController.mailAdresa).append(" je proslijedio email: ID = ").append(email.getId()).append(" NOVI PRIMATELJ/I: ");
                        primatelji.forEach(o -> sb.append(o).append(" "));
                        sb.append(LocalDateTime.now());
                        String res = sb.toString();
                        podaci.add(res);

                        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat\\email-promjene.dat"));) {
                            out.writeObject(podaci);
                        } catch (IOException e) {
                            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                            logger.error(poruka, e);
                            throw new RuntimeException(e);
                        }
                    } catch (BazaPodatakaException e) {
                        String poruka = "Došlo je do pogreške u radu s bazom podataka";
                        logger.error(poruka, e);
                        throw new RuntimeException(e);
                    } catch (KrivaMailAdresaException e){
                        Alert alert2 = new Alert(Alert.AlertType.ERROR, "Jedna od email adresa koju ste unijeli ne postoji u bazi!");
                        alert2.setTitle("");
                        alert2.setHeaderText("Email nije proslijeđen upisanim adresama");
                        alert2.show();
                        String poruka = "Došlo je do pogreške kod provjere email adrese";
                        logger.error(poruka, e);
                    }
                }
            } else if (result.get() == ButtonType.CANCEL) {
                Alert alert3 = new Alert(Alert.AlertType.INFORMATION, "Email nije proslijeđen.");
                alert3.setTitle("");
                alert3.setHeaderText("Informacija o proslijeđivanju emaila.");
                alert3.show();
            }
        }
    }

    public void dodajUEmailoveSZvjezdicom(){
        if (!emailoviTV.getSelectionModel().isEmpty()){
            Email email = emailoviTV.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Da li ste sigurni da želite dodati odabrani email u emailove s zvjezdicom?");
            alert.setTitle("Potvrda");
            alert.setHeaderText("Potvrda za dodavanje emaila.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (Optional.ofNullable(email).isPresent()) {
                    try {
                        BazaPodataka.dodajUEmailoveSZvjezdicom(email, LoginScreenController.mailAdresa);
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "Email je uspješno dodan u emailove s zvjezdicom!");
                        alert2.setTitle("");
                        alert2.setHeaderText("Informacija o dodavanju emaila.");
                        alert2.show();

                        List<String> podaci = new ArrayList<>();
                        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\email-promjene.dat"));) {
                            podaci = (ArrayList<String>) in.readObject();
                        } catch (ClassNotFoundException e) {
                            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                            logger.error(poruka, e);
                            throw new RuntimeException(e);
                        } catch (EOFException ignored){}
                        podaci.add(LoginScreenController.mailAdresa + " je dodao email u emailove s zvjezdicom: ID = " + email.getId() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
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
                    }
                }
            } else if (result.get() == ButtonType.CANCEL) {
                Alert alert3 = new Alert(Alert.AlertType.INFORMATION, "Email nije dodan u emailove s zvjezdicom.");
                alert3.setTitle("");
                alert3.setHeaderText("Informacija o dodavanju emaila.");
                alert3.show();
            }
        }
    }

    public void prikaziLoginScreen(){
        Main.prikazi("login-screen.fxml");
    }
    public void prikaziNoviEmail(){
        Main.prikazi("user-novi-email.fxml");
    }
    public void prikaziPregledEmailova(){
        Main.prikazi("user-pregled-emailova.fxml");
    }
}
