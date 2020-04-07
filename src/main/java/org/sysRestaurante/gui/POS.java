package org.sysRestaurante.gui;

import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import javafx.application.Platform;
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
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javafx.stage.Window;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.SpinnerCellFactory;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.NotificationHandler;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class POS {

    private VBox detailsWrapperBox;
    private TextField searchBox;
    private Button addProductButton;
    private Button removeButton;
    private Button clearButton;
    private Label unitPriceLabel;
    private Label contentLabel;
    private Label codProductLabel;
    private Label categoryLabel;
    private Label subtotalLabel;
    private BorderPane wrapperBox;
    private ListView<ProductDao> productsListView;
    private TableView<ProductDao> selectedProductsTableView;
    private TableColumn<ProductDao, String> descriptionColumn;
    private TableColumn<ProductDao, Integer> qtdColumn;
    private TableColumn<ProductDao, Double> priceColumn;
    private TableColumn<ProductDao, Double> totalColumn;
    private Spinner<Integer> qtySpinner;
    private ObservableList<ProductDao> selectedProductsList = FXCollections.observableArrayList();
    private ObservableList<ProductDao> products = FXCollections.observableArrayList();
    private double total = 0;
    private boolean fromPOS = false;

    public void setFromPOS(boolean fromPOS) {
        this.fromPOS = fromPOS;
    }

    public void setRemoveButton(Button removeButton) {
        this.removeButton = removeButton;
    }

    public void setClearButton(Button clearButton) {
        this.clearButton = clearButton;
    }

    public void setDetailsWrapperBox(VBox detailsWrapperBox) {
        this.detailsWrapperBox = detailsWrapperBox;
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

    public void setSubtotalLabel(Label subtotalLabel) {
        this.subtotalLabel = subtotalLabel;
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

    public void setProducts(ObservableList<ProductDao> products){
        this.products = products;
    }

    public void setSelectedProductsList(ObservableList<ProductDao> selectedProductsList) {
        this.selectedProductsList = selectedProductsList;
    }

    public void refreshProductsList() {
        FilteredList<ProductDao> filteredData = new FilteredList<>(products, null);
        String filter = searchBox.getText().toUpperCase();

        if(filter == null || filter.length() == 0) {
            filteredData.setPredicate(null);
        }
        else {
            filteredData.setPredicate(s -> s.getDescription().toUpperCase().contains(filter) ||
                            s.getCategory().toUpperCase().contains(filter) ||
                            String.valueOf(s.getIdProduct()).contains(filter));
        }

        productsListView.setItems(filteredData);
    }

    public void startSearchControls() {
        selectedProductsTableView.focusedProperty().addListener((observable) -> refreshDetailsBoxSelectable(false));
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
                wrapperBox.requestFocus();
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
    }

    public void handleKeyEvent() {
        Runnable run = () -> {
            searchBox.requestFocus();
            searchBox.selectAll();
        };
        removeButton.setOnMouseClicked(event -> removeItem());
        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        clearButton.setOnMouseClicked(event -> clearShoppingBasket());
        selectedProductsTableView.setOnMouseClicked(event -> refreshDetailsBoxSelectable(false));
        productsListView.focusedProperty().addListener((observable) -> refreshDetailsBoxSelectable(true));
        productsListView.setOnMouseClicked(event -> refreshDetailsBoxSelectable(true));
        wrapperBox.getScene().getAccelerators().clear();
        wrapperBox.getScene().getAccelerators().put(SceneNavigator.F3_SEARCH, run);
        wrapperBox.getScene().getAccelerators().put(SceneNavigator.F4_CANCEL, this::onCancelButton);
        productsListView.requestFocus();
        productsListView.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case F2:
                    onFinalizeOrder();
                    break;
                case DIGIT1:
                case NUMPAD1:
                    productsListView.getSelectionModel().select(0);
                    break;
                case DIGIT2:
                case NUMPAD2:
                    productsListView.getSelectionModel().select(1);
                    break;
                case DIGIT3:
                case NUMPAD3:
                    productsListView.getSelectionModel().select(2);
                    break;
                case DIGIT4:
                case NUMPAD4:
                    productsListView.getSelectionModel().select(3);
                    break;
                case DIGIT5:
                case NUMPAD5:
                    productsListView.getSelectionModel().select(4);
                    break;
                case DIGIT6:
                case NUMPAD6:
                    productsListView.getSelectionModel().select(5);
                    break;
                case DIGIT7:
                case NUMPAD7:
                    productsListView.getSelectionModel().select(6);
                    break;
                case DIGIT8:
                case NUMPAD8:
                    productsListView.getSelectionModel().select(7);
                    break;
                case DIGIT9:
                case NUMPAD9:
                    productsListView.getSelectionModel().select(8);
                    break;
                case ESCAPE:
                    wrapperBox.requestFocus();
                    productsListView.getSelectionModel().clearSelection();
                    break;
                case ENTER:
                    ProductDao product = productsListView.getSelectionModel().getSelectedItem();
                    if (product != null) addToSelectedProductsList(product);
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
                    wrapperBox.requestFocus();
                    selectedProductsTableView.getSelectionModel().clearSelection();
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
        OrderDao order = AppFactory.getOrderDao();
        if (order instanceof ComandaDao) {
            System.out.println("COMANDA");
            if (selectedProductsTableView.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta do sistema");
                alert.setHeaderText("Atenção!");
                alert.setContentText("Não é possível encerrar o pedido pois nenhum item foi adicionado a lista");
                alert.showAndWait();
            } else {
                AppController.showPaymentDialog(AppFactory.getPos().getPOSWindow(), wrapperBox);
                System.out.println(order.getIdOrder());
                if (fromPOS) getPOSWindow().hide();
                NotificationHandler.showInfo("Pedido realizado com sucesso");
            }
        } else {
            System.out.println("ORDER");
            if (selectedProductsTableView.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta do sistema");
                alert.setHeaderText("Atenção!");
                alert.setContentText("Não é possível encerrar o pedido pois nenhum item foi adicionado a lista");
                alert.showAndWait();
            } else {
                AppFactory.setOrderDao(new OrderDao());
                AppController.showPaymentDialog(AppFactory.getPos().getPOSWindow(), wrapperBox);
                if (AppFactory.getCashierController().isSellConfirmed()) {
                    AppFactory.getSelectedProducts().clear();
                    AppFactory.setOrderDao(new OrderDao());
                    wrapperBox.getScene().getWindow().hide();
                }
            }
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
        subtotalLabel.setText(CurrencyField.getBRLCurrencyFormat().format(total));
    }

    public void updateDetailsBox(ProductDao product) {
        detailsWrapperBox.setDisable(false);
        Format format = CurrencyField.getBRLCurrencyFormat();
        unitPriceLabel.setText(format.format(product.getSellPrice()));
        contentLabel.setText(product.getDescription());
        codProductLabel.setText(String.valueOf(product.getIdProduct()));
        categoryLabel.setText(product.getCategory());
    }

    public void updateDetailsBox() {
        detailsWrapperBox.setDisable(true);
        Format format = CurrencyField.getBRLCurrencyFormat();
        unitPriceLabel.setText(format.format(0));
        contentLabel.setText("Nenhum produto selecionado");
        codProductLabel.setText("");
        categoryLabel.setText("Sem categoria");
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

    public boolean containsId(final ObservableList<ProductDao> list, final int id) {
        return list.stream().anyMatch(o -> o.getIdProduct() == id);
    }

    public void addToSelectedProductsList(ProductDao product) {
        if (selectedProductsList.contains(product)) {
            product.incrementsQuantity();
        } else if (containsId(selectedProductsList, product.getIdProduct())) {
            selectedProductsList.stream().filter(pr -> pr.getDescription()
                    .equals(product.getDescription()))
                    .collect(Collectors.toList()).get(0).incrementsQuantity();
        } else {
            product.setQuantity(1);
            selectedProductsList.add(product);
        }

        product.setTotal(product.getSellPrice());
        updateSelectedList();
    }

    public void addToSelectedProductsList(ProductDao product, int qty) {
        if (selectedProductsList.contains(product)) {
            product.setQuantity(product.getQuantity() + qty);
        } else if (containsId(selectedProductsList, product.getIdProduct())) {
            selectedProductsList.stream().filter(pr -> pr.getDescription()
                    .equals(product.getDescription()))
                    .collect(Collectors.toList()).get(0).incrementsQuantity(qty);
        } else {
            product.setQuantity(qty);
            selectedProductsList.add(product);
        }

        product.setTotal(product.getSellPrice());
        updateSelectedList();
    }

    public double getTotal() {
        return total;
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

    public void updateComandaItems() {
        OrderDao order = AppFactory.getOrderDao();
        List<ProductDao> products = Order.getItemsByOrderId(order.getIdOrder());
        for (ProductDao product : products) {
            addToSelectedProductsList(product, product.getQuantity());
        }
    }

    public void removeItem() {
        if (!selectedProductsTableView.getSelectionModel().isEmpty()) {
            ProductDao selected = selectedProductsTableView.getSelectionModel().getSelectedItem();
            selected.setQuantity(0);
            selectedProductsTableView.getItems().remove(selected);
            selectedProductsList.remove(selected);
            updateSelectedList();
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
