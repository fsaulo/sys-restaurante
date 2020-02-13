package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Product;

public class ComandaPOSController extends POSController {

    @FXML
    public VBox confirm;
    @FXML
    public VBox cancel;
    @FXML
    public VBox finalizeComanda;
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
    private Label codComanda;
    @FXML
    private Label codTable;
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

    private ObservableList<ProductDao> selectedProductsList = FXCollections.observableArrayList();
    private ObservableList<ProductDao> products = new Product().getProducts();
    private ComandaDao comanda = AppFactory.getComandaDao();
    private static boolean confirmed;

    public void initialize() {
        AppFactory.setPosController(this);
        AppFactory.setComandaPOSController(this);
        setStageControls();
        setStageObjects();
    }

    public void setStageControls() {
        setDetailsWrapperBox(detailsWrapperBox);
        setRemoveButton(removeButton);
        setEditButton(editButton);
        setSearchBox(searchBox);
        setAddProductButton(addProductButton);
        setUnitPriceLabel(unitPriceLabel);
        setContentLabel(contentLabel);
        setCodProductLabel(codProductLabel);
        setCategoryLabel(categoryLabel);
        setLabel(label);
        setTotalCashierLabel(totalCashierLabel);
        setEditableItems(editableItems);
        setWrapperBox(wrapperBox);
        setProductsListView(productsListView);
        setSelectedProductsTableView(selectedProductsTableView);
        setDescriptionColumn(descriptionColumn);
        setQtdColumn(qtdColumn);
        setPriceColumn(priceColumn);
        setTotalColumn(totalColumn);
        setQtySpinner(qtySpinner);
        setBarcodeImage(barcodeImage);
        setSelectedProductsList(selectedProductsList);
        setProducts(products);
    }

    public void setStageObjects() {
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
            if (event.getCode().equals(KeyCode.ENTER) && !productsListView.getItems().isEmpty()) {
                productsListView.getSelectionModel().selectFirst();
                addToSelectedProductsList(productsListView.getSelectionModel().getSelectedItem());
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                label.requestFocus();
            }
        });


        addProductButton.setOnAction(event -> {
            if (!productsListView.getItems().isEmpty()) {
                addToSelectedProductsList(productsListView.getSelectionModel().getSelectedItem(), qtySpinner.getValue());
                qtySpinner.decrement(qtySpinner.getValue() - 1);
            }
        });

        Platform.runLater(() -> {
            handleKeyEvent();
            handleSpecificKeyEvent();
            wrapperBox.getScene().getWindow().setOnCloseRequest(windowEvent -> addProductsToComanda());
        });

        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        clearButton.setOnMouseClicked(event -> clearShoppingBasket());
        selectedProductsTableView.setOnMouseClicked((e) -> {
            updateControls();
            refreshDetailsBoxSelectable(false);
        });
        productsListView.focusedProperty().addListener((observable) -> refreshDetailsBoxSelectable(true));
        productsListView.setOnMouseClicked((e) -> refreshDetailsBoxSelectable(true));
        codComanda.setText("#" + comanda.getIdComanda());
        codTable.setText("NOVO PEDIDO NA MESA " + comanda.getIdTable());
        finalizeComanda.setOnMouseClicked(event -> onFinalizeOrder());
        confirm.setOnMouseClicked(event -> {
            addProductsToComanda();
            wrapperBox.getScene().getWindow().hide();
        });
    }

    @Override
    public void onFinalizeOrder() {
        if (selectedProductsTableView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Atenção!");
            alert.setContentText("Não é possível encerrar o pedido pois nenhum item foi adicionado a lista");
            alert.showAndWait();
        } else {
            AppFactory.setOrderDao(comanda);
            AppController.openFinishSell();
            if (isSellConfirmed()) {
                AppFactory.getManageComandaController().refreshTileList();
                close();
            }
        }
    }

    public void addProductsToComanda() {
        new Order().addProductsToOrder(comanda.getIdOrder(), selectedProductsList);
    }

    public void handleSpecificKeyEvent() {
        wrapperBox.getScene().getAccelerators().put(SceneNavigator.ESC_CLOSE, this::close);
    }

    private void close() {
        wrapperBox.getScene().getWindow().hide();
    }

    public static void setSellConfirmed(boolean conf) {
        confirmed = conf;
    }

    public static boolean isSellConfirmed() {
        return confirmed;
    }
}
