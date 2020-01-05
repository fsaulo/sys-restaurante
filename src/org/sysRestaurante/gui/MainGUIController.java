package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class MainGUIController {

    @FXML
    private StackPane sceneHolder;

    private MainGUIController mainGUIController;

    public void setScene(Node node) {
        sceneHolder.getChildren().setAll(node);
    }

    public Scene getScene() {
        return sceneHolder.getScene();
    }

    public void setMainGUIController() {
        mainGUIController = this;
    }

    public MainGUIController getMainGUIController() {
        return mainGUIController;
    }

    public void setMainPanePadding(int top, int right, int bottom, int left) {
        sceneHolder.setPadding(new Insets(top, right, bottom, left));
    }

    public void setMainPaneSize(int height, int width) {
        sceneHolder.setPrefHeight(height);
        sceneHolder.setPrefWidth(width);
    }
}
