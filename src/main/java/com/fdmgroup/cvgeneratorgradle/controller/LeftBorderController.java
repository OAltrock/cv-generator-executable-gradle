package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class LeftBorderController/* implements Initializable*/ {

    @FXML
    private BorderPane mainWindow;

    public void closeApp(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Exiting");
        alert.setContentText("Do you really want to exit");

        if(alert.showAndWait().get()== ButtonType.OK) {
            Stage stage = (Stage) mainWindow.getScene().getWindow();
            stage.close();
        }

    }

    public void showLeftBorder(ActionEvent actionEvent) {
        TreeItem<String> cv = new TreeItem<>("CV");
        TreeItem<String> summary = new TreeItem<>("Summary");
        TreeItem<String> details = new TreeItem<>("Details");
        TreeItem<String> personalInformation = new TreeItem<>("Personal Information");
        TreeItem<String> experience = new TreeItem<>("Experience");
        TreeItem<String> skills = new TreeItem<>("Skills");
        TreeItem<String> education = new TreeItem<>("Education");

        //setting up tree nodes (should be populated by a cv class in the future)
        details.getChildren().addAll(personalInformation, experience, education, skills);
        cv.getChildren().addAll(details, summary);

        //setting up tree view
        TreeView<String> leftBorderTreeView = createTreeView(cv);

        //setting up app navigation
        leftBorderTreeView.getSelectionModel().selectedItemProperty().addListener((o,oldV,newV)->{
            System.out.println(o);
            FXMLLoader fxmlLoader;
            try {
                switch (newV.getValue()) {
                    case "Personal Information" -> {
                        fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource("personalInfo.fxml"));
                        mainWindow.setCenter(fxmlLoader.load());
                    }
                    case "Experience" ->  {

                    }
                    case "Summary" -> {

                    }
                    case "Education" -> {

                    }
                    case "Skills" -> {

                    }
                    default -> {}
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private TreeView<String> createTreeView(TreeItem<String> cv) {
        TreeView<String> ret = new TreeView<>();
        ret.setRoot(cv);
        HBox leftBorderContainer = new HBox(ret);
        ret.setPrefWidth(370);
        mainWindow.setLeft(leftBorderContainer);
        return ret;
    }

    public void showInfo(ActionEvent actionEvent) {
        Label aboutLabel = new Label("This is the alpha version of the FDM CV Generator.\nIt is partially based on an previous full stack project.");
        Border b = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        aboutLabel.setBorder(b);
        aboutLabel.setPadding(new Insets(10));
        aboutLabel.setWrapText(true);

        VBox about = new VBox(aboutLabel);
        about.setPadding(new Insets(10));
        about.setAlignment(Pos.CENTER);
        mainWindow.setCenter(about);
    }
}
