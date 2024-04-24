package com.fdmgroup.cvgeneratorgradle.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class FDMCenterVBoxWrapper extends VBox {

    public void setDesign(){
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(20));
        setSpacing(20);
        setFillWidth(true);
        setMinWidth(450);
    }


    public FDMCenterVBoxWrapper() {
    }

    public FDMCenterVBoxWrapper(double v) {
        super(v);
    }

    public FDMCenterVBoxWrapper(Node... nodes) {
        super(nodes);
    }

    public FDMCenterVBoxWrapper(double v, Node... nodes) {
        super(v, nodes);
    }
}
