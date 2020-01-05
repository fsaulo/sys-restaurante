package org.sysRestaurante.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.Encryption;

import java.io.IOException;
import java.util.logging.Logger;

public class MainGUI extends Application {

    private static final Logger LOGGER = new LoggerHandler().getGenericConsoleHandler(MainGUI.class.getName());
    private static MainGUIController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        startProgram(primaryStage);
        Encryption.setKey("Jaguaric@3105");
        LOGGER.info("Program started with " + ExceptionHandler.getGlobalExceptionsCount() + " errors.");
    }

    private static Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane wrapperPane = loader.load(MainGUI.class.getResourceAsStream(SceneNavigator.MAIN));

        mainController = loader.getController();

        LOGGER.info("Wrapper pane successfully loaded.");

        mainController.setMainPanePadding(300, 120, 300, 120);
        SceneNavigator.setMainGUIController(mainController);
        SceneNavigator.loadScene(SceneNavigator.LOGIN);

        return wrapperPane;
    }

    public static void clear() {
        Stage stage = (Stage) mainController.getScene().getWindow();
        stage.close();
    }

    public static void startProgram(Stage stage) throws IOException {
        stage.setTitle("SysRestaurante");
        stage.setScene(createScene(loadMainPane()));
        stage.setMinHeight(390);
        stage.setMinWidth(465);
        stage.show();

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
            LOGGER.info("Program ended.");
        });
    }

    public static void restartProgram() throws IOException {
        clear();
        startProgram(new Stage());
    }

    public static MainGUIController getMainController() {
        return mainController;
    }

    private static Scene createScene(Pane mainPane) {
        return new Scene(mainPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
