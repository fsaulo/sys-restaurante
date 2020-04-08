package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.dao.SessionDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Order;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ManageComandaController {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TilePane tilePane;
    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private VBox newComandaButton;
    @FXML
    private Label averageTime;
    @FXML
    private Label busyTables;
    @FXML
    private Label availableTables;
    @FXML
    private Label averageIncome;
    @FXML
    private VBox registerTableButton;

    private SessionDao session;
    private List<ComandaDao> comandas;
    private int permanencyDurationInMinutes = 0;
    private double totalIncome = 0;

    @FXML
    public void initialize() {
        AppFactory.setManageComandaController(this);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        comandas = Order.getComandasByIdCashier(AppFactory.getCashierDao().getIdCashier());
        scrollPane.setFitToWidth(true);
        tilePane.setPrefColumns(50);
        tilePane.getChildren().clear();

        if (!Cashier.isOpen()) {
            newComandaButton.setDisable(true);
        }

        try {
            handleAddComanda();
            handleRegisterTable();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        initSessionDetails();
        listBusyTable();

        NumberFormat format = CurrencyField.getBRLCurrencyFormat();
        averageTime.setText(session.getAveragePermanencyInMinutes() + " minutos");
        busyTables.setText(String.valueOf(session.getBusyTablesCount()));
        availableTables.setText(String.valueOf(session.getAvailableTablesCount()));
        averageIncome.setText(format.format(averageIncome()));
    }

    public void handleAddComanda() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.NEW_COMANDA_DIALOG));
        VBox node = loader.load();
        PopOver popOver = new PopOver(node);
        popOver.arrowLocationProperty().setValue(PopOver.ArrowLocation.BOTTOM_LEFT);
        newComandaButton.setOnMouseClicked(e1 -> popOver.show(newComandaButton));
    }

    public void handleRegisterTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.REGISTER_TABLE_VIEW));
        VBox node = loader.load();
        PopOver popOver = new PopOver(node);
        popOver.arrowLocationProperty().setValue(PopOver.ArrowLocation.BOTTOM_LEFT);
        registerTableButton.setOnMouseClicked(e1 -> popOver.show(registerTableButton));
    }

    public void initSessionDetails() {
        int busy = Order.getBusyTables().size();
        session = AppFactory.getSessionDao();
        session.setIdCashier(AppFactory.getCashierDao().getIdCashier());
        session.setBusyTablesCount(busy);
        session.setAvailableTablesCount(Order.getTables().size() - busy);
    }

    public void listBusyTable() {
        for (ComandaDao item : comandas) {
            computeAverageTime(item);
            computeTotalIncome(item);
            computeTotalComanda(item);

            if (item.getIdCategory() != 6) {
                try {
                    buildAndAddTiles(item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void computeAverageTime(ComandaDao comanda) {
        LocalDateTime dateTimeOpen = comanda.getDateOpening().atTime(comanda.getTimeOpening());

        if (comanda.isOpen()) {
            permanencyDurationInMinutes += ChronoUnit.MINUTES.between(dateTimeOpen, LocalDateTime.now());
        } else {
            LocalDateTime dateTimeClose = comanda.getDateClosing().atTime(comanda.getTimeClosing());
            permanencyDurationInMinutes += ChronoUnit.MINUTES.between(dateTimeOpen, dateTimeClose);
        }

        if (comandas.size() > 0) {
            session.setAveragePermanencyInMinutes(permanencyDurationInMinutes / comandas.size());
        } else {
            session.setAveragePermanencyInMinutes(0);
        }
    }

    public void computeTotalComanda(ComandaDao comanda) {
        double total = 0;
        List<ProductDao> list = Order.getItemsByOrderId(comanda.getIdOrder());
        for (ProductDao product : list) {
            total += product.getQuantity() * product.getSellPrice();
            comanda.setTotal(total);
        }
    }

    public void computeTotalIncome(ComandaDao comada) {
        totalIncome += comada.getTotal();
        session.setTotalComandaIncome(totalIncome);
    }

    public double averageIncome() {
        if (comandas.size() <= 0) return (0);
        else return session.getTotalComandaIncome() / comandas.size();
    }

    public void buildAndAddTiles(ComandaDao comanda) throws IOException {
        Label comandaCod = new Label("#" + comanda.getIdComanda());
        Label statusLabel = new Label("Ocupada");
        Label cashSpent = new Label(CurrencyField.getBRLCurrencyFormat().format(comanda.getTotal()));
        Label tableCod = new Label("MESA " + comanda.getIdTable());
        Label timeDuration = new Label("Aberto há " + calculateTimePeriod(comanda));

        Pane pane = new Pane();
        HBox topTile = new HBox();
        HBox.setHgrow(pane, Priority.ALWAYS);
        topTile.getChildren().addAll(comandaCod, pane, tableCod);
        Circle icon = new Circle(4);
        icon.getStyleClass().add("circle-status");
        comandaCod.setStyle("-fx-font-size: 13px; -fx-opacity: 0.5");
        tableCod.setStyle("-fx-font-size: 17px;");
        statusLabel.setGraphic(icon);
        Separator sep = new Separator();
        sep.setMinHeight(3);

        VBox tile = new VBox();
        tile.getChildren().addAll(topTile, sep, statusLabel, cashSpent, timeDuration);
        tile.setAlignment(Pos.TOP_RIGHT);
        tile.setMinWidth(190);
        tile.getStylesheets().add("css/menu.css");
        tile.getStyleClass().add("comanda-tile");
        tile.setOnMouseEntered(event -> setSelectedLabels(true, comandaCod, statusLabel, cashSpent, tableCod, timeDuration));
        tile.setOnMouseExited(event -> setSelectedLabels(false, comandaCod, statusLabel, cashSpent, tableCod, timeDuration));
        tilePane.getChildren().addAll(tile);
        session.setTotalComandaIncome(session.getTotalComandaIncome() + comanda.getTotal());

        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.COMANDA_VIEW));
        loader.setController(new ComandaViewController(comanda));
        PopOver popOver = new PopOver(loader.load());
        tile.setOnMouseClicked(event -> {
            AppFactory.setOrderDao(comanda);
            popOver.show(tile);
        });
    }

    public void setSelectedLabels(boolean isSelected, Label... labels) {
        if (isSelected) {
            for (Label label : labels)
                label.getStyleClass().add("label-tile-hover");
        } else {
            for (Label label : labels)
                label.getStyleClass().remove("label-tile-hover");
        }
    }

    public void refreshTileList() {
        initialize();
    }

    public String calculateTimePeriod(ComandaDao comanda) {
        LocalDateTime dateTimeOpenned = comanda.getDateOpening().atTime(comanda.getTimeOpening());
        String durationText = null;

        if (ChronoUnit.DAYS.between(dateTimeOpenned, LocalDateTime.now()) > 1) {
            durationText = ChronoUnit.DAYS.between(dateTimeOpenned, LocalDateTime.now()) + " dias";
        } else if (ChronoUnit.HOURS.between(dateTimeOpenned, LocalDateTime.now()) > 1) {
            durationText = ChronoUnit.HOURS.between(dateTimeOpenned, LocalDateTime.now()) + " horas";
        } else if (ChronoUnit.MINUTES.between(dateTimeOpenned, LocalDateTime.now()) > 60) {
            durationText = "mais de uma hora";
        } else if (ChronoUnit.MINUTES.between(dateTimeOpenned, LocalDateTime.now()) < 1) {
            durationText = "menos de um minuto";
        } else {
            durationText = ChronoUnit.MINUTES.between(dateTimeOpenned, LocalDateTime.now()) + " minutos";
        }

        return durationText;
    }
}
