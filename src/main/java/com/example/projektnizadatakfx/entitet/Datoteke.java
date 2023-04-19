package com.example.projektnizadatakfx.entitet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Datoteke {
    private static final Logger logger = LoggerFactory.getLogger(Datoteke.class);

    public static Map<String, String> dohvatiKorisnikeILozinke(){
        Map<String, String> map = new HashMap<>();
        try (Scanner scanner = new Scanner(new File("dat//korisnici-lozinke.txt"))){
            while (scanner.hasNextLine()){
                map.put(scanner.next(), scanner.next());
            }
        } catch (FileNotFoundException e) {
            logger.error("File korisnici-lozinke.txt nije pronaden");
            throw new RuntimeException("File not found");
        }
        return map;
    }

    public static boolean provjeriKorisnikaILozinku(String korisnik, String lozinka){
        lozinka = hashPassword(lozinka);
        if (!korisnik.contains("@email.com") && !korisnik.equals("admin")){
            korisnik += "@email.com";
        }
        Map<String, String> map = dohvatiKorisnikeILozinke();
        for (Map.Entry<String, String> entry : map.entrySet()){
            if (entry.getKey().equals(korisnik) && entry.getValue().equals(lozinka)){
                return true;
            }
        }
        return false;
    }

    public static String hashPassword(String password) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = messageDigest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    public static void spremiNovogKorisnikaILozinku(Korisnik korisnik){
        try (FileWriter fileWriter = new FileWriter("dat//korisnici-lozinke.txt", true)) {
            fileWriter.write("\n" + korisnik.getMailAdresa() + "@email.com " + hashPassword(korisnik.getLozinka()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> korisnciLozinke = new ArrayList<>();
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat\\korisnici-lozinke-promjene.dat"));) {
            try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("dat\\korisnici-lozinke-promjene.dat"));) {
                korisnciLozinke = (List<String>) in.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (EOFException ignored){}
            korisnciLozinke.add("Kreiran novi korisnik:  " + korisnik.getMailAdresa() + "@email.com " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
            out.writeObject(korisnciLozinke);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
