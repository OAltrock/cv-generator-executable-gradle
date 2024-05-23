package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.fdmgroup.cvgeneratorgradle.utils.GeneratorConfig.*;
import static com.fdmgroup.cvgeneratorgradle.utils.LoadObjectFromJson.loadObjectFromJson;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.*;

public class MainController implements Initializable {

    @Setter
    CVTemplate cvTemplate;

    TreeView<String> treeView;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu recent;

    /*public MainController(CVTemplate cvTemplate) {
        System.out.println(cvTemplate);
        this.cvTemplate = cvTemplate;
    }

    public MainController() {
    }*/

    public void closeApp(ActionEvent e) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Exiting");
        alert.setContentText("Do you really want to exit");


        if (alert.showAndWait().isPresent() && alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) mainWindow.getScene().getWindow();
            System.out.println("got here");
            saveRecent(recentFiles, recentFileNames);
            stage.close();
        }

    }

    public void createNewCV(ActionEvent actionEvent) {
        cvTemplate = new CVTemplate();
        setLabel("Fill out the specific sections of your CV");
        showLeftBorder(actionEvent);
    }


    public void showLeftBorder(ActionEvent actionEvent) {
        TreeItem<String> cv = new TreeItem<>("CV");
        TreeItem<String> summary = new TreeItem<>("Summary");
        TreeItem<String> details = new TreeItem<>("Details");
        TreeItem<String> personalInformation = new TreeItem<>("Personal Information");
        TreeItem<String> experience = new TreeItem<>("Experience");
        TreeItem<String> skills = new TreeItem<>("Skills");
        TreeItem<String> education = new TreeItem<>("Education");
        TreeItem<String> profile = new TreeItem<>("Profile");
        List<TreeItem<String>> detailsItems = List.of(profile, personalInformation, experience, education, skills);
        List<TreeItem<String>> rootItems = List.of(details, summary);

        details.getChildren().addAll(detailsItems);
        cv.getChildren().addAll(rootItems);

        //setting up tree view
        treeView = createTreeView(cv);
        treeView.setPrefWidth(300);
        treeView.setShowRoot(false);
        treeView.getRoot().getChildren().forEach(child -> child.setExpanded(true));

        //setting up app navigation
        //o is observable, oldV is old value (those two are not being used); newV is new value (ie: what the user has selected)
        treeView.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {

            switch (newV.getValue()) {
                case "Personal Information" -> {
                    new PersonalInformationController(cvTemplate, treeView,
                            (Stage) mainWindow.getScene().getWindow()).initialize(mainWindow, recent, this);
                }
                case "Experience" -> {
                    new ExperienceController(cvTemplate,treeView,
                            (Stage) mainWindow.getScene().getWindow()).initialize(mainWindow, recent, this);
                }
                case "Summary" -> {
                    new SummaryController(cvTemplate,treeView,
                            (Stage) mainWindow.getScene().getWindow()).initialize(mainWindow, recent, this);
                }
                case "Education" -> {
                    new EducationController(cvTemplate,treeView,
                            (Stage) mainWindow.getScene().getWindow()).initialize(mainWindow, recent, this);
                }
                case "Skills" -> {
                    new SkillsController(cvTemplate, treeView,
                            (Stage) mainWindow.getScene().getWindow()).initialize(mainWindow, recent, this);
                }
                case "Profile" -> {
                    new ProfileController(cvTemplate,treeView,
                            (Stage) mainWindow.getScene().getWindow()).initialize(mainWindow, recent, this);
                }


                default -> {
                }
            }
        });
    }


    public void loadCV(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load CV");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON File", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(mainWindow.getScene().getWindow());
        if (selectedFile!=null) {
            cvTemplate = loadObjectFromJson(selectedFile.getPath());
            showLeftBorder(event);
            new SummaryController(cvTemplate,treeView, (Stage) mainWindow.getScene().getWindow()).initialize(mainWindow, recent, this);
        }
    }

    public void loadRecentCV(Stage stage) throws FileNotFoundException {
        List<MenuItem> newRecent = new ArrayList<>(recentFiles.values().stream().map(k->{
            MenuItem recentFile = new MenuItem(k);
            recentFile.setOnAction(actionEvent -> {
                try {
                    cvTemplate = loadObjectFromJson(recentFile.getText());
                    showLeftBorder(new ActionEvent());
                    new SummaryController(cvTemplate, treeView, stage).initialize(mainWindow,recent, this);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            recentFile.setStyle("-fx-color: white");
            return recentFile;
        }).toList());
        recent.getItems().setAll(newRecent.reversed());
    }

    /**
     * sets up the label in the center (ie: the about-label)
     * @param message custom message for the center-label
     */
    private void setLabel(String message) {
        setCenter();
        VBox centerDefault = (VBox) mainWindow.getCenter();
        Label messageLabel = (Label) centerDefault.lookup("#centerDefaultLabel");
        Border b = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT));
        messageLabel.setBorder(b);
        messageLabel.setPadding(new Insets(10));
        messageLabel.setWrapText(true);
        messageLabel.setText(message);
    }

    /**
     * sets center of the main BorderPane to the about-page ({@link BorderPane}
     */
    private void setCenter() {
        FXMLLoader loader = new FXMLLoader(CVGeneratorApp.class.getResource("centerDefault.fxml"));
        try {
            //loadRecentCV((Stage) mainWindow.getScene().getWindow());
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
        setLabel("This is the alpha version of the FDM CV Generator.\nIt is partially based on a previous full stack project.");
    }

    //Option to initialize a cvTemplate object without any bound actions (ie: load last edited cv automatically)
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recent.setStyle("-fx-color: white");
        recent.getItems().addAll(recentFiles.entrySet().stream().map(k-> {
            MenuItem recentFile = new MenuItem(k.getValue());
            recentFile.setStyle("-fx-color: white");
            return recentFile;
        }).toList());
        recentFileNames = loadRecentFileNames();
        recentFiles = loadRecentFiles();
        menuBar.setOnMouseEntered(event -> {
            loadRecent();
        });
    }

    public void loadRecent() {
        try {
            loadRecentCV((Stage) mainWindow.getScene().getWindow());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCV() {
        File selectedFile = selectFileFromFileChooser("Save", "JSON File", "*.json", (Stage) mainWindow.getScene().getWindow());
        if (selectedFile != null) {
            saveObjectAsJson(cvTemplate, selectedFile.getPath(), recent, cvTemplate);
            try {
                this.loadRecentCV((Stage) mainWindow.getScene().getWindow());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
