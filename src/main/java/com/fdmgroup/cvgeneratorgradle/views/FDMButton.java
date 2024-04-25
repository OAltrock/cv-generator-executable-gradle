package com.fdmgroup.cvgeneratorgradle.views;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class FDMButton extends Button {

    public void setDesign() {
        setMinWidth(USE_PREF_SIZE);
    }
    
    public void setDesign(String style) {
        configure(style);
    }

    public FDMButton() {
    }

    public FDMButton(String s) {
        super(s);
    }

    public FDMButton(String s, Node node) {
        super(s, node);
    }
    
    
    
    // configures style according to style 
    private void configure(String style) {
    	
        // Set specific style based on the input parameter
        if (style.equals("primary")) {
        	this.setStyle("-fx-padding: 10px;" +
		                    "-fx-text-align: center;" +
		                    "-fx-font-size: 16px;" +
		                    "-fx-cursor: hand;" +
		                    "-fx-background-radius: 2vh;" + 
		                    "-fx-background-color: #C5FF00;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: transparent;");
        } else if (style.equals("secondary")) {
        	this.setStyle("-fx-padding: 10px;" +
		                    "-fx-text-align: center;" +
		                    "-fx-font-size: 16px;" +
		                    "-fx-cursor: hand;" +
		                    "-fx-background-radius: 2vh;" + 
		                    "-fx-background-color: #E9EDF6;" +
                            "-fx-text-fill: black;" +
                            "-fx-border-color: transparent;");
        } else if (style.equals("tertiary")) {
        	this.setStyle("-fx-padding: 10px;" +
		                    "-fx-text-align: center;" +
		                    "-fx-font-size: 16px;" +
		                    "-fx-cursor: hand;" +
		                    "-fx-background-radius: 2vh;" + 
		                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: white;" +
                            "-fx-border-color: #C5FF00;" +
                            "-fx-border-width: 1px;");
        }
    }
}
