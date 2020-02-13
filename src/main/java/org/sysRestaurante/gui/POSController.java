package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Window;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.SpinnerCellFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Format;
import java.util.ArrayList;

public class POSController {

    private VBox detailsWrapperBox;
    private VBox removeButton;
    private VBox editButton;
    private TextField searchBox;
    private Button addProductButton;
    private Label unitPriceLabel;
    private Label contentLabel;
    private Label codProductLabel;
    private Label categoryLabel;
    private Label label;
    private Label totalCashierLabel;
    private HBox editableItems;
    private BorderPane wrapperBox;
    private ListView<ProductDao> productsListView;
    private TableView<ProductDao> selectedProductsTableView;
    private TableColumn<ProductDao, String> descriptionColumn;
    private TableColumn<ProductDao, Integer> qtdColumn;
    private TableColumn<ProductDao, Double> priceColumn;
    private TableColumn<ProductDao, Double> totalColumn;
    private Spinner<Integer> qtySpinner;
    private ImageView barcodeImage;
    private ObservableList<ProductDao> selectedProductsList = FXCollections.observableArrayList();
    private ObservableList<ProductDao> products = FXCollections.observableArrayList();
    private static final String GENERIC_BARCODE = "src/main/resources/images/barcode.png";
    private double total = 0;

    public void setDetailsWrapperBox(VBox detailsWrapperBox) {
        this.detailsWrapperBox = detailsWrapperBox;
    }

    public void setRemoveButton(VBox removeButton) {
        this.removeButton = removeButton;
    }

    public void setEditButton(VBox editButton) {
        this.editButton = editButton;
    }

    public void setSearchBox(TextField searchBox) {
        this.searchBox = searchBox;
    }

    public void setAddProductButton(Button addProductButton) {
        this.addProductButton = addProductButton;
    }

    public void setUnitPriceLabel(Label unitPriceLabel) {
        this.unitPriceLabel = unitPriceLabel;
    }

    public void setContentLabel(Label contentLabel) {
        this.contentLabel = contentLabel;
    }

    public void setCodProductLabel(Label codProductLabel) {
        this.codProductLabel = codProductLabel;
    }

    public void setCategoryLabel(Label categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public void setTotalCashierLabel(Label totalCashierLabel) {
        this.totalCashierLabel = totalCashierLabel;
    }

    public void setEditableItems(HBox editableItems) {
        this.editableItems = editableItems;
    }

    public void setWrapperBox(BorderPane wrapperBox) {
        this.wrapperBox = wrapperBox;
    }

    public void setProductsListView(ListView<ProductDao> productsListView) {
        this.productsListView = productsListView;
    }

    public void setSelectedProductsTableView(TableView<ProductDao> selectedProductsTableView) {
        this.selectedProductsTableView = selectedProductsTableView;
    }

    public void setDescriptionColumn(TableColumn<ProductDao, String> descriptionColumn) {
        this.descriptionColumn = descriptionColumn;
    }

    public void setQtdColumn(TableColumn<ProductDao, Integer> qtdColumn) {
        this.qtdColumn = qtdColumn;
    }

    public void setPriceColumn(TableColumn<ProductDao, Double> priceColumn) {
        this.priceColumn = priceColumn;
    }

    public void setTotalColumn(TableColumn<ProductDao, Double> totalColumn) {
        this.totalColumn = totalColumn;
    }

    public void setQtySpinner(Spinner<Integer> qtySpinner) {
        this.qtySpinner = qtySpinner;
    }

    public void setBarcodeImage(ImageView barcodeImage) {
        this.barcodeImage = barcodeImage;
    }

    public void setProducts(ObservableList<ProductDao> products){
        this.products = products;
    }

    public void setSelectedProductsList(ObservableList<ProductDao> selectedProductsList) {
        this.selectedProductsList = selectedProductsList;
    }

    public ObservableList<ProductDao> getSelectedProductsList() {
        return selectedProductsList;
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
        Runnable run = () -> {
            searchBox.requestFocus();
            searchBox.selectAll();
        };
        wrapperBox.getScene().getAccelerators().clear();
        wrapperBox.getScene().getAccelerators().put(SceneNavigator.F3_SEARCH, run);
        wrapperBox.getScene().getAccelerators().put(SceneNavigator.F4_CANCEL, this::onCancelButton);
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
                    wrapperBox.requestFocus();
                    productsListView.getSelectionModel().clearSelection();
                    break;
                default:
                    searchBox.setText(searchBox.getText().concat(keyEvent.getText()));
                    if (keyEvent.getText() != null && !keyEvent.getText().isEmpty() &&
                            keyEvent.getText().chars().allMatch(Character::isLetterOrDigit)) {
                        searchBox.requestFocus();
                    }
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
                    searchBox.setText(searchBox.getText().concat(keyEvent.getText()));
                    if (keyEvent.getText() != null && !keyEvent.getText().isEmpty() &&
                            keyEvent.getText().chars().allMatch(Character::isLetterOrDigit)) {
                        searchBox.requestFocus();
                    }
                    break;
            }
            refreshDetailsBoxSelectable(false);
        });
    }

    public void onFinalizeOrder() {
        ArrayList<ProductDao> products = new ArrayList<>(selectedProductsTableView.getItems());
        AppFactory.setSelectedProducts(products);
        if (this instanceof CashierPOSController) {
            if (selectedProductsTableView.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta do sistema");
                alert.setHeaderText("Atenção!");
                alert.setContentText("Não é possível encerrar o pedido pois nenhum item foi adicionado a lista");
                alert.showAndWait();
            } else {
                AppFactory.setOrderDao(new OrderDao());
                AppController.openFinishSell();
                if (AppFactory.getCashierController().isSellConfirmed()) {
                    AppFactory.getSelectedProducts().clear();
                    AppFactory.setOrderDao(new OrderDao());
                    editableItems.getScene().getWindow().hide();
                }
            }
        } else {
            wrapperBox.getScene().getWindow().hide();
        }
    }

    public boolean onCancelButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta do sistema");
        alert.setHeaderText("Tem certeza que deseja cancelar venda?");
        alert.setContentText("Todos os registros salvos serão perdidos.");
        alert.initOwner(wrapperBox.getScene().getWindow());
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            wrapperBox.getScene().getWindow().hide();
            AppFactory.setOrderDao(new OrderDao());
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

    protected void updateSelectedList() {
        if (selectedProductsTableView.getItems().isEmpty()) {
            selectedProductsTableView.setEditable(false);
        } else selectedProductsTableView.setEditable(true);

        selectedProductsTableView.setItems(selectedProductsList);
        selectedProductsTableView.refresh();
        updateTotalCashierLabel();
    }

    protected void updateTotalCashierLabel() {
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
        if (!selectedProductsTableView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação do sistema");
            alert.setHeaderText("Tem certeza que deseja limpar todos os itens do carrinho?");
            alert.setContentText("Essa ação não poderá ser desfeita.");
            alert.initOwner(wrapperBox.getScene().getWindow());
            alert.showAndWait();

            if (alert.getResult().equals(ButtonType.OK)) {
                for (ProductDao item : selectedProductsTableView.getSelectionModel().getSelectedItems()) {
                    item.setQuantity(0);
                }
                selectedProductsTableView.getItems().clear();
                selectedProductsList.clear();
                updateSelectedList();
            }
        }
    }

    public Window getPOSWindow() {
        Window window = wrapperBox.getScene().getWindow();
        if (window != null) {
            return window;
        }
        return new Scene(wrapperBox).getWindow();
    }

    public void searchByCategory(String category) {
        searchBox.setText(category);
    }
}
