package com.fdmgroup.cvgeneratorgradle.views;

import com.fdmgroup.cvgeneratorgradle.models.Location;
import com.fdmgroup.cvgeneratorgradle.models.Stream;
import com.fdmgroup.cvgeneratorgradle.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PersonalInfoPage extends FDMPage{
    private FDMCenterVBoxWrapper centerBox;

    private Label pageTitle;
    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private Label streamLabel;

    private ChoiceBox<String> streamChooser;
    private DatePicker streamStart;
    private DatePicker streamEnd;
    private CheckBox ongoing;
    private Label dateLabel;
    private FDMDateWrapper dateWrapper;
    private Label locationLabel;
    private ChoiceBox<String> locationChooser;
    private FDMHBox buttonWrapper;
    private FDMButton nextBtn;
    private FDMButton prevBtn;

    private final User user;
    private final Location location;
    private final Stream stream;

    public PersonalInfoPage(User user, Location location, Stream stream, ObservableList<TextInputControl> textFields) {
        this.user = user;
        this.textFields = textFields;
        this.location = location;
        this.stream = stream;

        initialize();
    }

    private void initialize() {
        pageTitle = new Label("Personal Information");
        firstName = new TextField(user.getFirstName());
        firstName.setPromptText("First Name");
        lastName = new TextField(user.getLastName());
        lastName.setPromptText("Last Name");
        email = new TextField(user.getEmail());
        email.setPromptText("Enter your email");
        streamLabel = new Label("Choose your practise");
        streamChooser = new ChoiceBox<>(FXCollections.observableArrayList("Business","Technical"));
        streamChooser.setValue("");
        dateLabel = new Label("Choose start and end");
        streamStart = (stream.getStartDate() != null && !stream.getStartDate().isEmpty() ) ? new DatePicker(LocalDate.parse(stream.getStartDate()))
                : new DatePicker();
        streamEnd = (stream.getStartDate() != null && !stream.getStartDate().isEmpty()) ?
                (LocalDate.parse(stream.getEndDate()).isAfter(LocalDate.parse(stream.getStartDate()))) ? new DatePicker(LocalDate.parse(stream.getEndDate()))
                        : new DatePicker(LocalDate.now().plusMonths(1))
                : new DatePicker();


        ongoing = new CheckBox("ongoing");
        if (streamEnd.getValue() != null) {
            ongoing.setSelected(streamEnd.getValue().isAfter(LocalDate.now()));
            streamEnd.setDisable(streamEnd.getValue().isAfter(LocalDate.now()) || streamEnd.getValue().isBefore(streamStart.getValue()));
        }
        dateWrapper = new FDMDateWrapper(streamStart, streamEnd, ongoing);
        dateWrapper.setDesign();
        locationLabel = new Label("Choose your location");
        locationChooser = new ChoiceBox<>(FXCollections.observableArrayList("Germany", "International"));
        locationChooser.setValue("");

        prevBtn = new FDMButton("Previous");
        nextBtn = new FDMButton("Next");

        buttonWrapper = new FDMHBox(prevBtn,nextBtn);
        buttonWrapper.setDesign();
        centerBox = new FDMCenterVBoxWrapper(pageTitle, firstName,lastName, email,
                streamLabel,streamChooser,dateLabel, dateWrapper, locationLabel,locationChooser, buttonWrapper);
    }
}
