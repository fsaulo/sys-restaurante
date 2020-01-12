package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.StackPane;
import org.sysRestaurante.applet.AppFactory;

public class MainGUIController {

    @FXML
    private StackPane sceneHolder;

    public void initialize() {
        AppFactory.setMainController(this);
    }

    public Scene getScene() {
        return sceneHolder.getScene();
    }

    public void darkenScreen() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.5);
        sceneHolder.setEffect(colorAdjust);
    }

    public void brightenScreen() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        sceneHolder.setEffect(colorAdjust);
    }

    public void setScene(Node node) {
        sceneHolder.getChildren().setAll(node);
    }

    public void setMainPanePadding(int top, int right, int bottom, int left) {
        sceneHolder.setPadding(new Insets(top, right, bottom, left));
    }
}
