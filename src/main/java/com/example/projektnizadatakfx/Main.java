package com.example.projektnizadatakfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static Stage newStage;

    @Override
    public void start(Stage stage) throws IOException {
        newStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setMainPage(GridPane root) {
        Scene scene = new Scene(root,700,600);
        newStage.setScene(scene);
        newStage.show();
    }

    public static void prikazi(String url){
        GridPane root;
        try {
            root = FXMLLoader.load(Main.class.getResource(url));
            Main.setMainPage(root);
        } catch (IOException e) {
            String poruka = "Došlo je do pogreške kod ucitavanja novog ekrana";
            logger.error(poruka, e);
            e.printStackTrace();
        }
    }
}