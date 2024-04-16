package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Predicate;
import static com.fdmgroup.cvgeneratorgradle.controller.SceneSearchUtil.findAllTextFields;

public class ProfileController implements InitializableFXML, HasToggleableSaveButtons, HasAddableTextFields {

    private ObservableList<TextInputControl> textAreas;

    @Override
    public void initialize(BorderPane main, String resource) {
        InitializableFXML.super.initialize(main, resource);
        Label label = new Label("Profile");
        Button saveBtn = new Button("Save");
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.maxWidth(100);
        textArea.setPromptText("See Global FDM CV – Support Guide for detailed guidance on this section");

        VBox centerBox = new VBox(label, textArea,saveBtn);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setFillWidth(false);
        centerBox.setPadding(new Insets(20));
        centerBox.setSpacing(20);
        main.setCenter(centerBox);
        textAreas = FXCollections.observableArrayList();

        Predicate<String> atLeast50Chars = (string -> string.length()>=50 && string.matches("^.*\\w+.*$"));

        addValidationToSaveButtons(textAreas,atLeast50Chars,saveBtn);


        textAreas.addAll(findAllTextFields(centerBox));
        createValidationForTextFields(atLeast50Chars.negate(), textAreas, "Write at least 50 letters.");


    }
}
