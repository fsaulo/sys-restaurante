package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.sysRestaurante.gui.formatter.CurrencyField;

import java.util.Locale;

public class ProductFormDialogController {

    @FXML
    private TextField productName;
    @FXML
    private TextField decoy1;
    @FXML
    private TextField decoy2;
    @FXML
    private CheckBox hidePrice;
    @FXML
    private CheckBox trackStock;
    @FXML
    private TextField qtyStock;
    @FXML
    private TextField criticStock;
    @FXML
    private CheckBox menuItem;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label label;
    @FXML
    private HBox priceBox;
    @FXML
    private HBox buyPriceBox;

    private CurrencyField priceField;
    private CurrencyField buyPriceField;

    public void initialize() {
        priceField = new CurrencyField(new Locale("pt", "BR"), NodeOrientation.LEFT_TO_RIGHT);
        buyPriceField = new CurrencyField(new Locale("pt", "BR"), NodeOrientation.LEFT_TO_RIGHT);

        setPriceFields();

        Platform.runLater(() -> label.requestFocus());
        trackStock.selectedProperty().addListener(observable -> {
            qtyStock.setDisable(!qtyStock.isDisabled());
            criticStock.setDisable(!criticStock.isDisabled());
            buyPriceField.setDisable(!buyPriceField.isDisabled());
        });

        menuItem.selectedProperty().addListener(observable -> {
            trackStock.setSelected(false);
            trackStock.setDisable(menuItem.isSelected());
        });

        hidePrice.selectedProperty().addListener(observable -> {
            menuItem.setSelected(false);
            menuItem.setDisable(hidePrice.isSelected());
            trackStock.setSelected(hidePrice.isSelected());
            trackStock.setDisable(hidePrice.isSelected());
            priceField.setDisable(!priceField.isDisabled());

            setDisableStockFields(!trackStock.isSelected());
        });

    }

    public void setDisableStockFields(boolean disable) {
        qtyStock.setDisable(disable);
        criticStock.setDisable(disable);
        buyPriceField.setDisable(disable);
    }

    public void setPriceFields() {
        priceField.setMinHeight(35);
        buyPriceField.setMinHeight(35);
        priceBox.getChildren().remove(decoy1);
        priceBox.getChildren().add(0, priceField);
        buyPriceBox.getChildren().remove(decoy2);
        buyPriceBox.getChildren().add(0, buyPriceField);
        buyPriceField.setDisable(true);

        HBox.setHgrow(priceField, Priority.ALWAYS);
        HBox.setHgrow(buyPriceField, Priority.ALWAYS);
    }
}
