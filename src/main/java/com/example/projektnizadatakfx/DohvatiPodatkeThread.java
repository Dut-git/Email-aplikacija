package com.example.projektnizadatakfx;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class DohvatiPodatkeThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(DohvatiPodatkeThread.class);

    private String fileName;
    private TextArea textArea;

    public DohvatiPodatkeThread(String fileName, TextArea textArea) {
        this.fileName = fileName;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        List<String> promjene = new ArrayList<>();
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            promjene = (ArrayList<String>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            String poruka = "Došlo je do pogreške kod dohvacanja podataka iz datoteke";
            logger.error(poruka, e);
            throw new RuntimeException(e);
        }
        appendToTextArea(promjene);
    }

    private synchronized void appendToTextArea(List<String> promjene) {
        Platform.runLater(() -> {
            for (String promjena : promjene) {
                textArea.appendText(promjena + "\n");
            }
        });
    }
}
