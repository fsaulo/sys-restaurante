package org.sysRestaurante.gui;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ProductManagementController {

    @FXML
    private VBox addProductButton;
    @FXML
    private TableView<ProductDao> productTableView;
    @FXML
    private TableColumn<ProductDao, Integer> codTableColoumn;
    @FXML
    private TableColumn<ProductDao, String> descriptionTableColumn;
    @FXML
    private TableColumn<ProductDao.CategoryDao, String> categoryTableColumn;
    @FXML
    private TableColumn<ProductDao, Double> unitPriceTableColumn;
    @FXML
    private TableColumn<ProductDao, Integer> currentStockTableColumn;
    @FXML
    private TableColumn<ProductDao, Integer> criticalStockTableColumn;
    @FXML
    private VBox removeProductButton;
    @FXML
    private VBox editProductButton;
    @FXML
    private VBox wrapperVBox;
    @FXML
    private TextField searchBox;
    @FXML
    private ComboBox<ProductDao.CategoryDao> categoryComboBox;
    @FXML
    private BorderPane borderPaneHolder;

    private static ObservableList<ProductDao> products;
    private boolean editing;

    public void initialize() {
        AppFactory.setProductManagementController(this);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
//        productsListView.focusedProperty().addListener((observable) -> refreshDetailsBoxSelectable());
//        productsListView.setOnMouseClicked(event -> refreshDetailsBoxSelectable());
//        productsListView.requestFocus();
//        productsListView.setOnKeyPressed(keyEvent -> {
//            if (keyEvent.getCode() == KeyCode.ESCAPE) {
//                wrapperVBox.requestFocus();
//                productsListView.getSelectionModel().clearSelection();
//            } else {
//                searchBox.setText(searchBox.getText().concat(keyEvent.getText()));
//                if (keyEvent.getText() != null && !keyEvent.getText().isEmpty() &&
//                        keyEvent.getText().chars().allMatch(Character::isLetterOrDigit)) {
//                    searchBox.requestFocus();
//                }
//            }
//            refreshDetailsBoxSelectable();
//        });

//        startSearchControls();
        updateTable();
        handleCategoryComboBox();

//        editProductButton.setOnMouseClicked(mouseEvent -> openEditingForm());
//        removeProductButton.setOnMouseClicked(mouseEvent -> confirmDelete());
        addProductButton.setOnMouseClicked(mouseEvent ->
                AppController.showDialog(SceneNavigator.REGISTER_NEW_PRODUCT_FORM, true));
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
                            .getCategoryDescription()
                            .toUpperCase()
                            .contains(filter) || String.valueOf(s.getIdProduct()).contains(filter));
        }

//        productsListView.setItems(filteredData);
    }

//    public void startSearchControls() {
//        searchBox.textProperty().addListener((observable -> refreshProductsList()));
//    }

    public void updateTable() {
        productTableView.setItems(getProductsFromDatabase());
        codTableColoumn.setCellValueFactory(new PropertyValueFactory<>("idProduct"));
        unitPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        categoryTableColumn.setCellValueFactory(new PropertyValueFactory<>("categoryDescription"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        currentStockTableColumn.setCellValueFactory(new PropertyValueFactory<>("supply"));
        criticalStockTableColumn.setCellValueFactory(new PropertyValueFactory<>("minSupply"));
        unitPriceTableColumn.setCellFactory((CellFormatter<ProductDao, Double>) value -> CurrencyField
                .getBRLCurrencyFormat()
                .format(value));
        productTableView.refresh();
    }

//    public void refreshDetailsBoxSelectable() {
//        if (productsListView.getSelectionModel().isEmpty()) {
//            updateDetailsBox();
//        } else {
//            updateDetailsBox(productsListView.getSelectionModel().getSelectedItem());
//        }
//    }

    public ObservableList getProductsFromDatabase() {
        try {
            products = Product.getProducts();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return products;
    }

    public void searchByCategory(String category) {
        searchBox.setText(category);
    }

    public void reload() {
        initialize();
    }

//    public void confirmDelete() {
//        try {
//            if (!productsListView.getSelectionModel().isEmpty()) {
//                ProductDao selectedProduct = productsListView.getSelectionModel().getSelectedItem();
//
//                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("Confirmação do sistema");
//                alert.setHeaderText("Tem certeza que deseja remover este item da base de dados?");
//                alert.setContentText("Essa ação não poderá ser desfeita.");
//                alert.showAndWait();
//
//                if (alert.getResult().equals(ButtonType.OK)) {
//                    Product.remove(selectedProduct.getIdProduct());
//                    NotificationHandler.showInfo("Produto removido com sucesso.");
//                    this.reload();
//                }
//            }
//        } catch (NullPointerException ex) {
//            ex.printStackTrace();
//            NotificationHandler.errorDialog(ex);
//        }
//    }

    //    public void openEditingForm() {
    //        if (!productsListView.getSelectionModel().isEmpty()) {
    //            ProductDao selectedProduct = productsListView.getSelectionModel().getSelectedItem();
    //            setEditing(true);
    //            AppFactory.setProductDao(selectedProduct);
    //            AppController.showDialog(SceneNavigator.REGISTER_NEW_PRODUCT_FORM, true);
    //        }
    //    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
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
                            setText(item.getCategoryDescription());
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
    }
}
