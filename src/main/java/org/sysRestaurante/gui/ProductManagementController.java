package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Product;
import org.sysRestaurante.util.NotificationHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
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
    private TableColumn<ProductDao, Integer> soldTableColumn;
    @FXML
    private VBox removeProductButton;
    @FXML
    private VBox editProductButton;
    @FXML
    private VBox wrapperVBox;
    @FXML
    private VBox refresh;
    @FXML
    private TextField searchBox;
    @FXML
    private ComboBox<ProductDao.CategoryDao> categoryComboBox;
    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private CheckBox stockCheckBox;

    private static ObservableList<ProductDao> products;
    private boolean editing;

    public void initialize() {
        Platform.runLater(this::requestFocus);
        AppFactory.setProductManagementController(this);

        searchBox.setOnKeyTyped(keyEvent -> refreshProductsList());
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        productTableView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                wrapperVBox.requestFocus();
                productTableView.getSelectionModel().clearSelection();
            } else {
                searchBox.setText(searchBox.getText().concat(keyEvent.getText()));
                if (keyEvent.getText() != null && !keyEvent.getText().isEmpty() &&
                        keyEvent.getText().chars().allMatch(Character::isLetterOrDigit)) {
                    searchBox.requestFocus();
                }
            }
        });

        productTableView.setRowFactory(tableView -> {
            final TableRow<ProductDao> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();

            SeparatorMenuItem separatorMenu = new SeparatorMenuItem();
            MenuItem optionEditProduct = new MenuItem("Editar");
            MenuItem optionsDetails = new MenuItem("Mostrar detalhes");
            MenuItem optionsDelete = new MenuItem("Remover");

            optionEditProduct.setOnAction(actionEvent -> openEditingForm());
            optionsDelete.setOnAction(actionEvent -> confirmDelete());

            // FIXME: Construct page to show details about product such as sells & more.
            optionsDetails.setDisable(true);
            contextMenu.getItems().addAll(optionEditProduct, optionsDetails, separatorMenu, optionsDelete);
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty().not())
                    .then(contextMenu)
                    .otherwise((ContextMenu) null));

            return row;
        });

        updateTable();
        handleCategoryComboBox();

        categoryComboBox.setOnAction(observable -> searchByCategory(categoryComboBox.getSelectionModel().getSelectedItem().getCategoryDescription()));
        editProductButton.setOnMouseClicked(mouseEvent -> openEditingForm());
        removeProductButton.setOnMouseClicked(mouseEvent -> confirmDelete());
        refresh.setOnMouseClicked(mouseEvent -> updateTable());
        addProductButton.setOnMouseClicked(mouseEvent -> AppController.showDialog(SceneNavigator.REGISTER_NEW_PRODUCT_FORM, true));
        stockCheckBox.selectedProperty().addListener(observable -> filterOnlyInStock(stockCheckBox.isSelected()));
    }

    private void requestFocus() {
        wrapperVBox.requestFocus();
    }

    public void refreshProductsList() {
        FilteredList<ProductDao> filteredData = new FilteredList<>(Objects.requireNonNull(products), null);
        String filter = searchBox.getText().toUpperCase();

        if (filter.length() == 0) {
            filteredData.setPredicate(null);
        } else {
            filteredData.setPredicate(s -> s.getDescription().toUpperCase().contains(filter) ||
                    s.getCategoryDao()
                            .getCategoryDescription()
                            .toUpperCase()
                            .contains(filter) || String.valueOf(s.getIdProduct()).contains(filter));
        }

        productTableView.setItems(filteredData);
    }

    public void searchByCategory(String category) {
        FilteredList<ProductDao> filteredData = new FilteredList<>(Objects.requireNonNull(products), null);
        String filter = category.toUpperCase();

        if (filter.length() == 0 || filter.equals("TODOS")) {
            filteredData.setPredicate(null);
        } else {
          filteredData.setPredicate(s -> s.getCategoryDescription().toUpperCase().contains(filter));
        }

        productTableView.setItems(filteredData);
    }

    public void filterOnlyInStock(boolean selected) {
        ObservableList<ProductDao> filtered = productTableView.getItems();
        FilteredList<ProductDao> filteredData = new FilteredList<>(Objects.requireNonNull(filtered), null);

        if (selected) {
            filteredData.setPredicate(s -> s.getSupply() > 0);
        } else {
            filteredData.setPredicate(null);
        }

        productTableView.setItems(filteredData);
    }

    public void updateTable() {
        stockCheckBox.setSelected(false);
        productTableView.setItems(getProductsFromDatabase());
        codTableColoumn.setCellValueFactory(new PropertyValueFactory<>("idProduct"));
        unitPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        categoryTableColumn.setCellValueFactory(new PropertyValueFactory<>("categoryDescription"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        currentStockTableColumn.setCellValueFactory(new PropertyValueFactory<>("supply"));
        criticalStockTableColumn.setCellValueFactory(new PropertyValueFactory<>("minSupply"));
        soldTableColumn.setCellValueFactory(new PropertyValueFactory<>("sold"));
        unitPriceTableColumn.setCellFactory((CellFormatter<ProductDao, Double>) value -> CurrencyField
                .getBRLCurrencyFormat()
                .format(value));
        productTableView.refresh();
    }

    public ObservableList getProductsFromDatabase() {
        try {
            products = Product.getProducts();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return products;
    }

    public void reload() {
        initialize();
    }

    public void confirmDelete() {
        try {
            if (!productTableView.getSelectionModel().isEmpty()) {
                ProductDao selectedProduct = productTableView.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmação do sistema");
                alert.setHeaderText("Tem certeza que deseja remover este item da base de dados?");
                alert.setContentText("Essa ação não poderá ser desfeita.");
                alert.showAndWait();

                if (alert.getResult().equals(ButtonType.OK)) {
                    Product.remove(selectedProduct.getIdProduct());
                    NotificationHandler.showInfo("Produto removido com sucesso.");
                    this.reload();
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            NotificationHandler.errorDialog(ex);
        }
    }

        public void openEditingForm() {
            if (!productTableView.getSelectionModel().isEmpty()) {
                ProductDao selectedProduct = productTableView.getSelectionModel().getSelectedItem();
                setEditing(true);
                AppFactory.setProductDao(selectedProduct);
                AppController.showDialog(SceneNavigator.REGISTER_NEW_PRODUCT_FORM, true);
            }
        }

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

        ProductDao.CategoryDao all = new ProductDao.CategoryDao();
        all.setCategoryDescription("Todos");
        categoryComboBox.getItems().add(all);
        categoryComboBox.getSelectionModel().select(all);
    }
}
