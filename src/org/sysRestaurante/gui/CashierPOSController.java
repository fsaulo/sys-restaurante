package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Product;
import org.sysRestaurante.util.CellFormatter;
import org.sysRestaurante.util.CurrencyField;
import org.sysRestaurante.util.SpinnerCellFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Format;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class CashierPOSController {

    @FXML
    public VBox cancelButton;
    @FXML
    public VBox finalizeSell;
    @FXML
    public VBox detailsWrapperBox;
    @FXML
    private VBox removeButton;
    @FXML
    private VBox editButton;
    @FXML
    private VBox clearButton;
    @FXML
    private TextField searchBox;
    @FXML
    private Button searchButton;
    @FXML
    private Button addProductButton;
    @FXML
    private Label unitPriceLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label codProductLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label label;
    @FXML
    private Label totalCashierLabel;
    @FXML
    private HBox editableItems;
    @FXML
    private BorderPane wrapperBox;
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
    @FXML
    private Spinner<Integer> qtySpinner;
    @FXML
    private ImageView barcodeImage;

    private final ObservableList<ProductDao> selectedProductsList = FXCollections.observableArrayList();
    private final ObservableList<ProductDao> products = new Product().getProducts();
    private static final String GENERIC_BARCODE = "resources/images/barcode.png";
    private double total = 0;

    public void initialize() {
        AppFactory.setCashierPOSController(this);

        setBarcodeImage();
        updateTables();
        updateTotalCashierLabel();
        updateControls();
        updateDetailsBox();

        removeButton.setOnMouseClicked(event -> {
            for (ProductDao item : selectedProductsTableView.getSelectionModel().getSelectedItems()) {
                item.setQuantity(0);
            }
            selectedProductsList.removeAll(selectedProductsTableView.getSelectionModel().getSelectedItems());
            updateSelectedList();
        });

        cancelButton.setOnMouseClicked(event -> onCancelButton());

        selectedProductsTableView.focusedProperty().addListener((observable) -> {
            updateControls();
            refreshDetailsBoxSelectable(false);
        });

        qtdColumn.setOnEditCommit(editableItems -> {
            if (!editableItems.getTableView().getSelectionModel().isEmpty()) {
                ProductDao product = editableItems.getTableView().getSelectionModel().getSelectedItem();
                product.setQuantity(editableItems.getNewValue());
                product.setTotal(product.getSellPrice());
                updateSelectedList();
            }
        });

        searchBox.textProperty().addListener((observable -> refreshProductsList()));
        searchBox.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                productsListView.getSelectionModel().selectFirst();
                addToSelectedProductsList(productsListView.getSelectionModel().getSelectedItem());
            }
        });


        addProductButton.setOnMouseClicked(event -> {
            addToSelectedProductsList(productsListView.getSelectionModel().getSelectedItem(), qtySpinner.getValue());
            qtySpinner.decrement(qtySpinner.getValue() - 1);
        });

        Platform.runLater(this::handleKeyEvent);
        searchButton.setOnMouseClicked(event -> refreshProductsList());
        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        finalizeSell.setOnMouseClicked(event -> onFinalizeOrder());
        clearButton.setOnMouseClicked(event -> clearShoppingBasket());
        selectedProductsTableView.setOnMouseClicked((e) -> { updateControls(); refreshDetailsBoxSelectable(false); });
        productsListView.focusedProperty().addListener((observable) -> refreshDetailsBoxSelectable(true));
        productsListView.setOnMouseClicked((e) -> refreshDetailsBoxSelectable(true));
    }

    public void refreshProductsList() {
        FilteredList<ProductDao> filteredData = new FilteredList<>(products, null);
        String filter = searchBox.getText().toUpperCase();

        if(filter == null || filter.length() == 0) {
            filteredData.setPredicate(null);
        }
        else {
            filteredData.setPredicate(s ->
                    s.getDescription().toUpperCase().contains(filter) ||
                    s.getCategory().toUpperCase().contains(filter) ||
                    String.valueOf(s.getIdProduct()).contains(filter));
        }

        productsListView.setItems(filteredData);
    }

    public void handleKeyEvent() {
        wrapperBox.getScene().getAccelerators().clear();
        wrapperBox.getScene().getAccelerators().put(SceneNavigator.F2_CONFIRMATION, this::onFinalizeOrder);
        productsListView.requestFocus();
        productsListView.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    ProductDao product = productsListView.getSelectionModel().getSelectedItem();
                    if (product != null) addToSelectedProductsList(product);
                    break;
                case F2:
                    onFinalizeOrder();
                    break;
                case DIGIT1: case NUMPAD1:
                    productsListView.getSelectionModel().select(0);
                    break;
                case DIGIT2: case NUMPAD2:
                    productsListView.getSelectionModel().select(1);
                    break;
                case DIGIT3: case NUMPAD3:
                    productsListView.getSelectionModel().select(2);
                    break;
                case DIGIT4: case NUMPAD4:
                    productsListView.getSelectionModel().select(3);
                    break;
                case DIGIT5: case NUMPAD5:
                    productsListView.getSelectionModel().select(4);
                    break;
                case DIGIT6: case NUMPAD6:
                    productsListView.getSelectionModel().select(5);
                    break;
                case DIGIT7: case NUMPAD7:
                    productsListView.getSelectionModel().select(6);
                    break;
                case DIGIT8: case NUMPAD8:
                    productsListView.getSelectionModel().select(7);
                    break;
                case DIGIT9: case NUMPAD9:
                    productsListView.getSelectionModel().select(8);
                    break;
                case ESCAPE:
                    label.requestFocus();
                    productsListView.getSelectionModel().clearSelection();
                    break;
                default:
                    searchBox.setText(keyEvent.getText());
                    searchBox.requestFocus();
                    break;
            }
            refreshDetailsBoxSelectable(true);
        });

        selectedProductsTableView.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case F2:
                    onFinalizeOrder();
                    break;
                case ESCAPE:
                    label.requestFocus();
                    selectedProductsTableView.getSelectionModel().clearSelection();
                    updateControls();
                    break;
                case DELETE:
                    selectedProductsList.remove(selectedProductsTableView.getSelectionModel().getSelectedItem());
                    break;
                default:
                    searchBox.setText(keyEvent.getText());
                    searchBox.requestFocus();
                    break;
            }
            refreshDetailsBoxSelectable(false);
        });
    }

    public void onFinalizeOrder() {
        ArrayList<ProductDao> products = new ArrayList<>(selectedProductsTableView.getItems());
        AppFactory.setSelectedProducts(products);

        if (selectedProductsTableView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Atenção!");
            alert.setContentText("Não é possível encerrar o pedido pois nenhum ítem foi adicionado a lista");
            alert.showAndWait();
        } else {
            AppController.openFinishSell();
            if (AppFactory.getCashierController().isSellConfirmed()) {
                AppFactory.getSelectedProducts().clear();
                AppFactory.setOrderDao(new OrderDao());
                editableItems.getScene().getWindow().hide();
            }
        }
    }

    public boolean onCancelButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta do sistema");
        alert.setHeaderText("Tem certeza que deseja cancelar venda?");
        alert.setContentText("Todos os registros salvos serão perdidos.");
        alert.showAndWait();

        if (alert.getResult() != ButtonType.CANCEL) {
            cancelButton.getScene().getWindow().hide();
            AppFactory.setOrderDao(new OrderDao());
            AppFactory.getSelectedProducts().clear();
            return true;
        }
        return false;
    }

    public void updateTables() {
        productsListView.setItems(products);
        productsListView.setCellFactory(plv -> new ProductListViewCell());
        selectedProductsTableView.setOnMouseClicked(event -> updateControls());
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        qtdColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        priceColumn.setCellFactory((CellFormatter<ProductDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
        totalColumn.setCellFactory((CellFormatter<ProductDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
        qtdColumn.setCellFactory(tc -> new SpinnerCellFactory(1, 999, 1, 1));
        qtdColumn.setEditable(true);
    }

    private void updateSelectedList() {
        if (selectedProductsTableView.getItems().isEmpty()) {
            selectedProductsTableView.setEditable(false);
        } else selectedProductsTableView.setEditable(true);

        selectedProductsTableView.setItems(selectedProductsList);
        selectedProductsTableView.refresh();
        updateTotalCashierLabel();
    }

    private void updateTotalCashierLabel() {
        total = 0;
        for (ProductDao item : selectedProductsTableView.getItems()) {
            total += item.getTotal();
        }
        totalCashierLabel.setText(CurrencyField.getBRLCurrencyFormat().format(total));
    }

    public void updateControls() {
        boolean empty;

        if (selectedProductsTableView.isFocused()) {
            empty = selectedProductsTableView.getSelectionModel().isEmpty();
        } else empty = true;

        removeButton.setDisable(empty);
        editButton.setDisable(empty);
    }

    public void updateDetailsBox(ProductDao product) {
        detailsWrapperBox.setDisable(false);
        Format format = CurrencyField.getBRLCurrencyFormat();
        unitPriceLabel.setText(format.format(product.getSellPrice()));
        contentLabel.setText(product.getDescription());
        codProductLabel.setText(String.valueOf(product.getIdProduct()));
        categoryLabel.setText(product.getCategory());
        barcodeImage.setVisible(true);
    }

    public void updateDetailsBox() {
        detailsWrapperBox.setDisable(true);
        Format format = CurrencyField.getBRLCurrencyFormat();
        unitPriceLabel.setText(format.format(0));
        contentLabel.setText("Nenhum produto selecionado");
        codProductLabel.setText("");
        categoryLabel.setText("Sem categoria");
        barcodeImage.setVisible(false);
    }

    public void refreshDetailsBoxSelectable(boolean selectable) {
        if (selectable) {
            if (productsListView.getSelectionModel().isEmpty()) {
                updateDetailsBox();
            } else {
                updateDetailsBox(productsListView.getSelectionModel().getSelectedItem());
            }
        } else {
            if (selectedProductsTableView.getSelectionModel().isEmpty()) {
                updateDetailsBox();
            } else {
                updateDetailsBox(selectedProductsTableView.getSelectionModel().getSelectedItem());
            }
        }
        addProductButton.setDisable(!selectable);
        qtySpinner.setDisable(!selectable);
    }

    public void addToSelectedProductsList(ProductDao product) {
        if (selectedProductsList.contains(product)) {
            product.incrementsQuantity();
        } else {
            product.setQuantity(1);
            selectedProductsList.add(product);
        }

        if (selectedProductsTableView.getSelectionModel().isEmpty()) {
            updateControls();
        }

        product.setTotal(product.getSellPrice());
        updateSelectedList();
    }

    public void addToSelectedProductsList(ProductDao product, int qty) {
        if (selectedProductsList.contains(product)) {
            product.setQuantity(product.getQuantity() + qty);
        } else {
            product.setQuantity(qty);
            selectedProductsList.add(product);
        }

        if (selectedProductsTableView.getSelectionModel().isEmpty()) {
            updateControls();
        }

        product.setTotal(product.getSellPrice());
        updateSelectedList();
    }

    public double getTotal() {
        return total;
    }

    public ObservableList<ProductDao> getItems() {
        return selectedProductsTableView.getItems();
    }

    public void setBarcodeImage() {
        try {
            barcodeImage.setImage(new Image(new FileInputStream(GENERIC_BARCODE)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void clearShoppingBasket() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação do sistema");
        alert.setHeaderText("Tem certeza que deseja limpar todos os ítens do carrinho?");
        alert.setContentText("Essa ação não poderá ser desfeita.");
        alert.showAndWait();

        if (alert.getResult().equals(ButtonType.OK)) {
            for (ProductDao item : selectedProductsTableView.getSelectionModel().getSelectedItems()) {
                item.setQuantity(0);
            }
            selectedProductsTableView.getItems().clear();
            selectedProductsList.clear();
        }
    }
}
