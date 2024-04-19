package com.fdmgroup.cvgeneratorgradle.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class FDMDateWrapper extends HBox {

    public void setDesign() {
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        this.minWidth(USE_PREF_SIZE);
    }

    public FDMDateWrapper() {
    }

    public FDMDateWrapper(double v) {
        super(v);
    }

    public FDMDateWrapper(Node... nodes) {
        super(nodes);
    }

    public FDMDateWrapper(double v, Node... nodes) {
        super(v, nodes);
    }
}
