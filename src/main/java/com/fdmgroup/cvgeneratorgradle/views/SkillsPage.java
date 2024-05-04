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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Getter
public class SkillsPage extends FDMPage implements HasAddableTextFields {
    private final CVTemplate cvTemplate;
    private FDMCenterVBoxWrapper centerBox;
    Label pageTitle;
    Label competencesLabel;
    Label certificatesLabel;
    Label languagesLabel;
    Label hobbiesLabel;
    GridPane competenceGridPane;
    GridPane certificateGridPane;
    GridPane languageGridPane;
    GridPane hobbiesGridPane;

    ObservableList<TextInputControl> textFields;
    private FDMButton nextBtn;
    private FDMButton prevBtn;
    private FDMHBox buttonWrapper;

    public SkillsPage(CVTemplate cvTemplate, ObservableList<TextInputControl> textFields) {
        this.cvTemplate = cvTemplate;
        this.textFields = textFields;

        initialize();
    }

    private void initialize() {
        Location location = (cvTemplate.getLocation()==null) ?
                new Location("Germany", 1, 1,1, 3,1,
                        3, 1, 3, 0,1,
                        0, 1, 1, 3, 0, 3, false) :
                cvTemplate.getLocation();
        pageTitle = new Label("Skills");
        competencesLabel = new Label("Add " + location.getMinCompetence() + " to " + location.getMaxCompetence() + " Competences");
        List<TextInputControl> competencesToAdd = new ArrayList<>();

        HashSet<String> competencesTemp = (cvTemplate.getCompetences() == null) ? new HashSet<>() : cvTemplate.getCompetences();
        competencesTemp.forEach(competence -> {
            competencesToAdd.add(new TextField(competence));
        });
        //textFields.addAll(competencesToAdd);
        System.out.println(competencesToAdd);
        competenceGridPane = new GridPane(3, competencesToAdd.size());
        FDMButton addBtn = new FDMButton("Add competence");
        createAddableAreaFromModel(competencesToAdd, competenceGridPane, addBtn, textFields, location.getMaxCompetence(), "Remove competence", "Competence");

        certificatesLabel = new Label("Add " + location.getMinCertificate() + " to " + location.getMaxCertificate() + " Certificates");
        List<TextInputControl> certsToAdd = new ArrayList<>();
        HashSet<String> certsTemp = (cvTemplate.getCertificates() == null) ? new HashSet<>() : cvTemplate.getCertificates();
        certsTemp.forEach(competence -> {
            certsToAdd.add(new TextField(competence));
        });
        //textFields.addAll(certsToAdd);
        certificateGridPane = new GridPane(3, certsToAdd.size());
        FDMButton addCertBtn = new FDMButton("Add certificate");
        createAddableAreaFromModel(certsToAdd, certificateGridPane, addCertBtn, textFields, location.getMaxCertificate(), "Remove certificate", "Certificate");

        languagesLabel = new Label("Add " + location.getMinLanguage() + " to " + location.getMaxLanguage() + " Languages");
        List<TextInputControl> languagesToAdd = new ArrayList<>();
        HashSet<Language> languagesTemp = (cvTemplate.getLanguages() == null) ? new HashSet<>() : cvTemplate.getLanguages();
        languagesTemp.forEach(competence -> {
            languagesToAdd.add(new TextField(competence.getLanguageType()));
        });
        //textFields.addAll(languagesToAdd);
        //ToDo: language level
        languageGridPane = new GridPane(4, languagesToAdd.size());
        languageGridPane.setMinWidth(600);
        MenuButton languageLevelButton = new MenuButton("Choose language level");
        //languageLevelButton.setMinWidth(100);
        List<MenuItem> languageLevels = Arrays.stream(LanguageLevel.values()).map(languageLevel -> new MenuItem(languageLevel.toString())).toList();
        languageLevelButton.getItems().addAll(languageLevels);

        FDMButton addLanguageBtn = new FDMButton("Add language");
        createAddableAreaFromModel(languagesToAdd, languageGridPane,  addLanguageBtn, languageLevelButton, textFields, location.getMaxLanguage(), "Remove language", "Language", cvTemplate);

        hobbiesLabel = new Label("Add " + location.getMinInterest() + " to " + location.getMaxInterest() + " Hobbies or Interests");
        List<TextInputControl> hobbiesToAdd = new ArrayList<>();
        HashSet<String> hobbiesTemp = (cvTemplate.getInterests() == null) ? new HashSet<>() : cvTemplate.getInterests();
        hobbiesTemp.forEach(competence -> {
            hobbiesToAdd.add(new TextField(competence));
        });
        //textFields.addAll(hobbiesToAdd);
        hobbiesGridPane = new GridPane(3, hobbiesToAdd.size());
        FDMButton addHobbyBtn = new FDMButton("Add interest or hobby");
        createAddableAreaFromModel(hobbiesToAdd, hobbiesGridPane, addHobbyBtn, textFields, location.getMaxInterest(), "Remove interest", "Interest or hobby");

        prevBtn = new FDMButton("Previous");
        nextBtn = new FDMButton("Next");
        buttonWrapper = new FDMHBox(prevBtn, nextBtn);
        buttonWrapper.setDesign();

        centerBox = new FDMCenterVBoxWrapper(pageTitle, competencesLabel, competenceGridPane, certificatesLabel,
                certificateGridPane, languagesLabel, languageGridPane,
                hobbiesLabel, hobbiesGridPane, buttonWrapper);
        centerBox.setDesign();
    }
}
