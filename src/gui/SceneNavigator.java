package gui;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class SceneNavigator {

    public static final String MAIN = "mainGUI.fxml";
    public static final String LOGIN = "loginPage.fxml";

    public static MainGUIController mainController;

    public static void setMainGUIController(MainGUIController mainController) {
        SceneNavigator.mainController = mainController;
    }

    public static void loadScene(String fxml) {
        try {
            mainController.setScene(FXMLLoader.load(SceneNavigator.class.getResource(fxml)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
