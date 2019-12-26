package sysRestaurante.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import sysRestaurante.util.Encryption;
import sysRestaurante.util.ExceptionHandler;
import sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class MainGUI extends Application {

    private static final Logger LOGGER = new LoggerHandler().getGenericConsoleHandler(MainGUI.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("SysRestaurante");
        primaryStage.setScene(createScene(loadMainPane()));
        primaryStage.setMinWidth(480);
        primaryStage.setMinHeight(380);
        Encryption.setKey("Jaguaric@3105");
        primaryStage.show();

        LOGGER.info("Program started with " + ExceptionHandler.getGlobalExceptionsCount() + " errors.");
    }

    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane wrapperPane = loader.load(getClass().getResourceAsStream(SceneNavigator.MAIN));

        MainGUIController mainController = loader.getController();
        LOGGER.info("Wrapper pane successfully loaded.");

        SceneNavigator.setMainGUIController(mainController);
        SceneNavigator.loadScene(SceneNavigator.LOGIN);

        return wrapperPane;
    }

    private Scene createScene(Pane mainPane) {
        return new Scene(mainPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
