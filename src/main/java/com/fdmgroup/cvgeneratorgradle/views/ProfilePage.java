package com.fdmgroup.cvgeneratorgradle.views;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import lombok.Getter;


@Getter
public class ProfilePage extends FDMPage{

    private FDMCenterVBoxWrapper centerBox;
    private final javafx.scene.control.Label pageTitle = new Label("Profile");
    private javafx.scene.control.TextArea profile;
    private FDMHBox buttonWrapper;
    private FDMButton prev;
    private FDMButton next;
    private ObservableList<TextInputControl> textfields;

    /*public ProfilePage(ObservableList<TextInputControl> textFields) {
        profile = new javafx.scene.control.TextArea("");
        this.textfields = textFields;


        initialize();

    }*/

    public ProfilePage(String profile, ObservableList<TextInputControl> textFields) {
        if (profile==null) profile = "";
        this.profile = new javafx.scene.control.TextArea(profile);
        this.textfields = textFields;
        initialize();
    }

    private void initialize() {
        next = new FDMButton("Next");
        next.setDesign("primary");
        prev = new FDMButton("Previous");
        prev.setDesign("primary");
        buttonWrapper = new FDMHBox(prev, next);
        buttonWrapper.setDesign();

        profile.setPromptText("See Global FDM CV – Support Guide for detailed guidance on this section");
        profile.setWrapText(true);
        textfields.add(profile);
        centerBox = new FDMCenterVBoxWrapper(pageTitle, profile, buttonWrapper);
    }


}
