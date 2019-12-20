package sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class MainGUIController {

    @FXML
    private VBox sceneHolder;

    public void setScene(Node node) {
        sceneHolder.getChildren().setAll(node);
    }
}
