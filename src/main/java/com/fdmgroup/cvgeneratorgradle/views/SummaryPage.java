package com.fdmgroup.cvgeneratorgradle.views;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Education;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

@Getter
public class SummaryPage extends FDMPage {
    private CVTemplate cvTemplate;

    private FDMCenterVBoxWrapper centerBox;

    private final Label profileLabel = new Label("Profile");
    private final TextArea profile = new TextArea();

    private final Label personalInformationLabel = new Label("Personal Information");
    private final ListView<String> personalInformation = new ListView<>();

    private final Label experienceLabel = new Label("Experiences");
    private final ListView<String> experience = new ListView<>();

    private final Label educationLabel = new Label("Education");
    private final ListView<String> education = new ListView<>();

    private final Label competencesLabel = new Label("Competences");
    private final ListView<String> competences = new ListView<>();
    private final Label certificatesLabel = new Label("Certificates");
    private final ListView<String> certificates = new ListView<>();
    private final Label languagesLabel = new Label("Languages");
    private final ListView<String> languages = new ListView<>();
    private final Label interestsLabel = new Label("Interests");
    private final ListView<String> interests = new ListView<>();
    private GridPane summaryGrid;

    private final FDMButton saveCV = new FDMButton("Save CV");
    private final MenuButton generateCV = new MenuButton("Generate Cv", null,
            new MenuItem("Generate PDF"), new MenuItem("Generate docx"));

    public SummaryPage(CVTemplate cvTemplate) {
        this.cvTemplate = cvTemplate;
        initialize();
    }

    private void initialize() {
        profile.setText((cvTemplate.getProfile() != null) ? cvTemplate.getProfile() : "");
        profile.setWrapText(true);

        personalInformation.setMinHeight(130);

        experience.setMinHeight(130);
        if (cvTemplate.getExperiences() == null) {
            cvTemplate.setExperiences(new ArrayList<>());
            cvTemplate.getExperiences().add(new Experience("", "", "",
                    new ArrayList<>(), "", "", ""));
        }
        //if there are more than one, nodes could be created for each one (same for education)
        Experience lastExperience = cvTemplate.getExperiences().getLast();
        String ongoing = (!lastExperience.getEndDate().isEmpty()) ? (LocalDate.parse(lastExperience.getEndDate()).isAfter(LocalDate.now())) ? "ongoing" : lastExperience.getEndDate() : "";
        ObservableList<String> experienceList = FXCollections.observableArrayList(
                "Job title: " + lastExperience.getJobTitle(),
                "Company: " + lastExperience.getCompanyName(),
                "at: " + lastExperience.getCompanyPlace(),
                "from: " + lastExperience.getStartDate(),
                "to: " + ongoing);
        if (lastExperience.getPositionFeatures() != null) {
            lastExperience.getPositionFeatures().forEach(positionFeature -> {
                experienceList.add("Position feature: " + positionFeature);
            });
        }
        experience.setItems(experienceList);

        education.setMinHeight(130);
        if (cvTemplate.getEducations() == null) {
            cvTemplate.setEducations(new ArrayList<>());
            cvTemplate.getEducations().add(new Education("", "", "",
                    "", "", "", "", new ArrayList<>()));
        }
        Education lastEducation = cvTemplate.getEducations().getLast();
        String ongoingEducation = (!lastEducation.getEndDate().isEmpty()) ?
                (LocalDate.parse(lastEducation.getEndDate()).isAfter(LocalDate.now())) ?
                        "ongoing" : lastEducation.getEndDate()
                : "";
        ObservableList<String> educationList = FXCollections.observableArrayList(
                "Degree: " + lastEducation.getDegree(),
                "Study title: "+ lastEducation.getStudyTitle(),
                "University: " + lastEducation.getUniversityName(),
                "at: " + lastEducation.getUniversityPlace(),
                "Thesis title: " + lastEducation.getThesisTitle(),
                "from: " + lastEducation.getStartDate(),
                "to: " + ongoingEducation);
        if (lastEducation.getKeyModules() != null) {
            lastEducation.getKeyModules().forEach(positionFeature -> {
                educationList.add("Key module: " + positionFeature);
            });
        }
        education.setItems(educationList);

        competences.setMinHeight(200);
        competences.setMinWidth(250);
        ObservableList<String> competencesList = FXCollections.observableArrayList((cvTemplate.getCompetences() != null) ? cvTemplate.getCompetences() : new ArrayList<>());
        competences.setItems(competencesList);

        certificates.setMinHeight(200);
        certificates.setMinWidth(250);
        ObservableList<String> certificatesList = FXCollections.observableArrayList((cvTemplate.getCertificates() != null) ? cvTemplate.getCertificates() : new ArrayList<>());
        certificates.setItems(certificatesList);

        interests.setMinHeight(200);
        ObservableList<String> interestsList = FXCollections.observableArrayList((cvTemplate.getInterests() != null) ? cvTemplate.getInterests() : new ArrayList<>());
        interests.setItems(interestsList);

        languages.setMinHeight(200);
        ObservableList<String> languagesList = FXCollections.observableArrayList();
        if (cvTemplate.getLanguages() == null) cvTemplate.setLanguages(new HashSet<>());
        cvTemplate.getLanguages().forEach(language -> {
            languagesList.add(language.getLanguageType() + " " + language.getLanguageLevel());
        });
        languages.setItems(languagesList);

        summaryGrid = new GridPane(2, 4);
        summaryGrid.add(competencesLabel, 0, 0);
        summaryGrid.add(certificatesLabel, 1, 0);
        summaryGrid.add(competences, 0, 1);
        summaryGrid.add(certificates, 1, 1);
        summaryGrid.add(interestsLabel, 0, 2);
        summaryGrid.add(languagesLabel, 1, 2);
        summaryGrid.add(interests, 0, 3);
        summaryGrid.add(languages, 1, 3);

        centerBox = new FDMCenterVBoxWrapper(profileLabel, profile, personalInformationLabel, personalInformation,
                experienceLabel, experience, educationLabel, education, summaryGrid, saveCV, generateCV);
        centerBox.setDesign();
        saveCV.setDesign("primary");

    }


}
