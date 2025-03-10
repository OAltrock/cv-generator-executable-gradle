package com.fdmgroup.cvgeneratorgradle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import static com.fdmgroup.cvgeneratorgradle.utils.GeneratorConfig.*;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.recentFileNames;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.recentFiles;

@RequiredArgsConstructor
public class CVGeneratorApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource("welcome2.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("FDM CV-Generator");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "images/FDM_Logo_Green_RGB.png"))));
        stage.setScene(scene);

        scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Figtree:ital,wght@0,300..900;1,300..900&display=swap");

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/welcomePage.css")).toExternalForm());

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
            saveRecent(recentFiles, recentFileNames, (BorderPane) stage.getScene().lookup("#mainWindow"));
            stage.close();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}