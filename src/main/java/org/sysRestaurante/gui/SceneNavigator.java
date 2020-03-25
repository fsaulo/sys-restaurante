package org.sysRestaurante.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class SceneNavigator {

    public static final String MAIN = "fxml/MainGUI.fxml";
    public static final String LOGIN = "fxml/Login.fxml";
    public static final String MENU_TOOL_BAR = "fxml/ToolBar.fxml";
    public static final String DASHBOARD = "fxml/Dashboard.fxml";
    public static final String APPLICATION_STAGE = "fxml/App.fxml";
    public static final String NOTE_PANE = "fxml/AddNotesDialog.fxml";
    public static final String CASHIER = "fxml/Cashier.fxml";
    public static final String CLOSE_CASHIER_DIALOG = "fxml/CloseCashierDialog.fxml";
    public static final String OPEN_CASHIER_DIALOG = "fxml/OpenCashierDialog.fxml";
    public static final String NEW_COMANDA_DIALOG = "fxml/NewComandaDialog.fxml";
    public static final String CASHIER_POS = "fxml/CashierPOS.fxml";
    public static final String COMANDA_POS = "fxml/ComandaPOS.fxml";
    public static final String PRODUCT_LIST_CELL = "fxml/ProductListCell.fxml";
    public static final String TABLE_LIST_CELL = "fxml/TableListCell.fxml";
    public static final String FINISH_SELL_DIALOG = "fxml/FinishSell.fxml";
    public static final String RECEIPT_VIEW = "fxml/ReceiptView.fxml";
    public static final String MANAGE_COMANDA = "fxml/ManageComanda.fxml";
    public static final String COMANDA_VIEW = "fxml/ComandaView.fxml";
    public static final String ADD_PRODUCTS_TO_COMANDA = "fxml/AddProductsToComanda.fxml";
    public static final KeyCombination ESC_CLOSE = new KeyCodeCombination(KeyCode.ESCAPE);
    public static final KeyCombination F2_CONFIRMATION = new KeyCodeCombination(KeyCode.F2);
    public static final KeyCombination F3_SEARCH = new KeyCodeCombination(KeyCode.F3);
    public static final KeyCombination F4_CANCEL = new KeyCodeCombination(KeyCode.F4);
    public static final KeyCombination F10_OPEN_OR_CLOSE_CASHIER = new KeyCodeCombination(KeyCode.F10);
    public static final KeyCombination F11_FULLSCREEN_MODE = new KeyCodeCombination(KeyCode.F11);

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(MainGUIController.class.getName());

    public static MainGUIController mainController;

    public static void setMainGUIController(MainGUIController mainController) {
        SceneNavigator.mainController = mainController;
    }

    public static void loadScene(String fxml) {
        try {
            mainController.setScene(FXMLLoader.load(SceneNavigator.class.getResource(fxml)));
        } catch (IOException e) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Scene couldn't be loaded.");
            e.printStackTrace();
        }
    }

    public static void loadScene(Node node) {
        mainController.setScene(node);
    }
}
