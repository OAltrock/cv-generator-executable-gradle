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

        if (alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) mainWindow.getScene().getWindow();
            stage.close();
        }

    }

    public void showLeftBorder(ActionEvent actionEvent) {
        //setting center scene
        setLabel("Fill out the specific sections of your CV");

        TreeItem<String> cv = new TreeItem<>("CV");
        TreeItem<String> summary = new TreeItem<>("Summary");
        TreeItem<String> details = new TreeItem<>("Details");
        TreeItem<String> personalInformation = new TreeItem<>("Personal Information");
        TreeItem<String> experience = new TreeItem<>("Experience");
        TreeItem<String> skills = new TreeItem<>("Skills");
        TreeItem<String> education = new TreeItem<>("Education");
        TreeItem<String> profile = new TreeItem<>("Profile");

        //setting up tree nodes (should be populated by a cv class in the future)
        details.getChildren().addAll(personalInformation, experience, education, skills,profile);
        cv.getChildren().addAll(details, summary);

        //setting up tree view
        TreeView<String> leftBorderTreeView = createTreeView(cv);

        //setting up app navigation
        leftBorderTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {

            switch (newV.getValue()) {
                case "Personal Information" -> {
                    setCenter("personalInfo.fxml");
                }
                case "Experience" -> {

                }
                case "Summary" -> {

                }
                case "Education" -> {
                    new EducationController(mainWindow).initialize();
                    /*setCenter("education.fxml");*/
                }
                case "Skills" -> {

                }
                default -> {
                }
            }
        });
    }

    private void setLabel(String message) {
        setCenter("centerDefault.fxml");
        VBox centerDefault =(VBox) mainWindow.getCenter();
        Label messageLabel = (Label) centerDefault.lookup("#centerDefaultLabel");
        Border b = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT));
        messageLabel.setBorder(b);
        messageLabel.setPadding(new Insets(10));
        messageLabel.setWrapText(true);
        messageLabel.setText(message);
    }

    private void setCenter(String resource) {
        FXMLLoader loader = new FXMLLoader(CVGeneratorApp.class.getResource(resource));
        try {
            mainWindow.setCenter(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        setLabel("This is the alpha version of the FDM CV Generator.\nIt is partially based on an previous full stack project.");
    }
}
