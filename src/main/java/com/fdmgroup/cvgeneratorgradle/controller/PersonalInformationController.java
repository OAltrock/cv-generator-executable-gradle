package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Location;
import com.fdmgroup.cvgeneratorgradle.models.Stream;
import com.fdmgroup.cvgeneratorgradle.models.User;

import com.fdmgroup.cvgeneratorgradle.views.PersonalInfoPage;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class PersonalInformationController extends FDMController
        implements HasToggleableSaveButtons, HasAddableTextFields, HasDateValidation {


    private PersonalInfoPage page;
    private User user;
    private Stream stream;
    private Location location;
    private DatePicker start;
    private DatePicker end;

    public final static HashSet<String> TECHNICAL = new HashSet<>(List.of("Java", "HTML/CSS/JavaScript", "JUnit", "Eclipse", "Maven", "Git", "MySQL", "UML"));
    public final static HashSet<String> BUSINESS = new HashSet<>(List.of("MySQL", "BPMN", "UML"));

    public PersonalInformationController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage, Menu recent) {
        this.cvTemplate = cvTemplate;
        user = cvTemplate.getUser();
        stream = cvTemplate.getStream();
        location = cvTemplate.getLocation();
        this.treeView = treeView;
        this.stage = stage;
        this.recent = recent;
    }

    public void initialize(BorderPane main, MainController mainController) {
        ObservableList<TextInputControl> textFields = FXCollections.observableArrayList();
        if (user == null) user = new User("", "", "", "");
        if (stream == null) stream = new Stream("", "","", new ArrayList<>(), new HashSet<>());
        if (location == null) location = new Location("", 1, 1, 1, 3, 1, 3, 1, 3, 0, 1, 0, 1, 1, 3, 0, 3, false);
        page = new PersonalInfoPage(user, location, stream, textFields);
        main.setCenter(page.createCenterPage(page.getCenterBox()));
        Button[] buttons = new Button[]{page.getNextBtn()};
        page.getCenterBox().setOnMouseExited(event -> {
            assignToModel();
        });
        textFields.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                textFields.forEach(textInputControl -> textInputControl.setOnMouseClicked(actionEvent ->
                        assignInput(mainController)));
            }
        });
        start = page.getStreamStart();
        end = page.getStreamEnd();
        CheckBox checkBox = page.getOngoing();
        BiPredicate<LocalDate, LocalDate> checkDate = (startDate, endDate) -> {
            if (startDate == null) {
                return false;
            } else if (checkBox.isSelected()) {
                return !startDate.isAfter(LocalDate.now());
            } else if (endDate == null) return false;
            else {
                return (startDate.isBefore(endDate) || startDate.isEqual(endDate)) && startDate.isBefore(LocalDate.now());
            }
        };

        addValidationToSaveButtons(textFields, List.of(page.getStreamChooser(), page.getLocationChooser()), string -> !string.matches("^.*[a-zA-Z]+.*$"), buttons);

        textFields.addAll(List.of(page.getFirstName(), page.getLastName(), page.getEmail()));

        createValidationForTextFields(string -> !string.matches("^.*[a-zA-Z]+.*$"), textFields, "Must contain at least one letter");
        addValidationToDates(start,end, checkDate,checkBox);
        page.getStreamChooser().setValue((stream.getStreamName() != null) ? stream.getStreamName() : "");
        page.getLocationChooser().setValue((location.getLocationName() != null) ? location.getLocationName() : "");
        if (cvTemplate.getFdmSkills() == null) cvTemplate.setFdmSkills(new HashSet<>());
        page.getStreamChooser().setOnAction(actionEvent -> {
            if (Objects.equals(page.getStreamChooser().getValue(), "Technical")) {
                cvTemplate.getFdmSkills().removeAll(BUSINESS);
                cvTemplate.getFdmSkills().addAll(TECHNICAL);
                stream.setStreamName("Technical");
            } else if (Objects.equals(page.getStreamChooser().getValue(), "Business")) {
                cvTemplate.getFdmSkills().removeAll(TECHNICAL);
                cvTemplate.getFdmSkills().addAll(BUSINESS);
                stream.setStreamName("Business");
            }
        });

        page.getLocationChooser().setOnAction(actionEvent -> {
            if (Objects.equals(page.getLocationChooser().getValue(), "Germany")) {
                location = new Location("Germany", 1, 3, 1, 1,
                        1, 3, 1,
                        3, 1, 5, 1,
                        4, 1, 4, 0,
                        4, false);
            } else if (Objects.equals(page.getLocationChooser().getValue(), "International")) {
                location = new Location("International", 1, 1, 1,
                        3, 1, 3,
                        1, 3, 0,
                        1, 0, 1, 1,
                        3, 0, 3, false);
            }
        });

        buttons[0].setOnMousePressed(actionEvent -> {
            assignInput(mainController);
            treeView.getSelectionModel().select(3);
            new ExperienceController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });

        page.getPrevBtn().setOnMousePressed(actionEvent -> {
            assignInput(mainController);
            treeView.getSelectionModel().select(1);
            new ProfileController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });


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

    private void assignToModel() {
        user.setFirstName(page.getFirstName().getText());
        user.setLastName(page.getLastName().getText());
        user.setEmail(page.getEmail().getText());
        cvTemplate.setUser(user);

        stream.setStartDate((start.getValue()!=null) ? start.getValue().toString(): "");

        stream.setEndDate((page.getStreamEnd().getValue()!=null && !page.getOngoing().isSelected()) ? (LocalDate.parse(end.getValue().toString()).isAfter(LocalDate.now())) ?
                "9999-01-01" : end.getValue().toString()
                : "9999-01-01");

        cvTemplate.setStream(stream);
        if (cvTemplate.getFdmSkills() == null) cvTemplate.setFdmSkills(new HashSet<>());
        //ToDo: add addable competences
        cvTemplate.getFdmSkills().addAll(stream.getPresetCompetences());
        cvTemplate.setLocation(location);
    }
}
