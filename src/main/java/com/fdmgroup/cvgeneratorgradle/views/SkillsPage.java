package com.fdmgroup.cvgeneratorgradle.views;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Location;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import lombok.Getter;

import java.util.ArrayList;
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
    /*TextField jobTitle;
    TextField companyName;
    TextField companyPlace;
    GridPane skillsGridPane;*/

    ObservableList<TextInputControl> textFields;
    //private List<TextInputControl> competencesToAdd;
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
        List<String> competencesTemp = (cvTemplate.getCompetences() == null) ? new ArrayList<>() : cvTemplate.getCompetences();
        competencesTemp.forEach(competence -> {
            competencesToAdd.add(new TextField(competence));
        });
        //textFields.addAll(competencesToAdd);
        competenceGridPane = new GridPane(3, competencesToAdd.size());
        FDMButton addBtn = new FDMButton("Add competence");
        createAddableAreaFromModel(competencesToAdd, competenceGridPane, addBtn, textFields, String.valueOf(location.getMaxCompetence()), "Remove competence", "Competence");

        certificatesLabel = new Label("Add " + location.getMinCertificate() + " to " + location.getMaxCertificate() + " Certificates");
        List<TextInputControl> certsToAdd = new ArrayList<>();
        List<String> certsTemp = (cvTemplate.getCertificates() == null) ? new ArrayList<>() : cvTemplate.getCertificates();
        certsTemp.forEach(competence -> {
            certsToAdd.add(new TextField(competence));
        });
        //textFields.addAll(certsToAdd);
        certificateGridPane = new GridPane(3, certsToAdd.size());
        FDMButton addCertBtn = new FDMButton("Add certificate");
        createAddableAreaFromModel(certsToAdd, certificateGridPane, addCertBtn, textFields, String.valueOf(location.getMaxCertificate()), "Remove certificate", "Certificate");

        languagesLabel = new Label("Add " + location.getMinLanguage() + " to " + location.getMaxLanguage() + " Languages");
        List<TextInputControl> languagesToAdd = new ArrayList<>();
        List<String> languagesTemp = (cvTemplate.getCompetences() == null) ? new ArrayList<>() : cvTemplate.getCompetences();
        languagesTemp.forEach(competence -> {
            languagesToAdd.add(new TextField(competence));
        });
        //textFields.addAll(languagesToAdd);
        //ToDo: language level
        languageGridPane = new GridPane(3, languagesToAdd.size());
        FDMButton addLanguageBtn = new FDMButton("Add language");
        createAddableAreaFromModel(languagesToAdd, languageGridPane, addLanguageBtn, textFields, String.valueOf(location.getMaxLanguage()), "Remove language", "Language");

        hobbiesLabel = new Label("Add " + location.getMinInterest() + " to " + location.getMaxInterest() + " Hobbies or Interests");
        List<TextInputControl> hobbiesToAdd = new ArrayList<>();
        List<String> hobbiesTemp = (cvTemplate.getInterests() == null) ? new ArrayList<>() : cvTemplate.getInterests();
        hobbiesTemp.forEach(competence -> {
            hobbiesToAdd.add(new TextField(competence));
        });
        //textFields.addAll(hobbiesToAdd);
        hobbiesGridPane = new GridPane(3, hobbiesToAdd.size());
        FDMButton addHobbyBtn = new FDMButton("Add interest or hobby");
        createAddableAreaFromModel(hobbiesToAdd, hobbiesGridPane, addHobbyBtn, textFields, String.valueOf(location.getMaxInterest()), "Remove interest", "Interest or hobby");

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
