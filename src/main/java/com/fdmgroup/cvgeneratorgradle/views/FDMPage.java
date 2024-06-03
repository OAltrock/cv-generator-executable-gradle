package com.fdmgroup.cvgeneratorgradle.views;

import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class FDMPage {
    FDMCenterVBoxWrapper centerBox;
    FDMHBox buttonWrapper;
    FDMButton prev;
    FDMButton next;
    ObservableList<TextInputControl> textFields;

    public ScrollPane createCenterPage(Pane centerBox) {
        FDMScrollPaneWrapper center = new FDMScrollPaneWrapper(centerBox);
        if (centerBox instanceof FDMCenterVBoxWrapper) ((FDMCenterVBoxWrapper) centerBox).setDesign();
        center.setDesign();
        return center;
    }


}
