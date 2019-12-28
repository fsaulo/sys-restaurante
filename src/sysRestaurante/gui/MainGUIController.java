package sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainGUIController {

    @FXML
    private StackPane sceneHolder;

    public void setScene(Node node) {
        sceneHolder.getChildren().setAll(node);
    }

    public void setMainPanePadding(int top, int right, int bottom, int left) {
        sceneHolder.setPadding(new Insets(top, right, bottom, left));
    }
}
