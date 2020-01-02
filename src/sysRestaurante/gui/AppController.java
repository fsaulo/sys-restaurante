package sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AppController {
    @FXML
    private BorderPane borderPaneHolder;

    public void initialize() throws IOException {
        borderPaneHolder.leftProperty().setValue(
                FXMLLoader.load(AppController.class.getResource(SceneNavigator.MENU_TOOL_BAR)));
        borderPaneHolder.centerProperty().setValue(
                FXMLLoader.load(AppController.class.getResource(SceneNavigator.DASHBOARD)));
        SceneNavigator.loadScene(borderPaneHolder);
        borderPaneHolder.setAlignment(borderPaneHolder.getCenter(), Pos.CENTER);
    }
}
