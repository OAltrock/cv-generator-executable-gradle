package com.fdmgroup.cvgeneratorgradle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CVGeneratorApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource("welcome2.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("FDM CV-Generator");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/FDM_Logo_Black_RGB.png")));
        stage.setScene(scene);

        scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Gloria+Hallelujah&family=Homemade+Apple&family=Inter:wght@400;600;700&family=JetBrains+Mono:ital@0;1&family=Josefin+Slab&family=Monoton&family=Permanent+Marker&family=Press+Start+2P&family=Special+Elite&family=Unbounded&family=Varela+Round&display=swap");

        scene.getStylesheets().add(getClass().getResource("css/welcomePage.css").toExternalForm());
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            closeApp(stage);
        });

    }

    public void closeApp(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Exiting");
        alert.setContentText("Do you really want to exit");

        if(alert.showAndWait().get()== ButtonType.OK) {
            stage.close();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}