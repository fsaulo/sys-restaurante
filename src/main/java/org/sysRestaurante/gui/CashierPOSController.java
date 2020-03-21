package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Product;

public class CashierPOSController extends POSController {

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

    public void initialize() {
        AppFactory.setPosController(this);
        AppFactory.setCashierPOSController(this);
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

        Platform.runLater( () -> {
            wrapperBox.getScene().getWindow().setOnCloseRequest(event -> { if (!onCancelButton()) event.consume(); });
            handleKeyEvent();
        });

        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        finalizeSell.setOnMouseClicked(event -> this.onFinalizeOrder());
        clearButton.setOnMouseClicked(event -> clearShoppingBasket());
        selectedProductsTableView.setOnMouseClicked((e) -> { updateControls(); refreshDetailsBoxSelectable(false); });
        productsListView.focusedProperty().addListener((observable) -> refreshDetailsBoxSelectable(true));
        productsListView.setOnMouseClicked((e) -> refreshDetailsBoxSelectable(true));
    }
}
