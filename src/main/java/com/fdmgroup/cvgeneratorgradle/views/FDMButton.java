package com.fdmgroup.cvgeneratorgradle.views;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class FDMButton extends Button {

    public void setDesign() {
        setMinWidth(USE_PREF_SIZE);
    }

    public FDMButton() {
    }

    public FDMButton(String s) {
        super(s);
    }

    public FDMButton(String s, Node node) {
        super(s, node);
    }
}
