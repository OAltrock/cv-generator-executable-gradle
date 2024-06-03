package com.fdmgroup.cvgeneratorgradle.views;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import lombok.Getter;


@Getter
public class ProfilePage extends FDMPage{

    private final javafx.scene.control.Label pageTitle = new Label("Profile");
    private final javafx.scene.control.TextArea profile;



    public ProfilePage(String profile, ObservableList<TextInputControl> textFields) {
        if (profile==null) profile = "";
        this.profile = new javafx.scene.control.TextArea(profile);
        this.textFields = textFields;
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
        textFields.add(profile);
        centerBox = new FDMCenterVBoxWrapper(pageTitle, profile, buttonWrapper);
    }


}
