package com.fdmgroup.cvgeneratorgradle.views;

import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class FDMPage {

    public ScrollPane createCenterPage(Pane centerBox) {
        FDMScrollPaneWrapper center = new FDMScrollPaneWrapper(centerBox);
        if (centerBox instanceof FDMCenterVBoxWrapper) ((FDMCenterVBoxWrapper) centerBox).setDesign();
        center.setDesign();
        return center;
    }
}
