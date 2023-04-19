package com.example.projektnizadatakfx;

import com.example.projektnizadatakfx.baza.BazaPodataka;
import com.example.projektnizadatakfx.entitet.Korisnik;
import com.example.projektnizadatakfx.entitet.Spol;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminKorisnikController {

    @FXML
    private TextField mailAdresaTF;
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
    private TextField lozinkaTF;
    @FXML
    private TableView<Korisnik> korisniciTV;
    @FXML
    private TableColumn<Korisnik, String> mailAdresaTC;
    @FXML
    private TableColumn<Korisnik, String> imeTC;
    @FXML
    private TableColumn<Korisnik, String> prezimeTC;
    @FXML
    private TableColumn<Korisnik, String> datumRodenjaTC;
    @FXML
    private TableColumn<Korisnik, String> spolTC;
    @FXML
    private TableColumn<Korisnik, String> brojMobitelaTC;
    @FXML
    private TableColumn<Korisnik, String> lozinkaTC;

    private static final Logger logger = LoggerFactory.getLogger(AdminKorisnikController.class);

    @FXML
    public void initialize(){
        List<Korisnik> korisnici;
        try {
            korisnici = BazaPodataka.dohvatiKorisnike().stream().toList();
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }
        spolCB.getItems().add(null);
        spolCB.getItems().add(Spol.MALE);
        spolCB.getItems().add(Spol.FEMALE);
        spolCB.getItems().add(Spol.NON_BINARY);

        spolTC.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSpol() != null) {
                return new SimpleStringProperty(cellData.getValue().getSpol().getSimbol());
            } return new SimpleStringProperty(null);});
        spolTC.setCellFactory(tc -> {
            TableCell<Korisnik, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(spolTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        imeTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIme()));
        imeTC.setCellFactory(tc -> {
            TableCell<Korisnik, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(imeTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        prezimeTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrezime()));
        prezimeTC.setCellFactory(tc -> {
            TableCell<Korisnik, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(prezimeTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        datumRodenjaTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDatumRodenjaString()));
        datumRodenjaTC.setCellFactory(tc -> {
            TableCell<Korisnik, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(datumRodenjaTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        mailAdresaTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMailAdresa()));
        mailAdresaTC.setCellFactory(tc -> {
            TableCell<Korisnik, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(mailAdresaTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        brojMobitelaTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBrojMobitela()));
        brojMobitelaTC.setCellFactory(tc -> {
            TableCell<Korisnik, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(brojMobitelaTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        lozinkaTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLozinka()));
        lozinkaTC.setCellFactory(tc -> {
            TableCell<Korisnik, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(lozinkaTC.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        korisniciTV.setItems(FXCollections.observableList(korisnici));
    }

    public void dohvatiKorisnike() {
        String ime = imeTF.getText();
        String prezime = prezimeTF.getText();
        LocalDate datumRodenja = null;
        if(datumRodenjaDP != null) {
            try {
                datumRodenja = datumRodenjaDP.getValue();
            } catch (DateTimeParseException ignored){}
        }
        Spol spol = spolCB.getSelectionModel().getSelectedItem();
        String lozinka = lozinkaTF.getText();
        String mailAdresa = mailAdresaTF.getText();
        Korisnik.KorisnikBuilder korisnikBuilder = new Korisnik.KorisnikBuilder(ime, prezime, datumRodenja, spol, lozinka, mailAdresa);
        String brojMobitela = brojMobitelaTF.getText();
        if (!brojMobitela.isBlank()){
            korisnikBuilder.saBrojemMobitela(brojMobitela);
        }
        Korisnik korisnik = korisnikBuilder.createKorisnik();

        List<Korisnik> korisnici = new ArrayList<>();
        try {
            korisnici = BazaPodataka.dohvatiKorisnikePremaKriterijima(korisnik);
        } catch (BazaPodatakaException e) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }

        korisniciTV.setItems(FXCollections.observableList(korisnici.stream().toList()));
    }

    public void obrisiKorisnika() {
        if (!korisniciTV.getSelectionModel().isEmpty()) {
            Korisnik korisnik = korisniciTV.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Da li ste sigurni da zelite obrisati odabrani podatak?");
            alert.setTitle("Potvrda");
            alert.setHeaderText("Potvrda brisanja podatka.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (Optional.ofNullable(korisnik).isPresent()) {
                    try {
                        BazaPodataka.obrisiKorisnika(korisnik);
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
                        podaci.add("admin - obrisan korisnik: MAIL ADRESA = " + korisnik.getMailAdresa() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
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
                        podaci.add("admin - obrisan racun: MAIL ADRESA = " + korisnik.getMailAdresa() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
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
