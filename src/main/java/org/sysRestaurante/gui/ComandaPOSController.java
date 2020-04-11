package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.EmployeeDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Personnel;
import org.sysRestaurante.model.Product;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static javafx.collections.FXCollections.*;

public class ComandaPOSController extends POS {

    @FXML
    private Button exitButton;
    @FXML
    private Button finalizeOrderButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button addProductButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button clearButton;
    @FXML
    private Spinner<Integer> qtySpinner;
    @FXML
    private BorderPane wrapperBox;
    @FXML
    private Label tableLabel;
    @FXML
    private Label codOrderLabel;
    @FXML
    private ComboBox<EmployeeDao> employeeComboBox;
    @FXML
    private TextField customerBox;
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

    private ComandaDao comanda = AppFactory.getComandaDao();
    private final ObservableList<ProductDao> selectedProductsList = observableArrayList();
    private final ObservableList<ProductDao> products = new Product().getProducts();

    @FXML
    public void initialize() {
        AppFactory.setPos(this);
        AppFactory.setOrderDao(comanda);
        handleEmployeesComboBox();
        calculateTimePeriod();
        writeCustomer();
        setStageControls();
        updateTables();
        updateDetailsBox();
        startSearchControls();
        updateComandaItems();

        exitButton.setOnAction(e1 -> {
            wrapperBox.getScene().getWindow().hide();
            Platform.runLater(this::saveChanges);
        });

        productsListView.setItems(products);
        productsListView.setCellFactory(plv -> new ProductListViewCell());
        employeeComboBox.setOnAction(event -> updateEmployee());
        tableLabel.setText("MESA #" + this.comanda.getIdTable());
        codOrderLabel.setText(String.valueOf(comanda.getIdOrder()));
        customerLabel.setText(Order.getCustomerName(comanda.getIdOrder()));
        customerBox.setOnKeyTyped(e-> customerLabel.setText(customerBox.getText()));
        NumberFormat format = CurrencyField.getBRLCurrencyFormat();
        subtotalLabel.setText(format.format(comanda.getTotal()));
        totalLabel.setText(format.format(comanda.getTotal()));

        updateButton.setOnMouseClicked(ac -> {
            updateComandaItems();
            saveChanges();
        });

        finalizeOrderButton.setOnMouseClicked(e -> {
            saveChanges();
            onFinalizeOrder();
        });

        Platform.runLater(() -> {
            updateEmployeeOnClose();
            selectedProductsTableView.refresh();
        });
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

    public void setCustomer() {
        Order.insertCustomerName(comanda.getIdOrder(), customerBox.getText());
    }

    public void writeCustomer() {
        customerBox.setText(Order.getCustomerName(comanda.getIdOrder()));
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

    public void saveChanges() {
        setCustomer();
        clear();
        saveProductList();
        AppFactory.getManageComandaController().refreshTileList();
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
}
