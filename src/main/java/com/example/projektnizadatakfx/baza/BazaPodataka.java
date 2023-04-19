package com.example.projektnizadatakfx.baza;

import com.example.projektnizadatakfx.entitet.*;
import com.example.projektnizadatakfx.iznimke.BazaPodatakaException;
import com.example.projektnizadatakfx.iznimke.KrivaMailAdresaException;
import com.example.projektnizadatakfx.iznimke.PogresanSpolException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class BazaPodataka {

    private static final Logger logger = LoggerFactory.getLogger(BazaPodataka.class);

    public static Set<Korisnik> dohvatiKorisnike() throws BazaPodatakaException {
        Set<Korisnik> listaKorisnika = new HashSet<>();
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("SELECT * FROM KORISNIK");
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                String ime = resultSet.getString("ime");
                String prezime = resultSet.getString("prezime");
                LocalDate datumRodenja = LocalDate.parse(resultSet.getString("datum_rodenja"), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                Spol spol = Spol.vratiSpol(resultSet.getString("spol"));
                String brojMobitela = resultSet.getString("broj_mobitela");
                String lozinka = resultSet.getString("lozinka");
                String mailAdresa = resultSet.getString("mail_adresa");
                Korisnik noviKorisnik = new Korisnik.KorisnikBuilder(ime, prezime, datumRodenja, spol, lozinka, mailAdresa).saBrojemMobitela(brojMobitela).createKorisnik();
                listaKorisnika.add(noviKorisnik);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        } catch (PogresanSpolException e) {
            throw new RuntimeException(e);
        }
        return listaKorisnika;
    }

    public static List<Korisnik> dohvatiKorisnikePremaKriterijima(Korisnik korisnik) throws BazaPodatakaException {
        List<Korisnik> listaKorisnika = new ArrayList<>();
        try (Connection veza = spajanjeNaBazu()) {
            StringBuilder sqlUpit = new StringBuilder("SELECT * FROM KORISNIK WHERE 1 = 1");
            if (Optional.ofNullable(korisnik).isPresent()) {
                if (!Optional.ofNullable(korisnik.getIme()).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND IME LIKE '%" + korisnik.getIme() + "%'");
                }
                if (!Optional.ofNullable(korisnik.getPrezime()).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND PREZIME LIKE '%" + korisnik.getPrezime() + "%'");
                }
                if (Optional.ofNullable(korisnik).map(Korisnik::getDatumRodenja).isPresent()) {
                    sqlUpit.append(" AND DATUM_RODENJA LIKE '%" + korisnik.getDatumRodenja().toString() + "%'");
                }
                if (Optional.ofNullable(korisnik).map(Korisnik::getSpol).isPresent()) {
                    sqlUpit.append(" AND SPOL LIKE '%" + korisnik.getSpol().getSimbol() + "%'");
                }
                if (!Optional.ofNullable(korisnik.getBrojMobitela()).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND BROJ_MOBITELA LIKE '%" + korisnik.getBrojMobitela() + "%'");
                }
                if (!Optional.ofNullable(korisnik.getLozinka()).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND LOZINKA LIKE '%" + korisnik.getLozinka() + "%'");
                }
                if (!Optional.ofNullable(korisnik.getMailAdresa()).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND MAIL_ADRESA LIKE '%" + korisnik.getMailAdresa() + "%'");
                }
            }
            PreparedStatement upit = veza.prepareStatement(sqlUpit.toString());
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                String ime = resultSet.getString("ime");
                String prezime = resultSet.getString("prezime");
                LocalDate datumRodenja = LocalDate.parse(resultSet.getString("datum_rodenja"), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                Spol spol = Spol.vratiSpol(resultSet.getString("spol"));
                String brojMobitela = resultSet.getString("broj_mobitela");
                String lozinka = resultSet.getString("lozinka");
                String mailAdresa = resultSet.getString("mail_adresa");
                Korisnik noviKorisnik = new Korisnik.KorisnikBuilder(ime, prezime, datumRodenja, spol, lozinka, mailAdresa).saBrojemMobitela(brojMobitela).createKorisnik();
                listaKorisnika.add(noviKorisnik);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        } catch (PogresanSpolException e) {
            throw new RuntimeException(e);
        }
        return listaKorisnika;
    }

    public static Korisnik dohvatiKorisnikaPremaMailAdresi(String mailAdresa) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("SELECT * FROM KORISNIK WHERE MAIL_ADRESA = ?");
            upit.setString(1, mailAdresa);
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                String mail_adresa = resultSet.getString("mail_adresa");
                String ime = resultSet.getString("ime");
                String prezime = resultSet.getString("prezime");
                LocalDate datumRodenja = LocalDate.parse(resultSet.getString("datum_rodenja"), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                Spol spol = Spol.vratiSpol(resultSet.getString("spol"));
                String brojMobitela = resultSet.getString("broj_mobitela");
                String lozinka = resultSet.getString("lozinka");
                return new Korisnik.KorisnikBuilder(ime, prezime, datumRodenja, spol, lozinka, mail_adresa).saBrojemMobitela(brojMobitela).createKorisnik();
            }
            return null;
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        } catch (PogresanSpolException e) {
            throw new RuntimeException(e);
        }
    }

    public static void spremiKorisnika(Korisnik korisnik) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza.prepareStatement("INSERT INTO KORISNIK(ime, prezime, datum_rodenja, spol, broj_mobitela, lozinka, mail_adresa) VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, korisnik.getIme());
            preparedStatement.setString(2, korisnik.getPrezime());
            preparedStatement.setString(3, korisnik.getDatumRodenja().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
            preparedStatement.setString(4, korisnik.getSpol().getSimbol());
            preparedStatement.setString(5, korisnik.getBrojMobitela());
            preparedStatement.setString(6, korisnik.getLozinka());
            preparedStatement.setString(7, korisnik.getMailAdresa());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
        Datoteke.spremiNovogKorisnikaILozinku(korisnik);
    }

    public static void obrisiKorisnika(Korisnik korisnik) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("DELETE FROM KORISNIK WHERE MAIL_ADRESA = ?");
            upit.setString(1, korisnik.getMailAdresa());
            upit.executeUpdate();
            upit = veza.prepareStatement("DELETE FROM RACUN WHERE KORISNIK = ?");
            upit.setString(1, korisnik.getMailAdresa());
            upit.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static List<Email> dohvatiEmailove() throws BazaPodatakaException {
        List<Email> emailovi = new ArrayList<>();
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("SELECT * FROM EMAIL");
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                Long emailId = resultSet.getLong("id");
                Set<String> primatelji = new HashSet<>();
                if (StringUtils.countMatches(resultSet.getString("primatelji"), " ") > 0) {
                    primatelji = Arrays.stream(resultSet.getString("primatelji").split(" ")).collect(Collectors.toSet());
                } else if (resultSet.getString("primatelji").length() > 0) {
                    primatelji.add(resultSet.getString("primatelji"));
                }
                VrijemeSlanja vrijemeSlanja = new VrijemeSlanja(LocalDateTime.parse(resultSet.getString("vrijeme_slanja"), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
                String naslov = resultSet.getString("naslov");
                String tijelo = resultSet.getString("tijelo");
                emailovi.add(new Email(emailId, primatelji, naslov, tijelo, vrijemeSlanja));
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
        return emailovi;
    }

    public static List<Email> dohvatiEmailovePoKriterijima(Email email) throws BazaPodatakaException {
        List<Email> emailovi = new ArrayList<>();
        try (Connection veza = spajanjeNaBazu()) {
            StringBuilder sqlUpit = new StringBuilder("SELECT * FROM EMAIL WHERE 1 = 1");
            if (Optional.ofNullable(email).isPresent()) {
                if (Optional.ofNullable(email).map(Email::getId).isPresent()) {
                    sqlUpit.append(" AND ID LIKE '" + email.getId() + "'");
                }
                if (Optional.ofNullable(email).map(Email::getPrimatelji).isPresent()) {
                    if (email.getPrimatelji().size() == 1) {
                        sqlUpit.append(" AND PRIMATELJI LIKE '%" + email.getPrimatelji().stream().toList().get(0) + "%'");
                    }
                    else if (email.getPrimatelji().size() > 1) {
                        sqlUpit.append(" AND (PRIMATELJI LIKE '%" + email.getPrimatelji().stream().toList().get(0) + "%'");
                        for (int i = 1; i < email.getPrimatelji().size(); i++) {
                            sqlUpit.append(" OR PRIMATELJI LIKE '%" + email.getPrimatelji().stream().toList().get(i) + "%'");
                        }
                        sqlUpit.append(")");
                    }
                }
                if (Optional.ofNullable(email).map(Email::getVrijemeSlanja).isPresent()) {
                    sqlUpit.append(" AND VRIJEME_SLANJA LIKE '%" + email.getVrijemeSlanja().toString() + "%'");
                }
                if (!Optional.ofNullable(email.getNaslov()).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND NASLOV LIKE '%" + email.getNaslov() + "%'");
                }
                if (!Optional.ofNullable(email.getTijelo()).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND TIJELO LIKE '%" + email.getTijelo() + "%'");
                }
            }
            PreparedStatement upit = veza.prepareStatement(sqlUpit.toString());
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                Long emailId = resultSet.getLong("id");
                Set<String> primatelji = new HashSet<>();
                if (StringUtils.countMatches(resultSet.getString("primatelji"), " ") > 0) {
                    primatelji = Arrays.stream(resultSet.getString("primatelji").split(" ")).collect(Collectors.toSet());
                } else if (resultSet.getString("primatelji").length() > 0) {
                    primatelji.add(resultSet.getString("primatelji"));
                }
                VrijemeSlanja vrijemeSlanja = new VrijemeSlanja(LocalDateTime.parse(resultSet.getString("vrijeme_slanja"), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
                String naslov = resultSet.getString("naslov");
                String tijelo = resultSet.getString("tijelo");
                emailovi.add(new Email(emailId, primatelji, naslov, tijelo, vrijemeSlanja));
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
        return emailovi;
    }

    public static void spremiMail(Email email) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza.prepareStatement("INSERT INTO EMAIL(id, primatelji, naslov, tijelo, vrijeme_slanja) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setLong(1, email.getId());
            preparedStatement.setString(2, String.join(" ", email.getPrimatelji()));
            preparedStatement.setString(3, email.getNaslov());
            preparedStatement.setString(4, email.getTijelo());
            preparedStatement.setString(5, email.getVrijemeSlanja().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void posaljiMail(Email email, Korisnik posiljatelj) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            for (String korisnik : email.getPrimatelji()) {
                PreparedStatement preparedStatement = veza.prepareStatement("UPDATE RACUN SET primljeni_emailovi = primljeni_emailovi + ? WHERE korisnik = ?");
                preparedStatement.setString(1, " '%" + email.getId() + "%'");
                preparedStatement.setString(2, korisnik);
                preparedStatement.executeUpdate();
            }
            PreparedStatement preparedStatement = veza.prepareStatement("UPDATE RACUN SET poslani_emailovi = poslani_emailovi + ? WHERE korisnik = ?");
            preparedStatement.setString(1, " '%" + email.getId() + "%'");
            preparedStatement.setString(2, posiljatelj.getMailAdresa());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void spremiIPosaljiMail(Email email, Korisnik posiljatelj) throws BazaPodatakaException, KrivaMailAdresaException{
        try (Connection veza = spajanjeNaBazu()) {
            Set<Korisnik> korisnici = dohvatiKorisnike();
            for (String primatelj : email.getPrimatelji()){
                if (korisnici.stream().noneMatch(korisnik -> korisnik.getMailAdresa().equals(primatelj))){
                    throw new KrivaMailAdresaException("Unesena je nepostojeća mail adresa!");
                }
            }
            spremiMail(email);
            for (String korisnik : email.getPrimatelji()) {
                PreparedStatement preparedStatement = veza.prepareStatement("UPDATE RACUN SET primljeni_emailovi = (primljeni_emailovi || ?) WHERE korisnik = ?");
                PreparedStatement preparedStatement2 = veza.prepareStatement("SELECT * FROM RACUN WHERE korisnik = ?");
                preparedStatement2.setString(1, korisnik);
                preparedStatement2.executeQuery();
                ResultSet resultSet = preparedStatement2.getResultSet();
                List<Long> primljeniEmailovi = new ArrayList<>();
                while (resultSet.next()){
                    primljeniEmailovi = Email.stringToListOfLongs(resultSet.getString("primljeni_emailovi"));
                }
                if (primljeniEmailovi.isEmpty()){
                    preparedStatement.setString(1, String.valueOf(email.getId()));
                }
                else {
                    preparedStatement.setString(1, " " + email.getId());
                }
                preparedStatement.setString(2, korisnik);
                preparedStatement.executeUpdate();
            }
            PreparedStatement preparedStatement = veza.prepareStatement("UPDATE RACUN SET poslani_emailovi = (poslani_emailovi || ?) WHERE korisnik = ?");
            PreparedStatement preparedStatement2 = veza.prepareStatement("SELECT * FROM RACUN WHERE korisnik = ?");
            preparedStatement2.setString(1, posiljatelj.getMailAdresa());
            preparedStatement2.executeQuery();
            ResultSet resultSet = preparedStatement2.getResultSet();
            List<Long> poslaniEmailovi = new ArrayList<>();
            while (resultSet.next()){
                poslaniEmailovi = Email.stringToListOfLongs(resultSet.getString("poslani_emailovi"));
            }
            if (poslaniEmailovi.isEmpty()){
                preparedStatement.setString(1, String.valueOf(email.getId()));
            }
            else {
                preparedStatement.setString(1, " " + email.getId());
            }
            preparedStatement.setString(2, posiljatelj.getMailAdresa());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static List<Email> dohvatiEmailovePoIdu(List<Long> ids) throws BazaPodatakaException {
        List<Email> emailovi = new ArrayList<>();
        try (Connection veza = spajanjeNaBazu()) {
            if (!ids.isEmpty()) {
                PreparedStatement upit = veza.prepareStatement("SELECT * FROM EMAIL WHERE ID = ?");
                for (Long id : ids) {
                    upit.setLong(1, id);
                    upit.executeQuery();
                    ResultSet resultSet = upit.getResultSet();
                    while (resultSet.next()) {
                        Long emailId = resultSet.getLong("id");
                        Set<String> primatelji = new HashSet<>();
                        if (StringUtils.countMatches(resultSet.getString("primatelji"), " ") > 0) {
                            primatelji = Arrays.stream(resultSet.getString("primatelji").split(" ")).collect(Collectors.toSet());
                        } else if (resultSet.getString("primatelji").length() > 0) {
                            primatelji.add(resultSet.getString("primatelji"));
                        }
                        VrijemeSlanja vrijemeSlanja = new VrijemeSlanja(LocalDateTime.parse(resultSet.getString("vrijeme_slanja"), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
                        String naslov = resultSet.getString("naslov");
                        String tijelo = resultSet.getString("tijelo");
                        emailovi.add(new Email(emailId, primatelji, naslov, tijelo, vrijemeSlanja));
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
        return emailovi;
    }

    public static void dodajNovePrimateljeUMail(Email email, Set<String> noviPrimatelji) throws BazaPodatakaException, KrivaMailAdresaException{
        try (Connection veza = spajanjeNaBazu()) {
            Set<Korisnik> korisnici = dohvatiKorisnike();
            for (String primatelj : noviPrimatelji){
                if (korisnici.stream().noneMatch(korisnik -> korisnik.getMailAdresa().equals(primatelj))){
                    throw new KrivaMailAdresaException("Unesena je nepostojeća mail adresa!");
                }
            }
            PreparedStatement preparedStatement = veza.prepareStatement("SELECT * FROM EMAIL WHERE ID = ?");
            preparedStatement.setLong(1, email.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            String primatelji = null;
            while (resultSet.next()) {
                primatelji = resultSet.getString("primatelji");
            }
            for (String primatelj : noviPrimatelji) {
                assert primatelji != null;
                if (!primatelji.contains(primatelj)) {
                    if (primatelji.isBlank()) {
                        primatelji = primatelj;
                    } else {
                        primatelji += " " + primatelj;
                    }
                }
            }
            preparedStatement = veza.prepareStatement("UPDATE EMAIL SET PRIMATELJI = ? WHERE ID  = ?");
            preparedStatement.setString(1, primatelji);
            preparedStatement.setLong(2, email.getId());
            preparedStatement.executeUpdate();
            preparedStatement = veza.prepareStatement("UPDATE RACUN SET PRIMLJENI_EMAILOVI = ? WHERE KORISNIK = ?");
            for (String primatelj : noviPrimatelji) {
                PreparedStatement preparedStatement2 = veza.prepareStatement("SELECT * FROM RACUN WHERE KORISNIK = ?");
                preparedStatement2.setString(1, primatelj);
                preparedStatement2.executeQuery();
                ResultSet resultSet2 = preparedStatement2.getResultSet();
                String primljeniEmailovi = null;
                while (resultSet2.next()){
                    primljeniEmailovi = resultSet2.getString("primljeni_emailovi");
                    List<Long> setLongova = Email.stringToListOfLongs(primljeniEmailovi);
                    if (setLongova.stream().noneMatch(res -> res == email.getId())){
                        if (primljeniEmailovi.isBlank()) {
                            primljeniEmailovi = String.valueOf(email.getId());
                        } else {
                            primljeniEmailovi += " " + email.getId();
                        }
                    }
                }
                preparedStatement.setString(1, primljeniEmailovi);
                preparedStatement.setString(2, primatelj);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void dodajUEmailoveSZvjezdicom(Email email, String mailAdresaKorisnika) throws BazaPodatakaException{
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement preparedStatement = veza.prepareStatement("UPDATE RACUN SET emailovi_s_zvjezdicom = CONCAT(emailovi_s_zvjezdicom, ?) WHERE KORISNIK = ?");
            PreparedStatement preparedStatement2 = veza.prepareStatement("SELECT * FROM RACUN WHERE KORISNIK = ?");
            preparedStatement2.setString(1, mailAdresaKorisnika);
            preparedStatement2.executeQuery();
            ResultSet resultSet = preparedStatement2.getResultSet();
            List<Long> emailoviSZvjezdicomIds = new ArrayList<>();
            while (resultSet.next()){
                emailoviSZvjezdicomIds = Email.stringToListOfLongs(resultSet.getString("emailovi_s_zvjezdicom"));
            }
            if (emailoviSZvjezdicomIds.isEmpty()){
                preparedStatement.setString(1, String.valueOf(email.getId()));
            }
            else {
                preparedStatement.setString(1, " " + email.getId());
            }
            preparedStatement.setString(2, mailAdresaKorisnika);
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void obrisiEmail(Email email) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("DELETE FROM EMAIL WHERE ID = ?");
            upit.setLong(1, email.getId());
            upit.executeUpdate();
            upit = veza.prepareStatement("SELECT * FROM RACUN");
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()){
                String mailAdresa = resultSet.getString("korisnik");
                List<Long> poslaniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("poslani_emailovi").length() == 1) {
                    poslaniEmailoviIds.add(Long.parseLong(resultSet.getString("poslani_emailovi")));
                } else if (resultSet.getString("poslani_emailovi").length() > 1) {
                    poslaniEmailoviIds = Arrays.stream(resultSet.getString("poslani_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> primljeniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("primljeni_emailovi").length() == 1) {
                    primljeniEmailoviIds.add(Long.parseLong(resultSet.getString("primljeni_emailovi")));
                } else if (resultSet.getString("primljeni_emailovi").length() > 1) {
                    primljeniEmailoviIds = Arrays.stream(resultSet.getString("primljeni_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> emailoviSaZvjezdicomIds = new ArrayList<>();
                if (resultSet.getString("emailovi_s_zvjezdicom").length() == 1) {
                    emailoviSaZvjezdicomIds.add(Long.parseLong(resultSet.getString("emailovi_s_zvjezdicom")));
                } else if (resultSet.getString("emailovi_s_zvjezdicom").length() > 1) {
                    emailoviSaZvjezdicomIds = Arrays.stream(resultSet.getString("emailovi_s_zvjezdicom").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Email> poslaniEmailovi = dohvatiEmailovePoIdu(poslaniEmailoviIds);
                List<Email> primljeniEmailovi = dohvatiEmailovePoIdu(primljeniEmailoviIds);
                List<Email> emailoviSaZvjezdicom = dohvatiEmailovePoIdu(emailoviSaZvjezdicomIds);
                List<Korisnik> korisnici = dohvatiKorisnikePremaKriterijima(new Korisnik.KorisnikBuilder(null, null, null, null, null, mailAdresa).createKorisnik());
                Korisnik korisnik = korisnici.get(0);
                Racun racun = new Racun(korisnik);
                poslaniEmailovi.removeIf(obj -> obj.getId() == email.getId());
                primljeniEmailovi.removeIf(obj -> obj.getId() == email.getId());
                emailoviSaZvjezdicom.removeIf(obj -> obj.getId() == email.getId());
                racun.setPoslaniMailovi(poslaniEmailovi);
                racun.setPrimljeniMailovi(primljeniEmailovi);
                racun.setEmailoviSaZvjezdicom(emailoviSaZvjezdicom);
                obrisiRacun(racun.getKorisnik().getMailAdresa());
                spremiRacun(racun);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static Set<Racun> dohvatiRacune() throws BazaPodatakaException {
        Set<Racun> listaRacuna = new HashSet<>();
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("SELECT * FROM RACUN");
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                String mailAdresa = resultSet.getString("korisnik");
                List<Long> poslaniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("poslani_emailovi").length() == 1) {
                    poslaniEmailoviIds.add(Long.parseLong(resultSet.getString("poslani_emailovi")));
                } else if (resultSet.getString("poslani_emailovi").length() > 1) {
                    poslaniEmailoviIds = Arrays.stream(resultSet.getString("poslani_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> primljeniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("primljeni_emailovi").length() == 1) {
                    primljeniEmailoviIds.add(Long.parseLong(resultSet.getString("primljeni_emailovi")));
                } else if (resultSet.getString("primljeni_emailovi").length() > 1) {
                    primljeniEmailoviIds = Arrays.stream(resultSet.getString("primljeni_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> emailoviSaZvjezdicomIds = new ArrayList<>();
                if (resultSet.getString("emailovi_s_zvjezdicom").length() == 1) {
                    emailoviSaZvjezdicomIds.add(Long.parseLong(resultSet.getString("emailovi_s_zvjezdicom")));
                } else if (resultSet.getString("emailovi_s_zvjezdicom").length() > 1) {
                    emailoviSaZvjezdicomIds = Arrays.stream(resultSet.getString("emailovi_s_zvjezdicom").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Email> poslaniEmailovi = dohvatiEmailovePoIdu(poslaniEmailoviIds);
                List<Email> primljeniEmailovi = dohvatiEmailovePoIdu(primljeniEmailoviIds);
                List<Email> emailoviSaZvjezdicom = dohvatiEmailovePoIdu(emailoviSaZvjezdicomIds);
                List<Korisnik> korisnici = dohvatiKorisnikePremaKriterijima(new Korisnik.KorisnikBuilder(null, null, null, null, null, mailAdresa).createKorisnik());
                Korisnik korisnik = korisnici.get(0);
                Racun racun = new Racun(korisnik);
                racun.setPoslaniMailovi(poslaniEmailovi);
                racun.setPrimljeniMailovi(primljeniEmailovi);
                racun.setEmailoviSaZvjezdicom(emailoviSaZvjezdicom);
                listaRacuna.add(racun);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
        return listaRacuna;
    }

    public static Set<Racun> dohvatiRacunePremaKriterijima(Racun racun) throws BazaPodatakaException {
        Set<Racun> listaRacuna = new HashSet<>();
        try (Connection veza = spajanjeNaBazu()) {
            StringBuilder sqlUpit = new StringBuilder("SELECT * FROM RACUN WHERE 1 = 1");
            if (Optional.ofNullable(racun).isPresent()) {
                if (!Optional.ofNullable(racun.getKorisnik().getMailAdresa()).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND KORISNIK LIKE '%" + racun.getKorisnik().getMailAdresa() + "%'");
                }
                if (!Optional.ofNullable(racun.getPoslaniEmailoviString()).map(String::isBlank).orElse(true)) {
                    if (racun.getPoslaniEmailovi().size() == 1) {
                        sqlUpit.append(" AND POSLANI_EMAILOVI LIKE '%" + racun.getPoslaniEmailovi().get(0).getId() + "%'");
                    } else {
                        sqlUpit.append(" AND (POSLANI_EMAILOVI LIKE '%" + racun.getPoslaniEmailovi().get(0).getId() + "%'");
                        for (int i = 1; i < racun.getPoslaniEmailovi().size(); i++) {
                            sqlUpit.append(" OR POSLANI_EMAILOVI LIKE '%" + racun.getPoslaniEmailovi().get(i).getId() + "%'");
                        }
                        sqlUpit.append(")");
                    }
                }
                if (!Optional.ofNullable(racun.getPrimljeniEmailoviString()).map(String::isBlank).orElse(true)) {
                    if (racun.getPrimljeniEmailovi().size() == 1) {
                        sqlUpit.append(" AND PRIMLJENI_EMAILOVI LIKE '%" + racun.getPrimljeniEmailovi().get(0).getId() + "%'");
                    } else {
                        sqlUpit.append(" AND (PRIMLJENI_EMAILOVI LIKE '%" + racun.getPrimljeniEmailovi().get(0).getId() + "%'");
                        for (int i = 1; i < racun.getPrimljeniEmailovi().size(); i++) {
                            sqlUpit.append(" OR PRIMLJENI_EMAILOVI LIKE '%" + racun.getPrimljeniEmailovi().get(i).getId() + "%'");
                        }
                        sqlUpit.append(")");
                    }
                }
                if (!Optional.ofNullable(racun.getEmailoviSZvjezdicomString()).map(String::isBlank).orElse(true)) {
                    if (racun.getEmailoviSZvjezdicom().size() == 1) {
                        sqlUpit.append(" AND EMAILOVI_S_ZVJEZDICOM LIKE '%" + racun.getEmailoviSZvjezdicom().get(0).getId() + "%'");
                    } else {
                        sqlUpit.append(" AND (EMAILOVI_S_ZVJEZDICOM LIKE '%" + racun.getEmailoviSZvjezdicom().get(0).getId() + "%'");
                        for (int i = 1; i < racun.getEmailoviSZvjezdicom().size(); i++) {
                            sqlUpit.append(" OR EMAILOVI_S_ZVJEZDICOM LIKE '%" + racun.getEmailoviSZvjezdicom().get(i).getId() + "%'");
                        }
                        sqlUpit.append(")");
                    }
                }
            }
            PreparedStatement upit = veza.prepareStatement(sqlUpit.toString());
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                String mailAdresa = resultSet.getString("korisnik");
                List<Long> poslaniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("poslani_emailovi").length() == 1) {
                    poslaniEmailoviIds.add(Long.parseLong(resultSet.getString("poslani_emailovi")));
                } else if (resultSet.getString("poslani_emailovi").length() > 1) {
                    poslaniEmailoviIds = Arrays.stream(resultSet.getString("poslani_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> primljeniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("primljeni_emailovi").length() == 1) {
                    primljeniEmailoviIds.add(Long.parseLong(resultSet.getString("primljeni_emailovi")));
                } else if (resultSet.getString("primljeni_emailovi").length() > 1) {
                    primljeniEmailoviIds = Arrays.stream(resultSet.getString("primljeni_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> emailoviSaZvjezdicomIds = new ArrayList<>();
                if (resultSet.getString("emailovi_s_zvjezdicom").length() == 1) {
                    emailoviSaZvjezdicomIds.add(Long.parseLong(resultSet.getString("emailovi_s_zvjezdicom")));
                } else if (resultSet.getString("emailovi_s_zvjezdicom").length() > 1) {
                    emailoviSaZvjezdicomIds = Arrays.stream(resultSet.getString("emailovi_s_zvjezdicom").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Email> poslaniEmailovi = dohvatiEmailovePoIdu(poslaniEmailoviIds);
                List<Email> primljeniEmailovi = dohvatiEmailovePoIdu(primljeniEmailoviIds);
                List<Email> emailoviSaZvjezdicom = dohvatiEmailovePoIdu(emailoviSaZvjezdicomIds);
                List<Korisnik> korisnici = dohvatiKorisnikePremaKriterijima(new Korisnik.KorisnikBuilder(null, null, null, null, null, mailAdresa).createKorisnik());
                Korisnik korisnik = korisnici.get(0);
                Racun noviRacun = new Racun(korisnik);
                noviRacun.setPoslaniMailovi(poslaniEmailovi);
                noviRacun.setPrimljeniMailovi(primljeniEmailovi);
                noviRacun.setEmailoviSaZvjezdicom(emailoviSaZvjezdicom);
                listaRacuna.add(noviRacun);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
        return listaRacuna;
    }

    public static void obrisiRacun(Racun racun) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("DELETE FROM RACUN WHERE KORISNIK = ?");
            upit.setString(1, racun.getKorisnik().getMailAdresa());
            upit.executeUpdate();
            upit = veza.prepareStatement("DELETE FROM KORISNIK WHERE MAIL_ADRESA = ?");
            upit.setString(1, racun.getKorisnik().getMailAdresa());
            upit.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void obrisiRacun(String mailAdresa) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("DELETE FROM RACUN WHERE KORISNIK = ?");
            upit.setString(1, mailAdresa);
            upit.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static Set<Racun> dohvatiRacunePremaKriterijima(String mailAdresaKorisnika, Racun racun) throws BazaPodatakaException {
        Set<Racun> listaRacuna = new HashSet<>();
        try (Connection veza = spajanjeNaBazu()) {
            StringBuilder sqlUpit = new StringBuilder("SELECT * FROM RACUN WHERE 1 = 1");
            if (Optional.ofNullable(racun).isPresent()) {
                if (!Optional.ofNullable(mailAdresaKorisnika).map(String::isBlank).orElse(true)) {
                    sqlUpit.append(" AND KORISNIK LIKE '%" + mailAdresaKorisnika + "%'");
                }
                if (!Optional.ofNullable(racun.getPoslaniEmailoviString()).map(String::isBlank).orElse(true)) {
                    if (racun.getPoslaniEmailovi().size() == 1) {
                        sqlUpit.append(" AND POSLANI_EMAILOVI LIKE '%" + racun.getPoslaniEmailovi().get(0).getId() + "%'");
                    } else {
                        sqlUpit.append(" AND (POSLANI_EMAILOVI LIKE '%" + racun.getPoslaniEmailovi().get(0).getId() + "%'");
                        for (int i = 1; i < racun.getPoslaniEmailovi().size(); i++) {
                            sqlUpit.append(" OR POSLANI_EMAILOVI LIKE '%" + racun.getPoslaniEmailovi().get(i).getId() + "%'");
                        }
                        sqlUpit.append(")");
                    }
                }
                if (!Optional.ofNullable(racun.getPrimljeniEmailoviString()).map(String::isBlank).orElse(true)) {
                    if (racun.getPrimljeniEmailovi().size() == 1) {
                        sqlUpit.append(" AND PRIMLJENI_EMAILOVI LIKE '%" + racun.getPrimljeniEmailovi().get(0).getId() + "%'");
                    } else {
                        sqlUpit.append(" AND (PRIMLJENI_EMAILOVI LIKE '%" + racun.getPrimljeniEmailovi().get(0).getId() + "%'");
                        for (int i = 1; i < racun.getPrimljeniEmailovi().size(); i++) {
                            sqlUpit.append(" OR PRIMLJENI_EMAILOVI LIKE '%" + racun.getPrimljeniEmailovi().get(i).getId() + "%'");
                        }
                        sqlUpit.append(")");
                    }
                }
                if (!Optional.ofNullable(racun.getEmailoviSZvjezdicomString()).map(String::isBlank).orElse(true)) {
                    if (racun.getEmailoviSZvjezdicom().size() == 1) {
                        sqlUpit.append(" AND EMAILOVI_S_ZVJEZDICOM LIKE '%" + racun.getEmailoviSZvjezdicom().get(0).getId() + "%'");
                    } else {
                        sqlUpit.append(" AND (EMAILOVI_S_ZVJEZDICOM LIKE '%" + racun.getEmailoviSZvjezdicom().get(0).getId() + "%'");
                        for (int i = 1; i < racun.getEmailoviSZvjezdicom().size(); i++) {
                            sqlUpit.append(" OR EMAILOVI_S_ZVJEZDICOM LIKE '%" + racun.getEmailoviSZvjezdicom().get(i).getId() + "%'");
                        }
                        sqlUpit.append(")");
                    }
                }
            }
            PreparedStatement upit = veza.prepareStatement(sqlUpit.toString());
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                String mailAdresa = resultSet.getString("korisnik");
                List<Long> poslaniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("poslani_emailovi").length() == 1) {
                    poslaniEmailoviIds.add(Long.parseLong(resultSet.getString("poslani_emailovi")));
                } else if (resultSet.getString("poslani_emailovi").length() > 1) {
                    poslaniEmailoviIds = Arrays.stream(resultSet.getString("poslani_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> primljeniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("primljeni_emailovi").length() == 1) {
                    primljeniEmailoviIds.add(Long.parseLong(resultSet.getString("primljeni_emailovi")));
                } else if (resultSet.getString("primljeni_emailovi").length() > 1) {
                    primljeniEmailoviIds = Arrays.stream(resultSet.getString("primljeni_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> emailoviSaZvjezdicomIds = new ArrayList<>();
                if (resultSet.getString("emailovi_s_zvjezdicom").length() == 1) {
                    emailoviSaZvjezdicomIds.add(Long.parseLong(resultSet.getString("emailovi_s_zvjezdicom")));
                } else if (resultSet.getString("emailovi_s_zvjezdicom").length() > 1) {
                    emailoviSaZvjezdicomIds = Arrays.stream(resultSet.getString("emailovi_s_zvjezdicom").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Email> poslaniEmailovi = dohvatiEmailovePoIdu(poslaniEmailoviIds);
                List<Email> primljeniEmailovi = dohvatiEmailovePoIdu(primljeniEmailoviIds);
                List<Email> emailoviSaZvjezdicom = dohvatiEmailovePoIdu(emailoviSaZvjezdicomIds);
                List<Korisnik> korisnici = dohvatiKorisnikePremaKriterijima(new Korisnik.KorisnikBuilder(null, null, null, null, null, mailAdresa).createKorisnik());
                Korisnik korisnik = korisnici.get(0);
                Racun noviRacun = new Racun(korisnik);
                noviRacun.setPoslaniMailovi(poslaniEmailovi);
                noviRacun.setPrimljeniMailovi(primljeniEmailovi);
                noviRacun.setEmailoviSaZvjezdicom(emailoviSaZvjezdicom);
                listaRacuna.add(noviRacun);
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
        return listaRacuna;
    }

    public static Racun dohvatiRacunPremaKorisniku(String mailAdresaKorisnika) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("SELECT * FROM RACUN WHERE KORISNIK = ?");
            upit.setString(1, mailAdresaKorisnika);
            upit.executeQuery();
            ResultSet resultSet = upit.getResultSet();
            while (resultSet.next()) {
                String mailAdresa = resultSet.getString("korisnik");
                List<Long> poslaniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("poslani_emailovi").length() == 1) {
                    poslaniEmailoviIds.add(Long.parseLong(resultSet.getString("poslani_emailovi")));
                } else if (resultSet.getString("poslani_emailovi").length() > 1) {
                    poslaniEmailoviIds = Arrays.stream(resultSet.getString("poslani_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> primljeniEmailoviIds = new ArrayList<>();
                if (resultSet.getString("primljeni_emailovi").length() == 1) {
                    primljeniEmailoviIds.add(Long.parseLong(resultSet.getString("primljeni_emailovi")));
                } else if (resultSet.getString("primljeni_emailovi").length() > 1) {
                    primljeniEmailoviIds = Arrays.stream(resultSet.getString("primljeni_emailovi").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Long> emailoviSaZvjezdicomIds = new ArrayList<>();
                if (resultSet.getString("emailovi_s_zvjezdicom").length() == 1) {
                    emailoviSaZvjezdicomIds.add(Long.parseLong(resultSet.getString("emailovi_s_zvjezdicom")));
                } else if (resultSet.getString("emailovi_s_zvjezdicom").length() > 1) {
                    emailoviSaZvjezdicomIds = Arrays.stream(resultSet.getString("emailovi_s_zvjezdicom").split(" ")).map(Long::valueOf).collect(Collectors.toList());
                }
                List<Email> poslaniEmailovi = dohvatiEmailovePoIdu(poslaniEmailoviIds);
                List<Email> primljeniEmailovi = dohvatiEmailovePoIdu(primljeniEmailoviIds);
                List<Email> emailoviSaZvjezdicom = dohvatiEmailovePoIdu(emailoviSaZvjezdicomIds);
                List<Korisnik> korisnici = dohvatiKorisnikePremaKriterijima(new Korisnik.KorisnikBuilder(null, null, null, null, null, mailAdresa).createKorisnik());
                Korisnik korisnik = korisnici.get(0);
                Racun noviRacun = new Racun(korisnik);
                noviRacun.setPoslaniMailovi(poslaniEmailovi);
                noviRacun.setPrimljeniMailovi(primljeniEmailovi);
                noviRacun.setEmailoviSaZvjezdicom(emailoviSaZvjezdicom);
                return noviRacun;
            }
            return null;
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void spremiRacun(Racun racun) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("INSERT INTO RACUN(korisnik, poslani_emailovi, primljeni_emailovi, emailovi_s_zvjezdicom) VALUES(?, ?, ?, ?)");
            upit.setString(1, racun.getKorisnik().getMailAdresa());
            upit.setString(2, racun.getPoslaniEmailovi().stream().map(email -> String.valueOf(email.getId())).collect(Collectors.joining(" ")));
            upit.setString(3, racun.getPrimljeniEmailovi().stream().map(email -> String.valueOf(email.getId())).collect(Collectors.joining(" ")));
            upit.setString(4, racun.getEmailoviSZvjezdicom().stream().map(email -> String.valueOf(email.getId())).collect(Collectors.joining(" ")));
            upit.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static void spremiNoviRacun(Racun racun) throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            PreparedStatement upit = veza.prepareStatement("INSERT INTO RACUN(korisnik, poslani_emailovi, primljeni_emailovi, emailovi_s_zvjezdicom) VALUES(?, ?, ?, ?)");
            upit.setString(1, racun.getKorisnik().getMailAdresa());
            upit.setString(2, "");
            upit.setString(3, "");
            upit.setString(4, "");
            upit.executeUpdate();
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
    }

    public static Long zadnjiEmailId() throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu()) {
            String sql = "SELECT MAX(id) FROM EMAIL";
            PreparedStatement statement = veza.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long maxId = resultSet.getLong(1);
                return maxId;
            }
        } catch (SQLException | IOException ex) {
            String poruka = "Došlo je do pogreške u radu s bazom podataka";
            logger.error(poruka, ex);
            throw new BazaPodatakaException(poruka, ex);
        }
        return 0L;
    }

    public static Connection spajanjeNaBazu() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/database.properties"));

        String urlBaze = properties.getProperty("urlBaze");
        String korisnickoIme = properties.getProperty("korisnickoIme");
        String lozinka = properties.getProperty("lozinka");

        return DriverManager.getConnection(urlBaze, korisnickoIme, lozinka);
    }
}
