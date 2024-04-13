package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PersonalInformationController implements InitializableFXML {
    @Override
    public void initialize(BorderPane main, String resource) {
        InitializableFXML.super.initialize(main, resource);
        VBox center = (VBox) main.getCenter();


    }
}
