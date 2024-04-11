package com.fdmgroup.cvgeneratorgradle.controller;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SkillsController implements Initialization {
	@Override
    public void initialize(BorderPane main, String resource) {
        Initialization.super.initialize(main, resource);
        VBox center = (VBox) main.getCenter();
	}
}
