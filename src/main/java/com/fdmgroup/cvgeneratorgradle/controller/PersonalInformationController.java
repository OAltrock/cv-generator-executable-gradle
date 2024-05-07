package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Location;
import com.fdmgroup.cvgeneratorgradle.models.Stream;
import com.fdmgroup.cvgeneratorgradle.models.User;
import com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson;
import com.fdmgroup.cvgeneratorgradle.views.PersonalInfoPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class PersonalInformationController implements InitializableFXML, HasToggleableSaveButtons, HasAddableTextFields {

    private final Stage stage;
    private PersonalInfoPage page;
    private User user;
    private Stream stream;
    private Location location;
    private ObservableList<TextInputControl> textFields;

    private CVTemplate cvTemplate;
    private TreeView<String> treeView;
    private final HashSet<String> TECHNICAL = new HashSet<>(List.of("Java", "HTML/CSS/JavaScript", "JUnit", "Eclipse", "Maven", "Git", "MySQL", "UML"));
    private final HashSet<String> BUSINESS = new HashSet<>(List.of("MySQL", "BPMN", "UML"));

    public PersonalInformationController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage) {
        this.cvTemplate = cvTemplate;
        user = cvTemplate.getUser();
        stream = cvTemplate.getStream();
        location = cvTemplate.getLocation();
        this.treeView = treeView;
        this.stage = stage;
    }

    @Override
    public void initialize(BorderPane main, String resource) {
        /*InitializableFXML.super.initialize(main, resource);
        VBox center = (VBox) main.getCenter();*/
        textFields = FXCollections.observableArrayList();
        if (user == null) user = new User("", "", "", "");
        if (stream == null) stream = new Stream("", new ArrayList<>(), new HashSet<>());
        if (location == null) location = new Location("", 1, 1, 1, 3, 1, 3, 1, 3, 0, 1, 0, 1, 1, 3, 0, 3, false);
        page = new PersonalInfoPage(user, location, stream, textFields);
        main.setCenter(page.createCenterPage(page.getCenterBox()));
        Button[] buttons = new Button[]{page.getPrevBtn(), page.getNextBtn()};
        //page.getPrevBtn().setDesign("primary");

        addValidationToSaveButtons(textFields, List.of(page.getStreamChooser(), page.getLocationChooser()), string -> !string.matches("^.*[a-zA-Z]+.*$"), buttons);

        textFields.addAll(List.of(page.getFirstName(), page.getLastName(), page.getEmail()));

        createValidationForTextFields(string -> !string.matches("^.*[a-zA-Z]+.*$"), textFields, "Must contain at least one letter");
        page.getStreamChooser().setValue((stream.getStreamName() != null) ? stream.getStreamName() : "");
        page.getLocationChooser().setValue((location.getLocationName() != null) ? location.getLocationName() : "");
        if (cvTemplate.getCompetences()==null) cvTemplate.setCompetences(new HashSet<>());
        page.getStreamChooser().setOnAction(actionEvent -> {
            if (Objects.equals(page.getStreamChooser().getValue(), "Technical")) {
                //ToDo: list should be addable
                cvTemplate.getCompetences().removeAll(BUSINESS);
                System.out.println(stream.getPresetCompetences());
                cvTemplate.getCompetences().addAll(TECHNICAL);
                stream.setStreamName("Technical");
            } else if (Objects.equals(page.getStreamChooser().getValue(), "Business")) {
                cvTemplate.getCompetences().removeAll(TECHNICAL);
                cvTemplate.getCompetences().addAll(BUSINESS);
                stream.setStreamName("Business");
            }
        });

        page.getLocationChooser().setOnAction(actionEvent -> {
            if (Objects.equals(page.getLocationChooser().getValue(), "Germany")) {
                location = new Location("Germany", 1, 5, 1, 1,
                        1, 3, 1,
                        3, 1, 5, 1,
                        5, 1, 5, 0,
                        5, false);
            } else if (Objects.equals(page.getLocationChooser().getValue(), "International")) {
                location = new Location("International", 1, 1, 1,
                        3, 1, 3,
                        1, 3, 0,
                        1, 0, 1, 1,
                        3, 0, 3, false);
            }
        });

        buttons[1].setOnAction(actionEvent -> {
            assignInfoInput();
            treeView.getSelectionModel().select(3);
            new ExperienceController(cvTemplate, treeView, stage).initialize(main, "");
        });

        buttons[0].setOnAction(actionEvent -> {
            assignInfoInput();
            treeView.getSelectionModel().select(1);
            new ProfileController(cvTemplate, treeView, stage).initialize(main, "");
        });


    }

    private void assignInfoInput() {

        user.setFirstName(page.getFirstName().getText());
        user.setLastName(page.getLastName().getText());
        user.setEmail(page.getEmail().getText());
        cvTemplate.setUser(user);

        cvTemplate.setStream(stream);
        HashSet<String> temp = cvTemplate.getCompetences();
        if (temp == null) temp = new HashSet<>();
        temp.addAll(stream.getPresetCompetences());
        //ToDo: add addable competences
        cvTemplate.setCompetences(temp);
        cvTemplate.setLocation(location);
    }
}
