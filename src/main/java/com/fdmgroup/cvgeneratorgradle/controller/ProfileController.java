package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.views.FDMButton;
import com.fdmgroup.cvgeneratorgradle.views.FDMCenterVBoxWrapper;
import com.fdmgroup.cvgeneratorgradle.views.ProfilePage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Predicate;
import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class ProfileController implements HasToggleableSaveButtons, HasAddableTextFields {

    private final Stage stage;
    private final CVTemplate cvTemplate;
    private final String profile;
    private final TreeView<String> treeView;

    public ProfileController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage) {
        this.stage = stage;
        this.cvTemplate = cvTemplate;
        profile = cvTemplate.getProfile();
        this.treeView = treeView;
    }

    public void initialize(BorderPane main) {
        ObservableList<TextInputControl> textAreas = FXCollections.observableArrayList();
        ProfilePage page = new ProfilePage(profile, textAreas);

        main.setCenter(page.createCenterPage(page.getCenterBox()));
        FDMCenterVBoxWrapper centerBox = page.getCenterBox();
        FDMButton saveBtn = page.getNext();

        Predicate<String> atLeast50Chars = (string -> string.length()>=50 && string.matches("^.*\\w+.*$"));

        addValidationToSaveButtons(textAreas,atLeast50Chars, saveBtn);


        textAreas.addAll(findAllTextFields(centerBox));

        createValidationForTextFields(atLeast50Chars.negate(), textAreas, "Write at least 50 letters.");

        saveBtn.setOnAction(actionEvent -> {
            cvTemplate.setProfile(page.getProfile().getText());
            saveObjectAsJson(cvTemplate);
            treeView.getSelectionModel().select(2);
            new PersonalInformationController(cvTemplate, treeView, stage).initialize(main);
        });

        page.getPrev().setVisible(false);
    }
}
