package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Product;

import java.util.List;
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
    @FXML
    private ComboBox<ProductDao.CategoryDao> categoryComboBox;

    private CurrencyField priceField;
    private CurrencyField buyPriceField;

    public void initialize() {
        priceField = new CurrencyField(new Locale("pt", "BR"), NodeOrientation.LEFT_TO_RIGHT);
        buyPriceField = new CurrencyField(new Locale("pt", "BR"), NodeOrientation.LEFT_TO_RIGHT);

        setPriceFields();
        handleCategoryComboBox();

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

    public void handleCategoryComboBox() {
        List<ProductDao.CategoryDao> categories = Product.getProductCategory();
        Callback<ListView<ProductDao.CategoryDao>, ListCell<ProductDao.CategoryDao>> cellFactory = new Callback<>() {
            @Override
            public ListCell<ProductDao.CategoryDao> call(ListView<ProductDao.CategoryDao> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ProductDao.CategoryDao item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getDescription());
                        }
                    }
                };
            }
        };

        categoryComboBox.setButtonCell(cellFactory.call(null));
        categoryComboBox.setCellFactory(cellFactory);

        assert categories != null;
        for (ProductDao.CategoryDao category : categories) {
            categoryComboBox.getItems().add(category);
        }

        categoryComboBox.getSelectionModel().select(categories.stream()
                .filter(e -> e.getIdCategory() == 5)
                .findFirst()
                .orElse(new ProductDao.CategoryDao()));
    }
}
