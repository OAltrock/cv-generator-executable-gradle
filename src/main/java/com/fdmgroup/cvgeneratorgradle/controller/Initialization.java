package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.util.List;
import java.util.function.Predicate;

public interface Initialization {

    @FXML
    default void initialize(BorderPane main, String resource) {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource(resource+".fxml"));
        try {
            main.setCenter(fxmlLoader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default Boolean addValidation(Parent node) {
        Predicate<String> checkNoLetters = input -> input.matches("[a-zA-Z]+");

        List<Node> textFields = node.getChildrenUnmodifiable().stream().filter(child -> child.getClass().toString().contains("TextField")).toList();
        List<Node> filteredTextFields =  textFields.stream().filter(textField -> !checkNoLetters.test(((TextField)textField).getText())).toList();
        return filteredTextFields.isEmpty();
    }

}
