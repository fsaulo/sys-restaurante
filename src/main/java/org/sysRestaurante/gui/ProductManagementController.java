package org.sysRestaurante.gui;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Product;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.SQLException;
import java.text.Format;
import java.util.Objects;
import java.util.logging.Logger;

public class ProductManagementController {

    @FXML
    private VBox addProductButton;
    @FXML
    private VBox detailsWrapperBox;
    @FXML
    private VBox wrapperVBox;
    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private ListView<ProductDao> productsListView;
    @FXML
    private TextField searchBox;
    @FXML
    private Label unitPriceLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label codProductLabel;
    @FXML
    private Label categoryLabel;

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(ProductManagementController.class.getName());
    private static ObservableList<ProductDao> products;

    public void initialize() {
        AppFactory.setProductManagementController(this);
        getProductsFromDatabase();
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        productsListView.focusedProperty().addListener((observable) -> refreshDetailsBoxSelectable());
        productsListView.setOnMouseClicked(event -> refreshDetailsBoxSelectable());
        productsListView.requestFocus();
        productsListView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                wrapperVBox.requestFocus();
                productsListView.getSelectionModel().clearSelection();
            } else {
                searchBox.setText(searchBox.getText().concat(keyEvent.getText()));
                if (keyEvent.getText() != null && !keyEvent.getText().isEmpty() &&
                        keyEvent.getText().chars().allMatch(Character::isLetterOrDigit)) {
                    searchBox.requestFocus();
                }
            }
            refreshDetailsBoxSelectable();
        });

        updateDetailsBox();
        startSearchControls();
        updateTables();

        addProductButton.setOnMouseClicked(mouseEvent -> AppController
                .showDialog(SceneNavigator.REGISTER_NEW_PRODUCT_FORM, true));
    }

    public void refreshProductsList() {
        assert products != null;
        FilteredList<ProductDao> filteredData = new FilteredList<>(Objects.requireNonNull(products), null);
        String filter = searchBox.getText().toUpperCase();

        if(filter.length() == 0) {
            filteredData.setPredicate(null);
        }
        else {
            filteredData.setPredicate(s -> s.getDescription().toUpperCase().contains(filter) ||
                    s.getCategoryDao()
                            .getDescription()
                            .toUpperCase()
                            .contains(filter) || String.valueOf(s.getIdProduct()).contains(filter));
        }

        productsListView.setItems(filteredData);
    }

    public void startSearchControls() {
        searchBox.textProperty().addListener((observable -> refreshProductsList()));
    }

    public void updateTables() {
        productsListView.setItems(products);
        productsListView.setCellFactory(plv -> new ProductListViewCell());
    }

    public void updateDetailsBox(ProductDao product) {
        detailsWrapperBox.setDisable(false);
        Format format = CurrencyField.getBRLCurrencyFormat();
        unitPriceLabel.setText(format.format(product.getSellPrice()));
        contentLabel.setText(product.getDescription());
        codProductLabel.setText(String.valueOf(product.getIdProduct()));
        categoryLabel.setText(product.getCategoryDao().getDescription());
    }

    public void updateDetailsBox() {
        detailsWrapperBox.setDisable(true);
        Format format = CurrencyField.getBRLCurrencyFormat();
        unitPriceLabel.setText(format.format(0));
        contentLabel.setText("Nenhum produto selecionado");
        codProductLabel.setText("");
        categoryLabel.setText("Sem categoria");
    }

    public void refreshDetailsBoxSelectable() {
        if (productsListView.getSelectionModel().isEmpty()) {
            updateDetailsBox();
        } else {
            updateDetailsBox(productsListView.getSelectionModel().getSelectedItem());
        }
    }

    public void getProductsFromDatabase() {
        try {
            products = Product.getProducts();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void searchByCategory(String category) {
        searchBox.setText(category);
    }
}
