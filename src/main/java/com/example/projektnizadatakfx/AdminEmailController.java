package com.example.projektnizadatakfx;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.entitet.Email;
import com.example.projektnizadatakfx.entitet.VrijemeSlanja;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.control.DateTimePicker;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminEmailController {

    @FXML
    private TextField idTF;
    @FXML
    private TextField primateljiTF;
    @FXML
    private TextField naslovTF;
    @FXML
    private TextField tijeloTF;
    @FXML
    private DateTimePicker datumIVrijemeDTP;
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

    private static final Logger logger = LoggerFactory.getLogger(AdminEmailController.class);

    @FXML
    public void initialize(){
        datumIVrijemeDTP.setDateTimeValue(null);
        List<Email> emailovi;
        try {
            emailovi = BazaPodataka.dohvatiEmailove().stream().toList();
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }


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
        emailoviTV.setItems(FXCollections.observableList(emailovi));
    }

    public void dohvatiEmailove(){
        Long id = null;
        try {
            id = Long.parseLong(idTF.getText());
        } catch (NumberFormatException ignored) {}
        String primatelji = primateljiTF.getText();
        VrijemeSlanja datumIVrijeme = null;
        try {
            datumIVrijeme = new VrijemeSlanja(datumIVrijemeDTP.getDateTimeValue());
            datumIVrijeme.vrijemeSlanja().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        } catch (Exception exception) {
            datumIVrijeme = null;
        }
        String naslov = naslovTF.getText();
        String tijelo = tijeloTF.getText();

        List<Email> emailovi = new ArrayList<>();
        try {
            emailovi = BazaPodataka.dohvatiEmailovePoKriterijima(new Email(id, Email.stringToSetOfPrimatelji(primatelji), naslov, tijelo, datumIVrijeme));
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }

        emailoviTV.setItems(FXCollections.observableList(emailovi.stream().toList()));
    }

    public void obrisiEmail(){
        if (!emailoviTV.getSelectionModel().isEmpty()) {
            Email email = emailoviTV.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Da li ste sigurni da zelite obrisati odabrani podatak?");
            alert.setTitle("Potvrda");
            alert.setHeaderText("Potvrda brisanja podatka.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (Optional.ofNullable(email).isPresent()) {
                    try {
                        BazaPodataka.obrisiEmail(email);
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "Podatak je uspjesno obrisan!");
                        alert2.setTitle("");
                        alert2.setHeaderText("Informacija o brisanju podatka.");
                        alert2.show();

                        List<String> podaci = new ArrayList<>();
                        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\email-promjene.dat"));) {
                            podaci = (ArrayList<String>) in.readObject();
                        } catch (ClassNotFoundException e) {
                            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                            logger.error(poruka, e);
                            throw new RuntimeException(e);
                        } catch (EOFException ignored){}
                        podaci.add("admin - obrisan email: ID = " + email.getId() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

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
