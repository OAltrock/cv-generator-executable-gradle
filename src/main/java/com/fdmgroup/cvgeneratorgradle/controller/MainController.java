package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Education;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private CVTemplate cvTemplate;

    @FXML
    private BorderPane mainWindow;

    public void closeApp(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Exiting");
        alert.setContentText("Do you really want to exit");

        if (alert.showAndWait().isPresent() && alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) mainWindow.getScene().getWindow();
            stage.close();
        }

    }


    public void showLeftBorder(ActionEvent actionEvent) {

        //creating


        //setting center scene (will be deprecated once we use classes (CVTemplate)
        setLabel("Fill out the specific sections of your CV");

        TreeItem<String> cv = new TreeItem<>("CV");
        TreeItem<String> summary = new TreeItem<>("Summary");
        TreeItem<String> details = new TreeItem<>("Details");
        TreeItem<String> personalInformation = new TreeItem<>("Personal Information");
        TreeItem<String> experience = new TreeItem<>("Experience");
        TreeItem<String> skills = new TreeItem<>("Skills");
        TreeItem<String> education = new TreeItem<>("Education");
        TreeItem<String> profile = new TreeItem<>("Profile");
        List<TreeItem<String>> detailsItems = List.of(personalInformation, experience, education, skills, profile);
        List<TreeItem<String>> rootItems = List.of(details, summary);

        //setting up tree nodes (should be populated by a cv class in the future)
        details.getChildren().addAll(detailsItems);
        cv.getChildren().addAll(rootItems);

        //setting up tree view

        TreeView<String> leftBorderTreeView = createTreeView(cv);
        leftBorderTreeView.setPrefWidth(300);
        leftBorderTreeView.setShowRoot(false);
        leftBorderTreeView.getRoot().getChildren().forEach(child -> child.setExpanded(true));

        //setting up app navigation
        //o is observable, oldV is old value (those two are not being used); newV is new value (ie: what the user has selected)
        leftBorderTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {

            switch (newV.getValue()) {
                case "Personal Information" -> {
                    new PersonalInformationController().initialize(mainWindow, "personalInfo");
                }
                case "Experience" -> {
                    new ExperienceController().initialize(mainWindow, "experience2");
                }
                case "Summary" -> {
                    new SummaryController().initialize(mainWindow, "test");
                }
                case "Education" -> {
                    new EducationController(cvTemplate.getEducations()).initialize(mainWindow, "education2");
                    /*setCenter("education.fxml");*/
                }
                case "Skills" -> {
                    new SkillsController().initialize(mainWindow, "skills");
                }
                case "Profile" -> {
                    new ProfileController().initialize(mainWindow, "profile");
                }
                default -> {
                }
            }
        });
    }

    /**
     * sets up the label in the center (ie: the about-label)
     * @param message custom message for the center-label
     */
    private void setLabel(String message) {
        setCenter("centerDefault.fxml");
        VBox centerDefault = (VBox) mainWindow.getCenter();
        Label messageLabel = (Label) centerDefault.lookup("#centerDefaultLabel");
        Border b = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT));
        messageLabel.setBorder(b);
        messageLabel.setPadding(new Insets(10));
        messageLabel.setWrapText(true);
        messageLabel.setText(message);
    }

    /**
     * sets center of the main BorderPane ({@link BorderPane}
     * @param resource String of the FXML file name (eg: centerDefault.fxml) {@link FXMLLoader}
     */
    private void setCenter(String resource) {
        FXMLLoader loader = new FXMLLoader(CVGeneratorApp.class.getResource(resource));
        try {
            mainWindow.setCenter(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * sets up the tree view. The given root is set as the root of new tree view, which is wrapped in a HBox {@link HBox}.
     * This HBox is than set as the left of the BorderPane {@link BorderPane}
     * @param cv root TreeItem {@link TreeItem}
     * @return {@link TreeView}
     */
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

    //Option to initialize a cvtemplate object without any bound actions (ie: load last edited cv automatically)
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.cvTemplate = new CVTemplate();
    }
}
