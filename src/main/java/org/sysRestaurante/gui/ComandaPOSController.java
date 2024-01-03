package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.EmployeeDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.model.Management;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Personnel;
import org.sysRestaurante.model.Product;
import org.sysRestaurante.util.ExceptionHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

import static javafx.collections.FXCollections.observableArrayList;

public class ComandaPOSController extends POS {

    @FXML
    private Button exitButton;
    @FXML
    private Button finalizeOrderButton;
    @FXML
    private Button addProductButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button cancelOrderButton;
    @FXML
    private Button changeTableButton;
    @FXML
    private Spinner<Integer> qtySpinner;
    @FXML
    private BorderPane wrapperBox;
    @FXML
    private Label tableLabel;
    @FXML
    private Label codOrderLabel;
    @FXML
    private Label codComandaLabel;
    @FXML
    private ComboBox<EmployeeDao> employeeComboBox;
    @FXML
    private TextField customerBox;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Label customerLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label timeLabel;
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
    private TextField searchBox;
    @FXML
    private Label unitPriceLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label codProductLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private VBox detailsWrapperBox;
    @FXML
    private Label totalLabel;
	@FXML
	private Label dateLabel;

    private final ComandaDao comanda = AppFactory.getComandaDao();
    private final ObservableList<ProductDao> selectedProductsList = observableArrayList();
    private final ObservableList<ProductDao> products = Product.getProducts();

    public ComandaPOSController() throws SQLException {
    }

    @FXML
    public void initialize() {
        assert(comanda != null);
        
        AppFactory.setComandaPOSController(this);
        AppFactory.setPos(this);
        AppFactory.setOrderDao(comanda);

        handleEmployeesComboBox();
        calculateTimePeriod();
        setStageControls();
        updateTables();
        updateDetailsBox();
        startSearchControls();
        updateComandaItems();
        handleChangeTable();

        exitButton.setOnAction(e1 -> exit());
        productsListView.setItems(products);
        productsListView.setCellFactory(plv -> new ProductListViewCell());
        employeeComboBox.setOnAction(event -> updateEmployee());
        tableLabel.setText("MESA #" + this.comanda.getIdTable());
        codOrderLabel.setText(String.valueOf(comanda.getIdOrder()));
        codComandaLabel.setText(String.valueOf(comanda.getIdComanda()));
		customerLabel.setText(parseCustomerName());
        changeTableButton.setStyle("-fx-cursor: hand;");

        addCustomerButton.setOnAction(e -> {
            Order.insertCustomerName(comanda.getIdOrder(), customerBox.getText());
            customerLabel.setText(customerBox.getText());
            customerBox.clear();
        });

        customerBox.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                Order.insertCustomerName(comanda.getIdOrder(), customerBox.getText());
                customerLabel.setText(customerBox.getText());
                customerBox.clear();
            }
        });

        NumberFormat format = CurrencyField.getBRLCurrencyFormat();
        subtotalLabel.setText(format.format(comanda.getTotal()));
        totalLabel.setText(format.format(comanda.getTotal()));
        cancelOrderButton.setOnMouseClicked(e -> onCancelOrder());
		dateLabel.setText(DateFormatter.DETAILED_TIME.format(comanda
					.getDateOpening()
					.atTime(comanda.getTimeOpening())));

        finalizeOrderButton.setOnMouseClicked(e -> {
            saveChanges();
            onFinalizeOrder();
        });

        Platform.runLater(() -> {
            updateEmployeeOnClose();
            selectedProductsTableView.refresh();
        });
    }

    public void handleChangeTable() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.SELEC_NEW_TABLE_VIEW));
        PopOver popOver;
        try {
            popOver = new PopOver(loader.load());
            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            PopOver finalPopOver = popOver;
            changeTableButton.setOnMouseClicked(e -> finalPopOver.show(changeTableButton));
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionHandler.incrementGlobalExceptionsCount();
        }
    }

    public void setStageControls() {
        setFromPOS(true);
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

	public String parseCustomerName() {
		String customerName = Order.getCustomerName(comanda.getIdOrder());
		String defaultName = "Não fornecido";
        return Objects.requireNonNullElse(customerName, defaultName);
	}

    public void updateEmployee() {
        int idEmployee;

        try {
            EmployeeDao employee = employeeComboBox.selectionModelProperty().get().getSelectedItem();
            idEmployee = employee.getIdEmployee();
        } catch (NullPointerException ex) {
            idEmployee = -1;
        }

        Order.updateEmployee(comanda.getIdOrder(), idEmployee);
    }

    public void updateEmployeeOnClose() {
        wrapperBox.getScene().getWindow().setOnCloseRequest(e1 -> Platform.runLater(this::saveChanges));
    }

    public void saveProductList() {
        if (!selectedProductsTableView.getItems().isEmpty()) {
            ArrayList<ProductDao> items = new ArrayList<>(selectedProductsTableView.getItems());
            Order.addProductsToOrder(comanda.getIdOrder(), items);
        }
    }

    public void clear() {
        Order.removeProductsFromOrder(comanda.getIdOrder());
    }

    public void exit() {
        wrapperBox.getScene().getWindow().hide();
        Platform.runLater(this::saveChanges);
    }

    public void saveChanges() {
        clear();
        saveProductList();
        AppFactory.getManageComandaController().update();
    }

    public void handleEmployeesComboBox() {
        ArrayList<EmployeeDao> employees = new Personnel().list();
        Callback<ListView<EmployeeDao>, ListCell<EmployeeDao>> cellFactory = new Callback<>() {
            @Override
            public ListCell<EmployeeDao> call(ListView<EmployeeDao> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(EmployeeDao item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText("ID: " + item.getIdEmployee() + " " + item.getName());
                        }
                    }
                };
            }
        };

        employeeComboBox.setButtonCell(cellFactory.call(null));
        employeeComboBox.setCellFactory(cellFactory);

        for (EmployeeDao employee : employees) {
            employeeComboBox.getItems().add(employee);
        }

        EmployeeDao selectedEmployee = null;
        for (EmployeeDao emp : employees) {
            if (emp.getIdEmployee() == comanda.getIdEmployee()) {
                selectedEmployee = emp;
                break;
            }
        }

        if (selectedEmployee != null ) {
            employeeComboBox.getSelectionModel().select(selectedEmployee);
        }
    }

    public void updateTableCod() {
        tableLabel.setText("MESA #" + AppFactory.getComandaDao().getIdTable());
    }

    public void calculateTimePeriod() {
        LocalDateTime dateTimeOpenned = comanda.getDateOpening().atTime(comanda.getTimeOpening());
        if (ChronoUnit.DAYS.between(dateTimeOpenned, LocalDateTime.now()) > 1) {
            timeLabel.setText(ChronoUnit.DAYS.between(dateTimeOpenned, LocalDateTime.now()) + " dias");
        } else if (ChronoUnit.HOURS.between(dateTimeOpenned, LocalDateTime.now()) > 1) {
            timeLabel.setText(ChronoUnit.HOURS.between(dateTimeOpenned, LocalDateTime.now()) + " horas");
        } else if (ChronoUnit.MINUTES.between(dateTimeOpenned, LocalDateTime.now()) > 60) {
            timeLabel.setText("Mais de uma hora");
        } else {
            timeLabel.setText(ChronoUnit.MINUTES.between(dateTimeOpenned, LocalDateTime.now()) + " minutos");
        }
    }

    public void onCancelOrder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta do sistema");
        alert.setHeaderText("Tem certeza que deseja cancelar o pedido?");
        alert.setContentText("Essa operação não poderá ser desfeita.");
        alert.initOwner(wrapperBox.getScene().getWindow());
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            double total = getTotal();
            int idComanda = comanda.getIdComanda();
            int idOrder = comanda.getIdOrder();
            int idTable = comanda.getIdTable();
            final int CANCELED = 3;
            Order.cancel(idOrder);
            Order.closeComanda(idComanda, total);
            Order.updateOrderStatus(idComanda, CANCELED);
            Order.updateOrderAmount(idComanda, total, 0, 0);
            Management.closeTable(idTable);
            exit();
		}
    }
}
