package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.models.PersonalInfoModel;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.io.IOException;

public class PersonalInfoController implements InitializableFXML {
    public TextField firstNameField;
    public TextField lastNameField;
    public TextField emailField;
    public ChoiceBox locationChoiceBox;
    public ChoiceBox streamChoiceBox;

    @Override
    public void initialize(BorderPane main, String resource) {
        InitializableFXML.super.initialize(main, resource);
        ScrollPane scrollPane = (ScrollPane) main.getCenter();
        VBox centerBox = (VBox) scrollPane.getContent();
    }
    @FXML
    private void handleSaveButton() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String location = (String) locationChoiceBox.getValue();
        String stream = (String) streamChoiceBox.getValue();

        PersonalInfoModel personalInfoModel = new PersonalInfoModel();

        personalInfoModel.setFirstName(firstName);
        personalInfoModel.setLastName(lastName);
        personalInfoModel.setEmail(email);
        personalInfoModel.setLocation(location);
        personalInfoModel.setStream(stream);

        savePersonalInfoToFile(personalInfoModel);
    }

    private void savePersonalInfoToFile(PersonalInfoModel personalInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(personalInfo);

        String filePath = "C:\\Users\\Adria\\OneDrive\\Desktop\\personalInfo.json";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
