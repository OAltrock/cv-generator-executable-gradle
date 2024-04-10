package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

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

}
