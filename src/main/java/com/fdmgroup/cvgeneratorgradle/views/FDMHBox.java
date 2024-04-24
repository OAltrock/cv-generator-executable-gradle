package com.fdmgroup.cvgeneratorgradle.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class FDMHBox extends HBox {

    public void setDesign() {
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        this.minWidth(USE_PREF_SIZE);
    }

    public FDMHBox(Node... nodes) {
        super(nodes);
    }
}
