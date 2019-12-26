package sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainGUIController {

    @FXML
    private StackPane sceneHolder;

    public void setScene(Node node) {
        sceneHolder.getChildren().setAll(node);
    }
}
