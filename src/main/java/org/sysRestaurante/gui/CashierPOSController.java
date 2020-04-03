package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Product;

public class CashierPOSController extends POS {

    @FXML
    private VBox cancelButton;
    @FXML
    private VBox finalizeSell;
    @FXML
    private VBox detailsWrapperBox;
    @FXML
    private Button removeButton;
    @FXML
    private Button clearButton;
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
    private Label subtotalLabel;
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

    private final ObservableList<ProductDao> selectedProductsList = FXCollections.observableArrayList();
    private final ObservableList<ProductDao> products = new Product().getProducts();

    public void initialize() {
        AppFactory.setPos(this);
        AppFactory.setCashierPOSController(this);
        setStageControls();
        setStageObjects();
        startSearchControls();
    }

    public void setStageControls() {
        setDetailsWrapperBox(detailsWrapperBox);
        setSearchBox(searchBox);
        setAddProductButton(addProductButton);
        setUnitPriceLabel(unitPriceLabel);
        setContentLabel(contentLabel);
        setCodProductLabel(codProductLabel);
        setCategoryLabel(categoryLabel);
        setSubtotalLabel(subtotalLabel);
        setWrapperBox(wrapperBox);
        setProductsListView(productsListView);
        setSelectedProductsTableView(selectedProductsTableView);
        setDescriptionColumn(descriptionColumn);
        setQtdColumn(qtdColumn);
        setPriceColumn(priceColumn);
        setTotalColumn(totalColumn);
        setQtySpinner(qtySpinner);
        setSelectedProductsList(selectedProductsList);
        setProducts(products);
        setRemoveButton(removeButton);
        setClearButton(clearButton);
    }

    public void setStageObjects() {
        updateTables();
        updateTotalCashierLabel();
        updateDetailsBox();

        cancelButton.setOnMouseClicked(event -> onCancelButton());
        finalizeSell.setOnMouseClicked(event -> this.onFinalizeOrder());
    }
}
