package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.KitchenOrderDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.SpinnerCellFactory;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.io.IOException;
import java.text.Format;
import java.util.*;
import java.util.logging.Logger;

public class POS {

    private VBox detailsWrapperBox;
    private TextField searchBox;
    private Button addProductButton;
    private Button removeButton;
    private Button clearButton;
    @FXML
    private TextArea orderDetailsTextArea;
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

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(POS.class.getName());

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

        if(filter.isEmpty()) {
            filteredData.setPredicate(null);
        }
        else {
            filteredData.setPredicate(s -> s.getDescription().toUpperCase().contains(filter) ||
                            s.getCategoryDao().getCategoryDescription().toUpperCase().contains(filter) ||
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
                product.setTotal(product.getSellPrice() * product.getQuantity());
                updateSelectedList();
            }
        });

        searchBox.textProperty().addListener((observable -> refreshProductsList()));
        searchBox.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !productsListView.getItems().isEmpty()) {
                productsListView.getSelectionModel().selectFirst();
                addToSelectedProductsList(productsListView.getSelectionModel().getSelectedItem(), 1, true);
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                wrapperBox.requestFocus();
            }
        });

        addProductButton.setOnAction(event -> {
            if (!productsListView.getItems().isEmpty()) {
                ProductDao productDao = productsListView.getSelectionModel().getSelectedItem();
                int qtyProduct = qtySpinner.getValue();
                addToSelectedProductsList(productDao, qtyProduct, true);
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
                    if (product != null) {
                        addToSelectedProductsList(product, 1, true);
                    }
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
        ArrayList<ProductDao> selectedProducts = new ArrayList<>(selectedProductsTableView.getItems());
        AppFactory.setSelectedProducts(selectedProducts);
        OrderDao order = AppFactory.getOrderDao();
        if (order instanceof ComandaDao) {
            if (selectedProductsTableView.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta do sistema");
                alert.setHeaderText("Atenção!");
                alert.setContentText("Não é possível encerrar o pedido pois nenhum item foi adicionado a lista");
                alert.initOwner(wrapperBox.getScene().getWindow());
                alert.showAndWait();
            } else {
                AppController.showPaymentDialog(AppFactory.getPos().getPOSWindow(), wrapperBox);

                if (AppController.isSellConfirmed()) {
                    NotificationHandler.showInfo("Comanda finalizada com sucesso!");
                    AppController.setSellConfirmed(false);
                    if (fromPOS) getPOSWindow().hide();
                }
            }
        } else {
            if (selectedProductsTableView.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta do sistema");
                alert.setHeaderText("Atenção!");
                alert.setContentText("Não é possível encerrar o pedido pois nenhum item foi adicionado a lista");
                alert.initOwner(wrapperBox.getScene().getWindow());
                alert.showAndWait();
            } else {
                AppFactory.setOrderDao(new OrderDao());
                AppController.showPaymentDialog(AppFactory.getPos().getPOSWindow(), wrapperBox);
                if (AppController.isSellConfirmed()) {
                    AppController.setSellConfirmed(false);
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
        qtdColumn.setEditable(false);
    }

    protected void updateSelectedList() {
        try {
            selectedProductsTableView.setEditable(false);
            selectedProductsTableView.setItems(selectedProductsList);
            selectedProductsTableView.refresh();
            updateTotalCashierLabel();
        } catch (NullPointerException exception) {
            LOGGER.warning("Not possible to update the selected items.");
        }
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
        categoryLabel.setText(product.getCategoryDao().getCategoryDescription());
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

    public void addToSelectedProductsList(ProductDao product, int qty, boolean sendToKitchen) {
        if (sendToKitchen) {
            ProductDao tempProduct = new ProductDao(product);
            tempProduct.setQuantity(qty);
            if (registerKitchenOrder(tempProduct)) {
                orderDetailsTextArea.clear();
            }
        }

        if (selectedProductsList.contains(product) || containsId(selectedProductsList, product.getIdProduct())) {
            final ProductDao tempProduct = product;
            ProductDao selectedProduct = new ProductDao();
            Optional<ProductDao> optionalProductDao = selectedProductsList
                    .stream()
                    .filter(pr -> pr.getIdProduct() == tempProduct.getIdProduct())
                    .findFirst();

            if (optionalProductDao.isPresent()){
                selectedProduct = optionalProductDao.get();
            }

            selectedProduct.incrementsQuantity(qty);
        } else {
            product.setQuantity(qty);
            selectedProductsList.add(product);
        }

        product.setTotal(product.getSellPrice() * product.getQuantity());
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
                OrderDao order = AppFactory.getOrderDao();
                if (order instanceof ComandaDao) {
                    List<KitchenOrderDao> tickets = Order.getKitchenTicketsByComandaId(((ComandaDao) order).getIdComanda());
                    assert tickets != null;
                    for (var item : tickets) {
                        Order.updateKitchenOrderStatus(item.getIdKitchenOrder(), KitchenOrderDao.KitchenOrderStatus.CANCELLED.getValue());
                        LOGGER.info("Ticket #" + item.getIdKitchenOrder() + " was cancelled");
                    }
                }

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
        assert products != null;
        for (ProductDao product : products) {
            addToSelectedProductsList(product, product.getQuantity(), false);
        }
    }

    public void removeItem() {
        try {
            if (!selectedProductsTableView.getSelectionModel().isEmpty()) {
                ProductDao selected = selectedProductsTableView.getSelectionModel().getSelectedItem();
                tryToCancelTicketByProduct(selected);
                selected.setQuantity(0);
                selectedProductsTableView.getItems().remove(selected);
                selectedProductsList.remove(selected);
                updateSelectedList();
            }
        } catch (NullPointerException exception) {
            LOGGER.info("There's no selected item to remove.");
        }
    }

    public void tryToCancelTicketByProduct(ProductDao product) {
        OrderDao order = AppFactory.getOrderDao();
        if (!(order instanceof ComandaDao)) {
            return;
        }

        ArrayList<KitchenOrderDao> kitchenOrdersList = Order.getKitchenTicketsByComandaId(
                ((ComandaDao) order).getIdComanda()
        );

        assert kitchenOrdersList != null;
        kitchenOrdersList.sort(Comparator.comparing(KitchenOrderDao::getKitchenOrderDateTime).reversed());
        for (var item : kitchenOrdersList) {
            ArrayList<ProductDao> products =
                    (ArrayList<ProductDao>) Order.getItemsByKitchenOrderId(item.getIdKitchenOrder());

            assert products != null;
            for (var productInTicket : products) {
                // Cancel the first ticket that contains product id and quantity
                if (productInTicket.getIdProduct() == product.getIdProduct() && productInTicket.getQuantity() == product.getQuantity() && !(
                        item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.CANCELLED) ||
                        item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.RETURNED))
                ) {
                    Order.updateKitchenOrderStatus(
                            item.getIdKitchenOrder(),
                            KitchenOrderDao.KitchenOrderStatus.CANCELLED.getValue()
                    );
                    LOGGER.info("Ticket #" + item.getIdKitchenOrder() + " was cancelled");
                    return;
                } else {
                    int qtyToRemove = product.getQuantity();
                    if (productInTicket.getIdProduct() == product.getIdProduct() && productInTicket.getQuantity() <= qtyToRemove && !(
                            item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.CANCELLED) ||
                            item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.RETURNED))
                    ) {
                        int qtyFinal = qtyToRemove - productInTicket.getQuantity();
                        if (qtyFinal < 0) return;
                        Order.updateKitchenOrderStatus(
                                item.getIdKitchenOrder(),
                                KitchenOrderDao.KitchenOrderStatus.CANCELLED.getValue()
                        );
                        product.setQuantity(qtyFinal);
                        LOGGER.info("Ticket #" + item.getIdKitchenOrder() + " was cancelled");
                    }
                }
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

    private boolean registerKitchenOrder(ProductDao product) {
        OrderDao order = AppFactory.getOrderDao();
        if (order instanceof ComandaDao) {
            int productType = product.getCategoryDao().getIdCategory();
            if (product.isMenuItem() || productType == ProductDao.CategoryDao.Type.LUNCH.getValue()
                    || productType == ProductDao.CategoryDao.Type.TASTE.getValue()
                    || productType == ProductDao.CategoryDao.Type.EXTRA_PORTION.getValue()) {

                String notes = Objects.equals(orderDetailsTextArea.getText(), "") ? "Sem observações" : orderDetailsTextArea.getText();
                int idOrder = Order.newKitchenOrder(((ComandaDao) order).getIdComanda(), KitchenOrderDao.KitchenOrderStatus.WAITING.getValue(), notes);

                if (idOrder > 0) {
                    Order.addProductToKitchenOrder(idOrder, product);

                    KitchenOrderDao ticket = Objects.requireNonNull(Order.getKitchenOrderById(idOrder));
                    ticket.setCustomerName(order.getCustomerName());
                    ticket.setIdOrder(order.getIdOrder());
                    ticket.setIdComanda(((ComandaDao) order).getIdComanda());
                    ticket.setIdEmployee(((ComandaDao) order).getIdEmployee());
                    ticket.setIdTable(((ComandaDao) order).getIdTable());

                    Platform.runLater(() -> {
                        try {
                            AppController.printKitchenTicket(ticket, product);
                        } catch (IOException e) {
                            LOGGER.warning("Impressora nao encontrada. Ticket não será impresso.");
                        }
                    });
                } else {
                    NotificationHandler.showInfo("Não foi possível registrar o pedido na cozinha");
                }
                return true;
            }
        }
        return false;
    }
}
