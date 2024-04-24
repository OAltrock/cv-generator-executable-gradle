package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.views.FDMButton;
import com.fdmgroup.cvgeneratorgradle.views.FDMCenterVBoxWrapper;
import com.fdmgroup.cvgeneratorgradle.views.ProfilePage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Predicate;
import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;

public class ProfileController implements InitializableFXML, HasToggleableSaveButtons, HasAddableTextFields {

    private CVTemplate cvTemplate;
    private String profile;
    private ObservableList<TextInputControl> textAreas;
    private TreeView<String> treeView;

    public ProfileController(CVTemplate cvTemplate, TreeView<String> treeView) {
        this.cvTemplate = cvTemplate;
        profile = cvTemplate.getProfile();
        this.treeView = treeView;
    }

    @Override
    public void initialize(BorderPane main, String resource) {
        textAreas = FXCollections.observableArrayList();
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
            treeView.getSelectionModel().select(2);
            new PersonalInformationController(cvTemplate, treeView).initialize(main,"");
        });

        page.getPrev().setVisible(false);
    }
}
