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
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.List;

import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class SkillsController extends FDMController implements HasToggleableSaveButtons,
        HasAddableTextFields {

    private final CVTemplate cvTemplate;
    private SkillsPage page;
    private final TreeView<String> treeView;
    private final Stage stage;
    Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");

    public SkillsController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage) {
        this.cvTemplate = cvTemplate;
        this.treeView = treeView;
        this.stage = stage;
    }

    public void initialize(BorderPane main) {
        ObservableList<TextInputControl> textFields = FXCollections.observableArrayList();
        page = new SkillsPage(cvTemplate, textFields);
        Button[] buttons = new Button[]{page.getNextBtn()};

        main.setCenter(page.createCenterPage(page.getCenterBox()));
        //ToDo: validation for at least one language (this is analogue for all not-addable-fields once the addable fields have been changed to not-addable)
        //ToDo: if there is input for a language there must be a language level
        //ToDo: as of now, there is an exception if there is language input but no language level
        addValidationToSaveButtons(textFields, predicate.negate(), buttons);
        textFields.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                textFields.forEach(textInputControl -> textInputControl.setOnMouseClicked(actionEvent ->
                        assignInput()));
            }
        });
        textFields.addAll(findAllTextFields(page.getCompetenceGridPane()));
        textFields.addAll(findAllTextFields(page.getCertificateGridPane()));
        textFields.addAll(findAllTextFields(page.getHobbiesGridPane()));
        ObservableList<TextInputControl> languageInput = FXCollections.observableArrayList();


        validateMaybeEmptyTextFields(page.getLanguageLevelButtons(), page.getLanguageGridPane());

		/*findAllTextFields(page.getLanguageGridPane()).stream().filter(
						textInputControl -> textInputControl.getText()!=null *//*&& !textInputControl.getText().isEmpty()*//*)
				.forEach(languageInput::add);*/

        //ToDo: validation not completely responsive yet
        Button[] buttonsForLanguageValidation = new Button[]{page.getPrevBtn(), page.getNextBtn()};
        validatePreviousBtn(page.getLanguageLevelButtons(), languageInput, page.getLanguageGridPane(), buttonsForLanguageValidation);
        languageInput.addAll(findAllTextFields(page.getLanguageGridPane()));
        createValidationForTextFields(string -> !string.matches("^.*[a-zA-Z]+.*$"), textFields, "Must contain at least one letter");

        page.getPrevBtn().setOnAction(actionEvent -> {
            assignInput();
            treeView.getSelectionModel().select(4);
            new EducationController(cvTemplate, treeView, stage).initialize(main);
        });
        buttons[0].setOnAction(actionEvent -> {
            assignInput();
            treeView.getSelectionModel().select(6);
            new SummaryController(cvTemplate, treeView, stage).initialize(main);
        });

        languageInput.forEach(textInputControl ->
                textInputControl.setOnMouseClicked(actionEvent ->
                        assignInput()));
    }

    private void assignInput() {
        List<TextInputControl> competencesInput = findAllTextFields(page.getCompetenceGridPane());
        HashSet<String> competencesToAdd = new HashSet<>();
        competencesInput.forEach(competence -> {
            competencesToAdd.add(competence.getText());
        });
        cvTemplate.setCompetences(competencesToAdd);

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

        saveObjectAsJson(cvTemplate);
    }
}
