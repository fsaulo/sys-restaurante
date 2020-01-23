package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Product;
import org.sysRestaurante.util.CellFormatter;
import org.sysRestaurante.util.CurrencyField;

public class CashierPOSController {

    @FXML
    public VBox cancelButton;
    @FXML
    public VBox finalizeSell;
    @FXML
    private VBox removeButton;
    @FXML
    private Label label;
    @FXML
    private Label totalCashierLabel;
    @FXML
    private HBox editableItems;
    @FXML
    private ListView<ProductDao> productsListView;
    @FXML
    private TableView<ProductDao> selectedProductsTableView;
    @FXML
    private TableColumn<ProductDao, String> descriptionColumn;
    @FXML
    private TableColumn<ProductDao, Integer> qtdColumn;
    @FXML
    private TableColumn<ProductDao, Double> priceColumn;
    @FXML
    private TableColumn<ProductDao, Double> totalColumn;

    private final ObservableList<ProductDao> selectedProductsList = FXCollections.observableArrayList();
    private double total = 0;
    private boolean confirmed = false;

    public void initialize() {
        AppFactory.setCashierPOSController(this);

        updateTables();
        updateTotalCashierLabel();

        productsListView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ProductDao product = productsListView.getSelectionModel().getSelectedItem();
                addToSelectedProductsList(product);
            }
        });

        removeButton.setOnMouseClicked(event -> {
            for (ProductDao item : selectedProductsTableView.getSelectionModel().getSelectedItems()) {
                item.setQuantity(1);
            }
            selectedProductsList.removeAll(selectedProductsTableView.getSelectionModel().getSelectedItems());
            updateSelectedList();
        });

        cancelButton.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Tem certeza que deseja cancelar venda?");
            alert.setContentText("Todos os registros salvos serão perdidos.");
            alert.showAndWait();
            if (alert.getResult() != ButtonType.CANCEL)
                ((Node) e.getSource()).getScene().getWindow().hide();
        });

        Platform.runLater(() -> {
            label.requestFocus();
            if (selectedProductsTableView.getSelectionModel().isEmpty())
                editableItems.setDisable(true);
        });
    }

    @FXML
    public void onFinalizeOrder(Event event) {
        if (selectedProductsTableView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Atenção!");
            alert.setContentText("Não é possível encerrar o pedido pois nenhum ítem foi adicionado a lista");
            alert.showAndWait();
        } else {
            AppController.openFinishSell();
            if (AppFactory.getCashierController().isSellConfirmed())
                editableItems.getScene().getWindow().hide();
        }
        event.consume();
    }

    public void updateTables() {
        productsListView.setItems(new Product().getProducts());
        productsListView.setCellFactory(productsListView -> new ProductListViewCell());
        selectedProductsTableView.setOnMouseClicked(event -> editableItems.setDisable(false));

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        qtdColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        priceColumn.setCellFactory((CellFormatter<ProductDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
        totalColumn.setCellFactory((CellFormatter<ProductDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
    }

    public void addToSelectedProductsList(ProductDao product) {
        if (selectedProductsList.contains(product)) {
            product.incrementsQuantity();
        } else {
            selectedProductsList.add(product);
        }

        if (selectedProductsTableView.getSelectionModel().isEmpty()) {
            editableItems.setDisable(true);
        }
        product.setTotal(product.getSellPrice());
        updateSelectedList();
    }

    private void updateSelectedList() {
        selectedProductsTableView.setItems(selectedProductsList);
        selectedProductsTableView.refresh();
        if (selectedProductsTableView.getSelectionModel().isEmpty())
            editableItems.setDisable(true);
        updateTotalCashierLabel();
    }

    private void updateTotalCashierLabel() {
        total = 0;
        for (ProductDao item : selectedProductsTableView.getItems()) {
            total += item.getTotal();
        }
        totalCashierLabel.setText(CurrencyField.getBRLCurrencyFormat().format(total));
    }

    public double getTotal() {
        return total;
    }

    public ObservableList<ProductDao> getItems() {
        return selectedProductsTableView.getItems();
    }
}
