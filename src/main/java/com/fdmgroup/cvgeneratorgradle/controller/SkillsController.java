package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Language;
import com.fdmgroup.cvgeneratorgradle.models.enums.LanguageLevel;
import com.fdmgroup.cvgeneratorgradle.utils.HelperClass;
import com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson;
import com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToDocument;
import com.fdmgroup.cvgeneratorgradle.views.FDMPage;
import com.fdmgroup.cvgeneratorgradle.views.SkillsPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;

public class SkillsController implements InitializableFXML, HasToggleableSaveButtons,
        HasAddableTextFields {

	private CVTemplate cvTemplate;
	private SkillsPage page;
	private ObservableList<TextInputControl> textFields;
	private TreeView<String> treeView;
	Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");

	public SkillsController(CVTemplate cvTemplate, TreeView<String> treeView) {
		this.cvTemplate = cvTemplate;
		this.treeView = treeView;
	}

	@Override
    public void initialize(BorderPane main, String resource) {
        /*InitializableFXML.super.initialize(main, resource);
        VBox center = (VBox) main.getCenter();*/
		textFields = FXCollections.observableArrayList();
		page = new SkillsPage(cvTemplate, textFields);
		Button[] buttons = new Button[] {page.getPrevBtn(), page.getNextBtn()};

		main.setCenter(page.createCenterPage(page.getCenterBox()));
		addValidationToSaveButtons(textFields,predicate.negate(), buttons);
		textFields.addAll(findAllTextFields(page.getCompetenceGridPane()));
		textFields.addAll(findAllTextFields(page.getCertificateGridPane()));
		textFields.addAll(findAllTextFields(page.getLanguageGridPane()));
		textFields.addAll(findAllTextFields(page.getHobbiesGridPane()));

		createValidationForTextFields(string -> !string.matches("^.*[a-zA-Z]+.*$"),textFields,"Must contain at least one letter");

		buttons[0].setOnAction(actionEvent -> {
			assignInput();
			treeView.getSelectionModel().select(4);
			new EducationController(cvTemplate,treeView).initialize(main,"");
		});
		buttons[1].setOnAction(actionEvent -> {
			assignInput();
			treeView.getSelectionModel().select(6);
			new SummaryController(cvTemplate, treeView).initialize(main,"summary");
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
			languagesToAdd.add(new Language(language.getText(), LanguageLevel.C2));
		});
		cvTemplate.setLanguages(languagesToAdd);

		List<TextInputControl> interestsInput = findAllTextFields(page.getHobbiesGridPane());
		HashSet<String> interestsToAdd = new HashSet<>();
		interestsInput.forEach(interest -> {
			interestsToAdd.add(interest.getText());
		});
		cvTemplate.setInterests(interestsToAdd);

		//Save cvTemplate as Json
		SaveObjectToJson.saveObjectAsJson(cvTemplate);
		Map<String, String> hashMap = HelperClass.convertCVObjectToHashMap(cvTemplate);
		System.out.println("Elements of the hashMap are:");
		for (Map.Entry<String, String> entry : hashMap.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
		try {
			SaveObjectToDocument.saveObjectAsWord(cvTemplate);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
