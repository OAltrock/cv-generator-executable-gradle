package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.User;
import com.fdmgroup.cvgeneratorgradle.views.SummaryPage;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class SummaryController implements InitializableFXML {
    CVTemplate cvTemplate;
    TreeView<String> treeView;
    private final Stage stage;

    public SummaryController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage) {
        this.cvTemplate = cvTemplate;
        this.treeView = treeView;
        this.stage = stage;
    }

    @Override
    public void initialize(BorderPane main, String resource) {
        SummaryPage page = new SummaryPage(cvTemplate, stage);
        main.setCenter(page.createCenterPage(page.getCenterBox()));
        if (cvTemplate.getUser()==null) cvTemplate.setUser(new User("","",""));
        ObservableList<String> personalInformationList = FXCollections.observableArrayList(
                "First name: "+ cvTemplate.getUser().getFirstName(), "Last name: "+ cvTemplate.getUser().getLastName(),
                "Email: " + cvTemplate.getUser().getEmail());

        page.getPersonalInformation().setItems(personalInformationList);
        page.getSaveCV().setOnAction(action -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON File","*.json"));
            File selectedFile = fileChooser.showSaveDialog(stage);
            System.out.println(selectedFile.getPath());
            if (!selectedFile.getName().isEmpty()) saveObjectAsJson(cvTemplate, selectedFile.getPath());
        });
        //page.getPersonalInformation().prefHeightProperty().bind(Bindings.size(personalInformationList).multiply(18));
        /*InitializableFXML.super.initialize(main, resource);
        ScrollPane scrollPane = (ScrollPane) main.getCenter();

        VBox center = (VBox) scrollPane.getContent();
        TextArea profile = (TextArea) center.lookup("#profile");
        ListView<String> personalInformation = (ListView<String>) center.lookup("#personalInformationLV");


        GridPane gridPaneSummary = (GridPane) center.getChildren().get(8);

        ListView<String> competencesListView = (ListView) gridPaneSummary.lookup("#competencesListView");
        competencesListView.prefHeightProperty().bind(Bindings.size(competencesListView.getItems()).multiply(18));
        competencesListView.getItems().addAll("- Java", "- HTML/ CSS/ JavaScript", "- Junit", "- Eclipse", "- Maven","- Git", "- MySQL", "- UML");*/

    }
}
