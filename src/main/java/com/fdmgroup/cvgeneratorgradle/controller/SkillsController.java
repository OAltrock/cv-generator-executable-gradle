package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Language;
import com.fdmgroup.cvgeneratorgradle.models.enums.LanguageLevel;

import com.fdmgroup.cvgeneratorgradle.views.SkillsPage;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.*;

import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class SkillsController extends FDMController implements HasToggleableSaveButtons,
        HasAddableTextFields {

    private SkillsPage page;
    Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");

    public SkillsController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage, Menu recent) {
        this.cvTemplate = cvTemplate;
        this.treeView = treeView;
        this.stage = stage;
        this.recent = recent;
    }

    public void initialize(BorderPane main, MainController mainController) {
        ObservableList<TextInputControl> textFields = FXCollections.observableArrayList();
        page = new SkillsPage(cvTemplate, textFields);

        //model will be set on mouse leaving edit view (all input will be saved when user clicks any navigation)
        page.getCenterBox().setOnMouseExited(event->{
            assignToModel();
        });
        Button[] buttons = new Button[]{page.getNext()};

        main.setCenter(page.createCenterPage(page.getCenterBox()));
        addValidationToSaveButtons(textFields, predicate.negate(), buttons);
        textFields.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                textFields.forEach(textInputControl -> textInputControl.setOnMouseClicked(actionEvent ->
                        assignInput(mainController)));
            }
        });
        textFields.addAll(findAllTextFields(page.getCompetenceGridPane()));
        textFields.addAll(findAllTextFields(page.getCertificateGridPane()));
        textFields.addAll(findAllTextFields(page.getHobbiesGridPane()));
        ObservableList<TextInputControl> languageInput = FXCollections.observableArrayList();


        validateMaybeEmptyTextFields(page.getLanguageLevelButtons(), page.getLanguageGridPane());

        Button[] buttonsForLanguageValidation = new Button[]{page.getPrev(), page.getNext()};
        validatePreviousBtn(page.getLanguageLevelButtons(), languageInput, page.getLanguageGridPane(), buttonsForLanguageValidation);
        languageInput.addAll(findAllTextFields(page.getLanguageGridPane()));
        createValidationForTextFields(string -> !string.matches("^.*[a-zA-Z]+.*$"), textFields, "Must contain at least one letter");

        page.getPrev().setOnMousePressed(actionEvent -> {
            assignInput(mainController);
            treeView.getSelectionModel().select(4);
            new EducationController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });
        buttons[0].setOnMousePressed(actionEvent -> {
            assignInput(mainController);
            treeView.getSelectionModel().select(6);
            new SummaryController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });

        languageInput.forEach(textInputControl ->
                textInputControl.setOnMouseClicked(actionEvent ->
                        assignInput(mainController)));
    }

    void assignInput(MainController mainController) {
        assignToModel();

        saveObjectAsJson(cvTemplate);
        try {
            mainController.loadRecentCV(stage);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //model should be set but not necessarily saved to file, therefore assign input is separated from saving
    private void assignToModel() {
        List<TextInputControl> competencesInput = findAllTextFields(page.getCompetenceGridPane());
       AbstractSet<String> competencesToAdd = new LinkedHashSet<>();
        competencesInput.forEach(competence -> {
            competencesToAdd.add(competence.getText());
        });
        cvTemplate.setKeySkills(competencesToAdd);

        List<TextInputControl> certificateInput = findAllTextFields(page.getCertificateGridPane());
        HashSet<String> certificatesToAdd = new HashSet<>();
        certificateInput.forEach(certificate -> {
            certificatesToAdd.add(certificate.getText());
        });
        cvTemplate.setCertificates(certificatesToAdd);

        List<TextInputControl> languageInput = findAllTextFields(page.getLanguageGridPane());
        HashSet<Language> languagesToAdd = new HashSet<>();
        languageInput.forEach(language -> {
            if (language.getText() != null && !language.getText().isEmpty()) {
                MenuButton languageLlvBtn = (MenuButton) page.getLanguageGridPane().getChildren().get(page.getLanguageGridPane().getChildren().indexOf(language) + 1);
                languagesToAdd.add(new Language(language.getText(), (!languageLlvBtn.getText().contains("Choose")) ? LanguageLevel.valueOf(languageLlvBtn.getText()) : LanguageLevel.C2));
            }
        });
        cvTemplate.setLanguages(languagesToAdd);

        List<TextInputControl> interestsInput = findAllTextFields(page.getHobbiesGridPane());
        HashSet<String> interestsToAdd = new HashSet<>();
        interestsInput.forEach(interest -> {
            interestsToAdd.add(interest.getText());
        });
        cvTemplate.setInterests(interestsToAdd);
    }
}
