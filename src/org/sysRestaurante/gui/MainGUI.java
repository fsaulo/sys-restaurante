package org.sysRestaurante.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.model.Authentication;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Receipt;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.Encryption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MainGUI extends Application {

    private static final Logger LOGGER = new LoggerHandler().getGenericConsoleHandler(MainGUI.class.getName());
    private static MainGUIController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
//        testReceipt();
        primaryStage.setHeight(550);
        primaryStage.setWidth(800);
        startProgram(primaryStage);
        Encryption.setKey("Jaguaric@3105");
        LOGGER.info("Program started with " + ExceptionHandler.getGlobalExceptionsCount() + " errors.");
    }

    private static Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane wrapperPane = loader.load(MainGUI.class.getResourceAsStream(SceneNavigator.MAIN));

        mainController = loader.getController();
        LOGGER.info("Wrapper pane successfully loaded.");
        mainController.setMainPanePadding(300, 70, 300, 70);
        wrapperPane.setOnMouseClicked(e -> wrapperPane.requestFocus());

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
            if (AppFactory.getUserDao() != null) {
                AppFactory.getLoginController().storeLastSessionDuration();
            }
            LOGGER.info(DBConnection.getGlobalDBRequestsCount() + " requests to database.");
            Platform.exit();
            LOGGER.info("Program exited.");
            System.exit(0);
        });
    }

    public static void restartProgram() throws IOException {
        clear();
        startProgram(new Stage());
    }

    public void testReceipt() {
        UserDao userDao = new Authentication().getUserData("fsaulo");
        OrderDao orderDao = new Cashier().getOrderById(299);
        ProductDao product1 = new ProductDao();
        product1.setDescription("Coca-cola 1L Retornável");
        product1.setQuantity(2);
        product1.setSellPrice(5.50);
        ProductDao product2 = new ProductDao();
        product2.setDescription("Cerveja SKOL Litrão");
        product2.setQuantity(5);
        product2.setSellPrice(10.50);
        ProductDao product3 = new ProductDao();
        product3.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        product3.setQuantity(5);
        product3.setSellPrice(10.50);
        ArrayList<ProductDao> prs = new ArrayList<>();
        prs.add(product1);
        prs.add(product2);
        prs.add(product3);
        Receipt rp = new Receipt(prs, userDao, orderDao);
        System.out.println(rp.getReceipt());
        Platform.exit();
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
