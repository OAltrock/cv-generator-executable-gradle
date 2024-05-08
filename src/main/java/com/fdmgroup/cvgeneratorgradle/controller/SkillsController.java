package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Language;
import com.fdmgroup.cvgeneratorgradle.models.enums.LanguageLevel;


import com.fdmgroup.cvgeneratorgradle.views.SkillsPage;
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

public class SkillsController implements HasToggleableSaveButtons,
        HasAddableTextFields {

	private final CVTemplate cvTemplate;
	private SkillsPage page;
    private final TreeView<String> treeView;
	private final Stage stage;
	Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");

	public SkillsController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage) {
		this.cvTemplate = cvTemplate;
		this.treeView = treeView;
		this.stage=stage;
	}

    public void initialize(BorderPane main) {
        ObservableList<TextInputControl> textFields = FXCollections.observableArrayList();
		page = new SkillsPage(cvTemplate, textFields);
		Button[] buttons = new Button[] {page.getPrevBtn(), page.getNextBtn()};

		main.setCenter(page.createCenterPage(page.getCenterBox()));
		addValidationToSaveButtons(textFields,predicate.negate(), buttons);
		textFields.addAll(findAllTextFields(page.getCompetenceGridPane()));
		textFields.addAll(findAllTextFields(page.getCertificateGridPane()));
		textFields.addAll(findAllTextFields(page.getHobbiesGridPane()));

		createValidationForTextFields(string -> !string.matches("^.*[a-zA-Z]+.*$"), textFields,"Must contain at least one letter");

		buttons[0].setOnAction(actionEvent -> {
			assignInput();
			treeView.getSelectionModel().select(4);
			new EducationController(cvTemplate,treeView, stage).initialize(main);
		});
		buttons[1].setOnAction(actionEvent -> {
			assignInput();
			treeView.getSelectionModel().select(6);
			new SummaryController(cvTemplate, treeView, stage).initialize(main);
		});
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
			if (language.getText()!=null && !language.getText().isEmpty()) {
				MenuButton languageLlvBtn = (MenuButton) page.getLanguageGridPane().getChildren().get(page.getLanguageGridPane().getChildren().indexOf(language) + 1);
				languagesToAdd.add(new Language(language.getText(), (languageLlvBtn.getText()!=null)? LanguageLevel.valueOf(languageLlvBtn.getText()):LanguageLevel.C2));
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
