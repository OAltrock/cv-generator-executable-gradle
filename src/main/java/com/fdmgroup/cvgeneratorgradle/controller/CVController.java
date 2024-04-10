package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public abstract class CVController {

    protected final BorderPane parent;
    protected final String resource;

    public CVController(BorderPane parent, String resource) {
        this.parent = parent;
        this.resource = resource;
    }

}
