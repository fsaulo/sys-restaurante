package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("SysRestaurante");
        primaryStage.setScene(createScene(loadMainPane()));
        primaryStage.setResizable(true);
        primaryStage.setMinHeight(150);
        primaryStage.setMinWidth(350);
        primaryStage.show();
    }

    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane wrapperPane = loader.load(
                getClass().getResourceAsStream(SceneNavigator.MAIN)
        );

        MainGUIController mainController = loader.getController();

        SceneNavigator.setMainGUIController(mainController);
        SceneNavigator.loadScene(SceneNavigator.LOGIN);

        return wrapperPane;
    }

    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
