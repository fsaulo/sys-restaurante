package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Product;
import org.sysRestaurante.util.NotificationHandler;

import java.util.ArrayList;
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
    private final List<Node> stackNodes = new ArrayList<>();

    public void initialize() {
        priceField = new CurrencyField(new Locale("pt", "BR"), NodeOrientation.LEFT_TO_RIGHT);
        buyPriceField = new CurrencyField(new Locale("pt", "BR"), NodeOrientation.LEFT_TO_RIGHT);

        setPriceFields();
        handleCategoryComboBox();
        addToRequiredFields(productName, priceField);

        Platform.runLater(() -> label.requestFocus());
        trackStock.selectedProperty().addListener(observable -> {
            qtyStock.setDisable(!qtyStock.isDisabled());
            criticStock.setDisable(!criticStock.isDisabled());
            buyPriceField.setDisable(!buyPriceField.isDisabled());
            qtyStock.setStyle("");
            criticStock.setStyle("");

            if (trackStock.isSelected()) {
                addToRequiredFields(qtyStock, criticStock);
            } else {
                removeFromRequiredFields(qtyStock, criticStock);
            }
        });

        menuItem.selectedProperty().addListener(observable -> {
            trackStock.setSelected(false);
            trackStock.setDisable(menuItem.isSelected());
            removeFromRequiredFields(qtyStock, criticStock);
        });

        hidePrice.selectedProperty().addListener(observable -> {
            menuItem.setSelected(false);
            menuItem.setDisable(hidePrice.isSelected());
            trackStock.setSelected(hidePrice.isSelected());
            trackStock.setDisable(hidePrice.isSelected());
            priceField.setDisable(!priceField.isDisabled());

            setDisableStockFields(!trackStock.isSelected());
            if (hidePrice.isSelected()) {
                removeFromRequiredFields(priceField);
                addToRequiredFields(qtyStock, criticStock);
            } else  {
                addToRequiredFields(priceField);
                removeFromRequiredFields(qtyStock, criticStock);
            }
        });

        Tooltip tooltip = new Tooltip("Preço de venda não pode ser R$ 0,00");
        tooltip.setStyle("-fx-font-size: 14");

        priceField.setTooltip(tooltip);
        productName.setOnKeyTyped(keyEvent -> productName.setStyle(""));
        priceField.setOnKeyTyped(keyEvent -> priceField.setStyle(""));
        qtyStock.setOnKeyTyped(keyEvent -> qtyStock.setStyle(""));
        criticStock.setOnKeyTyped(keyEvent -> criticStock.setStyle(""));
        confirmButton.setOnMouseClicked(e -> confirm());
        cancelButton.setOnAction(actionEvent -> label.getParent().getScene().getWindow().hide());

        qtyStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                qtyStock.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        criticStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                criticStock.setText(newValue.replaceAll("[^\\d]", ""));
            }
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

    /**
     * Check if at least one required field is empty.
     * Returns boolean true if it finds it.
     */
    public boolean requiredEmptyField() {
        for (Node node : stackNodes) {
            if (((TextField) node).getText().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public void addToRequiredFields(Node ... nodes) {
        for (Node node : nodes) {
            if (!stackNodes.contains(node)) {
                stackNodes.add(node);
            }
        }
    }

    public void removeFromRequiredFields(Node ... nodes) {
        for (Node node : nodes) {
            stackNodes.remove(node);
        }
    }

    public void alertRequired() {
        for (Node node : stackNodes) {
            if (((TextField) node).getText().isEmpty() || (node instanceof CurrencyField && ((CurrencyField) node).getAmount() == 0.0)) {
                node.setStyle("-fx-border-color: red; -fx-border-radius: 2");
            }
        }
    }

    public void confirm() {
        if (requiredEmptyField()) {
            alertRequired();

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Atenção! Campos obrigatórios não foram preenchidos");
            alert.setContentText("Todos os espaços marcos com (*) devem ser preenchidos.");
            alert.showAndWait();
        } else if (priceField.getAmount() == 0.0 && !hidePrice.isSelected()) {
            alertRequired();

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Atenção! Campo obrigatório preenchido incorretamente");
            alert.setContentText("O preço de revenda do produto não pode ser R$ 0,00");
            alert.showAndWait();
        } else {
            ProductDao productDao = new ProductDao();
            productDao.setDescription(productName.getText());
            productDao.setCategoryDao(categoryComboBox.getSelectionModel().getSelectedItem());
            productDao.setMenuItem(menuItem.isSelected());
            productDao.setSellPrice(priceField.getAmount());
            productDao.setIngredient(hidePrice.isSelected());
            productDao.setTrackStock(trackStock.isSelected());

            if (trackStock.isSelected()) {
                productDao.setSupply(Integer.parseInt(qtyStock.getText()));
                productDao.setMinSupply(Integer.parseInt(criticStock.getText()));
                productDao.setBuyPrice(buyPriceField.getAmount());
            }

            try {
                Product.insert(productDao);
                label.getParent().getScene().getWindow().hide();

                AppFactory.getProductManagementController().reload();
                NotificationHandler.showInfo("Produto inserido com sucesso!");
            } catch (Exception ex) {
                NotificationHandler.errorDialog(ex);
            }
        }
    }
}
