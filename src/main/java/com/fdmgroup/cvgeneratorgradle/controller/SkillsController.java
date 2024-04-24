package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.views.FDMPage;
import com.fdmgroup.cvgeneratorgradle.views.SkillsPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;

public class SkillsController implements InitializableFXML, HasToggleableSaveButtons,
        HasAddableTextFields {

	private CVTemplate cvTemplate;
	private ObservableList<TextInputControl> textFields;
	private TreeView<String> treeView;
	Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");

	public SkillsController(CVTemplate cvTemplate) {
		this.cvTemplate = cvTemplate;
	}

	@Override
    public void initialize(BorderPane main, String resource) {
        /*InitializableFXML.super.initialize(main, resource);
        VBox center = (VBox) main.getCenter();*/
		textFields = FXCollections.observableArrayList();
		SkillsPage page = new SkillsPage(cvTemplate, textFields);
		Button[] buttons = new Button[] {page.getPrevBtn(), page.getNextBtn()};

		main.setCenter(page.createCenterPage(page.getCenterBox()));
		addValidationToSaveButtons(textFields,predicate.negate(), buttons);
		textFields.addAll(findAllTextFields(page.getCompetenceGridPane()));
		textFields.addAll(findAllTextFields(page.getCertificateGridPane()));
		textFields.addAll(findAllTextFields(page.getLanguageGridPane()));
		textFields.addAll(findAllTextFields(page.getHobbiesGridPane()));

		createValidationForTextFields(string -> !string.matches("^.*[a-zA-Z]+.*$"),textFields,"Must contain at least one letter");
	}
}
