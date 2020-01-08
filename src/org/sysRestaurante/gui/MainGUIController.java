package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class MainGUIController {

    @FXML
    private StackPane sceneHolder;

    public void setScene(Node node) {
        sceneHolder.getChildren().setAll(node);
    }

    public Scene getScene() {
        return sceneHolder.getScene();
    }

    public void setMainPanePadding(int top, int right, int bottom, int left) {
        sceneHolder.setPadding(new Insets(top, right, bottom, left));
    }

    public void setPreferredSize(int height, int width) {
        sceneHolder.setMinWidth(width);
        sceneHolder.setPrefHeight(height);
    }
}
