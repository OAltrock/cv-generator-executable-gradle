package com.fdmgroup.cvgeneratorgradle.views;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Language;
import com.fdmgroup.cvgeneratorgradle.models.Location;
import com.fdmgroup.cvgeneratorgradle.models.enums.LanguageLevel;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import lombok.Getter;

import java.util.*;

@Getter
public class SkillsPage extends FDMPage implements HasAddableTextFields {
    private final CVTemplate cvTemplate;

    String languageLevelSelected;
    Label pageTitle;
    Label competencesLabel;
    Label certificatesLabel;
    Label languagesLabel;
    Label hobbiesLabel;

    GridPane competenceGridPane;
    List<MenuButton> languageLevelButtons;
    GridPane certificateGridPane;
    GridPane languageGridPane;
    GridPane hobbiesGridPane;


    public SkillsPage(CVTemplate cvTemplate, ObservableList<TextInputControl> textFields) {
        this.cvTemplate = cvTemplate;
        this.textFields = textFields;
        languageLevelButtons = new ArrayList<>();

        initialize();
    }

    private void initialize() {
        Location location = (cvTemplate.getLocation() == null) ?
                new Location("Germany", 1, 1, 1, 3, 1,
                        3, 1, 3, 0, 1,
                        0, 1, 1, 3, 0, 3, false) :
                cvTemplate.getLocation();
        pageTitle = new Label("Skills");
        competencesLabel = new Label("Add " + location.getMinCompetence() + " to " + location.getMaxCompetence() + " key skills");
        List<TextInputControl> competencesToAdd = new ArrayList<>();

        AbstractSet<String> competencesTemp = (cvTemplate.getKeySkills() == null) ? new LinkedHashSet<>() : cvTemplate.getKeySkills();
        competencesTemp.forEach(competence -> competencesToAdd.add(new TextArea(competence)));

        competenceGridPane = new GridPane(3, competencesToAdd.size());
        FDMButton addBtn = new FDMButton("Add key skill");
        createAddableAreaFromModel(competencesToAdd, competenceGridPane, addBtn, textFields, location.getMaxCompetence(), "Remove competence", "Competence");

        certificatesLabel = new Label("Add " + location.getMinCertificate() + " to " + location.getMaxCertificate() + " Certificates");
        List<TextInputControl> certsToAdd = new ArrayList<>();
        HashSet<String> certsTemp = (cvTemplate.getCertificates() == null) ? new HashSet<>() : cvTemplate.getCertificates();
        certsTemp.forEach(competence -> certsToAdd.add(new TextArea(competence)));
        certificateGridPane = new GridPane(3, certsToAdd.size());
        FDMButton addCertBtn = new FDMButton("Add certificate");
        createAddableAreaFromModel(certsToAdd, certificateGridPane, addCertBtn, textFields, location.getMaxCertificate(), "Remove certificate", "Certificate");

        languagesLabel = new Label("Add " + location.getMinLanguage() + " to " + location.getMaxLanguage() + " Languages");
        languageGridPane = new GridPane(2, location.getMaxLanguage());
        languageGridPane.setMinWidth(600);
        if (cvTemplate.getLocation() == null) cvTemplate.setLocation(
                new Location("Germany",
                        1, 3, 1, 1,
                        1, 3, 1,
                        3, 1, 3,
                        1, 3, 1, 3,
                        1, 3, true));
        if (cvTemplate.getLanguages() != null) {
            Iterator<Language> languageIterator = cvTemplate.getLanguages().iterator();
            for (int i = 0; i < cvTemplate.getLocation().getMaxLanguage(); i++) {
                List<MenuItem> languageLevels = Arrays.stream(LanguageLevel.values()).map(languageLevel ->
                {
                    MenuItem menuItem = new MenuItem(languageLevel.toString());
                    menuItem.setOnAction(action -> languageLevelSelected = menuItem.getText());
                    return menuItem;
                }).toList();
                Language current =  (languageIterator.hasNext()) ? languageIterator.next() : null;
                setLanguageRow(Objects.requireNonNullElseGet(current, Language::new), languageLevels, i);
            }
        }
        else {
            for (int i = 0; i < cvTemplate.getLocation().getMaxLanguage(); i++) {
                List<MenuItem> languageLevels = Arrays.stream(LanguageLevel.values()).map(languageLevel ->
                {
                    MenuItem menuItem = new MenuItem(languageLevel.toString());
                    menuItem.setOnAction(action -> languageLevelSelected = menuItem.getText());
                    return menuItem;
                }).toList();
                setLanguageRow(new Language(), languageLevels, i);
            }
        }

        hobbiesLabel = new Label("Add " + location.getMinInterest() + " to " + location.getMaxInterest() + " achievements or additional skills");
        List<TextInputControl> hobbiesToAdd = new ArrayList<>();
        HashSet<String> hobbiesTemp = (cvTemplate.getInterests() == null) ? new HashSet<>() : cvTemplate.getInterests();
        hobbiesTemp.forEach(competence -> hobbiesToAdd.add(new TextArea(competence)));
        hobbiesGridPane = new GridPane(3, hobbiesToAdd.size());
        FDMButton addHobbyBtn = new FDMButton("Add achievement or additional skill");
        createAddableAreaFromModel(hobbiesToAdd, hobbiesGridPane, addHobbyBtn, textFields, location.getMaxInterest(), "Remove achievement", "Achievement or additional skill");

        prev = new FDMButton("Previous");
        next = new FDMButton("Next");
        buttonWrapper = new FDMHBox(prev, next);
        buttonWrapper.setDesign();

        centerBox = new FDMCenterVBoxWrapper(pageTitle, competencesLabel, competenceGridPane, certificatesLabel,
                certificateGridPane, languagesLabel, languageGridPane,
                hobbiesLabel, hobbiesGridPane, buttonWrapper);
        centerBox.setDesign();
    }

    private void setLanguageRow(Language current, List<MenuItem> languageLevels, int i) {
        MenuButton languageLevelButton = (current.getLanguageType() == null) ? new MenuButton("Choose language level") : new MenuButton(current.getLanguageLevel().toString());
        languageLevelButton.getItems().addAll(languageLevels);
        addListenerToLanguageLevelButton(languageLevelButton);
        languageLevelButtons.add(languageLevelButton);


        TextInputControl textField = new TextField(current.getLanguageType());
        textField.setPromptText("Language");
        languageGridPane.add(textField, 0, i);
        languageGridPane.add(languageLevelButton, 1, i);
    }
}
