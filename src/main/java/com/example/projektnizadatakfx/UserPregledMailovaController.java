package com.example.projektnizadatakfx;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.entitet.Email;
import com.example.projektnizadatakfx.entitet.Racun;
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
import java.util.stream.Collectors;

public class UserPregledMailovaController {

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
    private ChoiceBox<String> tipEmailaCB;
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

    private static final Logger logger = LoggerFactory.getLogger(UserPregledMailovaController.class);

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

        tipEmailaCB.getItems().add("Svi emailovi");
        tipEmailaCB.getItems().add("Poslani");
        tipEmailaCB.getItems().add("Primljeni");
        tipEmailaCB.getItems().add("S zvjezdicom");

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

        Racun racun = null;
        if (!tipEmailaCB.getSelectionModel().isEmpty()) {
            try {
                racun = BazaPodataka.dohvatiRacunPremaKorisniku(LoginScreenController.mailAdresa);
            } catch (BazaPodatakaException e) {
                String poruka = "Došlo je do pogreške u radu s bazom podataka";
                logger.error(poruka, e);
                throw new RuntimeException(e);
            }
        }
        if (tipEmailaCB.getSelectionModel().getSelectedItem() != null) {
            if (tipEmailaCB.getSelectionModel().getSelectedItem().equals("Poslani")) {
                Racun finalRacun = racun;
                emailovi = emailovi.stream().filter(email -> finalRacun.getPoslaniEmailovi().stream().anyMatch(o -> o.getId() == email.getId())).collect(Collectors.toList());
            } else if (tipEmailaCB.getSelectionModel().getSelectedItem().equals("Primljeni")) {
                Racun finalRacun = racun;
                emailovi = emailovi.stream().filter(email -> finalRacun.getPrimljeniEmailovi().stream().anyMatch(o -> o.getId() == email.getId())).collect(Collectors.toList());
            } else if (tipEmailaCB.getSelectionModel().getSelectedItem().equals("S zvjezdicom")) {
                Racun finalRacun = racun;
                emailovi = emailovi.stream().filter(email -> finalRacun.getEmailoviSZvjezdicom().stream().anyMatch(o -> o.getId() == email.getId())).collect(Collectors.toList());
            }
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

                        List<String> korisnciLozinke = new ArrayList<>();
                        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\email-promjene.dat"));) {
                            korisnciLozinke = (List<String>) in.readObject();
                        } catch (ClassNotFoundException e) {
                            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
                            logger.error(poruka, e);
                            throw new RuntimeException(e);
                        } catch (EOFException ignored){}
                        korisnciLozinke.add(LoginScreenController.mailAdresa + " - obrisan email: ID =  " + email.getId() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
                        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat\\email-promjene.dat"));) {
                            out.writeObject(korisnciLozinke);
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

    public void prikaziUserMainScreen() {
        Main.prikazi("user-main-screen.fxml");
    }
}
