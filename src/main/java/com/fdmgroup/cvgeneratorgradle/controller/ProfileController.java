package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.views.FDMButton;
import com.fdmgroup.cvgeneratorgradle.views.FDMCenterVBoxWrapper;
import com.fdmgroup.cvgeneratorgradle.views.ProfilePage;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class ProfileController extends FDMController implements HasToggleableSaveButtons, HasAddableTextFields {

    private final Stage stage;
    private final CVTemplate cvTemplate;
    private final String profile;
    private final TreeView<String> treeView;
    private ProfilePage page;

    public ProfileController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage, Menu recent) {
        this.stage = stage;
        this.cvTemplate = cvTemplate;
        profile = cvTemplate.getProfile();
        this.treeView = treeView;
        this.recent = recent;
    }

    public void initialize(BorderPane main, MainController mainController) {
        ObservableList<TextInputControl> textAreas = FXCollections.observableArrayList();
        page = new ProfilePage(profile, textAreas);
        page.getCenterBox().setOnMouseExited(event->{
            assignToModel();
        });

        main.setCenter(page.createCenterPage(page.getCenterBox()));
        FDMCenterVBoxWrapper centerBox = page.getCenterBox();
        FDMButton saveBtn = page.getNext();

        Predicate<String> atLeast50Chars = (string -> string.length() >= 50 && string.matches("^.*\\w+.*\\R*.*$"));

        addValidationToSaveButtons(textAreas, atLeast50Chars, saveBtn);
        textAreas.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                textAreas.forEach(textInputControl -> textInputControl.setOnMouseClicked(actionEvent -> {
                            assignInput(mainController);
                            try {
                                mainController.loadRecentCV(stage);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ));
            }
        });


        textAreas.addAll(findAllTextFields(centerBox));

        createValidationForTextFields(atLeast50Chars.negate(), textAreas, "Write at least 50 letters.");

        saveBtn.setOnMousePressed(actionEvent -> {
            assignInput(mainController);

            treeView.getSelectionModel().select(2);
            try {
                mainController.loadRecentCV(stage);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            new PersonalInformationController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });

        page.getPrev().setVisible(false);
    }

    void assignInput(MainController mainController) {
        assignToModel();
        saveObjectAsJson(cvTemplate);
    }

    private void assignToModel() {
        cvTemplate.setProfile(page.getProfile().getText());
    }
}
