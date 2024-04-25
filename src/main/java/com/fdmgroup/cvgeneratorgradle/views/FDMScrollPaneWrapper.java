package com.fdmgroup.cvgeneratorgradle.views;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class FDMScrollPaneWrapper extends ScrollPane {

    public void setDesign() {
        this.setFitToHeight(true);
        this.setFitToWidth(true);
    }

    public FDMScrollPaneWrapper() {
    }

    public FDMScrollPaneWrapper(Node node) {
        super(node);
    }
}
