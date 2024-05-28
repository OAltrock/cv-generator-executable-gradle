package com.fdmgroup.cvgeneratorgradle.views;

import com.fdmgroup.cvgeneratorgradle.models.Location;
import com.fdmgroup.cvgeneratorgradle.models.Stream;
import com.fdmgroup.cvgeneratorgradle.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import lombok.Getter;

@Getter
public class PersonalInfoPage extends FDMPage{
    private FDMCenterVBoxWrapper centerBox;

    private Label pageTitle;
    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private Label streamLabel;

    private ChoiceBox<String> streamChooser;
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

        locationLabel = new Label("Choose your location");
        locationChooser = new ChoiceBox<>(FXCollections.observableArrayList("Germany", "International"));
        locationChooser.setValue("");

        prevBtn = new FDMButton("Previous");
        nextBtn = new FDMButton("Next");
        prevBtn.setDesign("primary");
        nextBtn.setDesign("primary");
        buttonWrapper = new FDMHBox(prevBtn,nextBtn);
        buttonWrapper.setDesign();
        centerBox = new FDMCenterVBoxWrapper(pageTitle, firstName,lastName, email,
                streamLabel,streamChooser,locationLabel,locationChooser, buttonWrapper);
    }
}
