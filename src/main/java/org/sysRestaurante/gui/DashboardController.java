package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.NoteDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Reminder;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DashboardController {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DashboardController.class.getName());
    private ArrayList<NoteDao> notesList;

    @FXML
    private Button clearNotesButton;
    @FXML
    private Button addNoteButton;
    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox notesPane;
    @FXML
    private VBox statusCashierBox;
    @FXML
    private Label statusCashierLabel;
    @FXML
    private LineChart<String, Number> lineChart;

    public void initialize() {
        AppFactory.setDashboardController(this);
        borderPane.setBottom(AppFactory.getAppController().getFooter());
        borderPane.setTop(AppFactory.getAppController().getHeader());
        clearNotesButton.setOnMouseClicked(e -> showClearAlertWindow());
        reloadNotes();
        updateCashierStatus();
        buildChart();
        Platform.runLater(this::handleKeyEvent);

        try {
            handleAddNotesMenu();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reloadNotes() {
        removeNotesFromList();
        updateNotesList();
        for (NoteDao item : notesList) {
            CheckBox box = new CheckBox(item.getContent());
            box.setWrapText(true);
            box.setOnMouseClicked(e -> {
                if (box.isSelected())
                    new Reminder().check(item.getIdNote());
                else
                    new Reminder().uncheck(item.getIdNote());
            });
            if (item.isChecked())
                box.setSelected(true);
            notesPane.getChildren().add(box);
        }
    }

    public void handleKeyEvent() {
        AppFactory.getMainController().getScene().getAccelerators().clear();
        AppFactory.getAppController().setFullScreenShortcut();
        statusCashierLabel.requestFocus();
    }

    public void handleAddNotesMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.NOTE_PANE));
        PopOver popOver = new PopOver(loader.load());
        addNoteButton.setOnMouseClicked(e1 -> popOver.show(addNoteButton));
        popOver.arrowLocationProperty().setValue(PopOver.ArrowLocation.BOTTOM_RIGHT);
    }

    public void showClearAlertWindow() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação do sistema");
        alert.setHeaderText("Deseja remover todas as anotações marcadas?");
        alert.setContentText("Essa ação não poderá ser desfeita");
        alert.initOwner(borderPane.getScene().getWindow());
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            try {
                new Reminder().removeChecked();
                removeNotesFromList();
                reloadNotes();
            } catch (SQLException e) {
                LOGGER.severe("Couldn't remove checked notes");
                e.printStackTrace();
            }
        }
    }

    public void removeNotesFromList() {
        if (notesList != null) notesList.clear();
        notesPane.getChildren().removeAll(notesPane.getChildren());
    }

    public void updateNotesList() {
        try {
            notesList = new Reminder().getAllPermanentNotes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCashierStatus() {
        boolean isCashierOpenned = Cashier.isOpen();
        statusCashierBox.getChildren().removeAll(statusCashierBox.getChildren());
        Label message = new Label();
        message.setStyle("-fx-font-family: carlito; " +
                "-fx-font-size: 15; " +
                "-fx-text-fill: white; " +
                "-fx-font-style: italic");
        String date = DateFormatter
                .TIME_DETAILS_FORMAT
                .format(Cashier.getCashierDateTimeDetailsById(AppFactory.getCashierDao().getIdCashier()));

        if (isCashierOpenned) {
            statusCashierLabel.setText("CAIXA LIVRE");
            statusCashierBox.setStyle("-fx-background-color: #58996A; -fx-background-radius: 5");
            statusCashierBox.getChildren().add(statusCashierLabel);
        } else {
            message.setText("Fechado em " + date);
            statusCashierLabel.setText("CAIXA FECHADO");
            statusCashierBox.setStyle("-fx-background-color: #bababa; -fx-background-radius: 5");
            statusCashierBox.getChildren().addAll(statusCashierLabel, message);
        }
    }

    public void addNoteToList(NoteDao noteDao) {
        notesList.add(noteDao);
    }

    public void buildChart() {
        XYChart.Series series = new XYChart.Series();
        final int listSize = 15;

        List<CashierDao> data = Cashier.getCashier();
        List<CashierDao> subListData = new ArrayList<>();
        CashierDao previousElement = data.get(0);
        double totalRevenue = previousElement.getRevenue();

        for (int i = 1; i < data.size(); i++) {
            CashierDao nextElement = data.get(i);

            try {
                if (previousElement.getDateClosing().isEqual(nextElement.getDateClosing())) {
                    totalRevenue += nextElement.getRevenue();
                } else {
                    totalRevenue = nextElement.getRevenue();
                    CashierDao newElement = previousElement;
                    newElement.setRevenue(totalRevenue);
                    newElement.setDateClosing(nextElement.getDateClosing());
                    newElement.setTimeClosing(nextElement.getTimeClosing());
                    newElement.setDateOpening(nextElement.getDateOpening());
                    newElement.setTimeOpening(nextElement.getTimeOpening());
                    subListData.add(newElement);
                }
            } catch (Exception ex) {
                ExceptionHandler.doNothing();
            }

            previousElement = nextElement;
        }

        if (subListData.size() < listSize) {
            data = subListData;
        } else {
            data = subListData.subList(subListData.size() - listSize, subListData.size() - 1);
        }

        for (CashierDao value : data) {
            String dateString;
            String dateString2;

            try {
                dateString = DateTimeFormatter.ofPattern("d MMM").format(value.getDateClosing());
                dateString2 = DateTimeFormatter.ofPattern("d MMM uuuu").format(value.getDateClosing());
            } catch (Exception ex) {
                dateString = DateTimeFormatter.ofPattern("d MMM").format(value.getDateOpening());
                dateString2 = DateTimeFormatter.ofPattern("d MMM uuuu").format(value.getDateClosing());
            }

            final XYChart.Data<String, Number> d1 = new XYChart.Data(dateString, value.getRevenue());

            StackPane stackPane = new StackPane();
            VBox box = new VBox();
            box.setPadding(new Insets(15));

            Label label1 = new Label(CurrencyField.getBRLCurrencyFormat().format(value.getRevenue()));
            Label label2 = new Label(dateString2);

            label1.setStyle("-fx-font-weight: bold; -fx-font-size: 13");
            label2.setStyle("-fx-font-size: 13");
            box.getChildren().addAll(label1, label2);
            box.setCursor(Cursor.HAND);
            box.setOnMouseClicked(mouseEvent -> openHistoryPane(mouseEvent, value));

            PopOver legend = new PopOver(box);

            box.setOnMouseExited(mouseEvent -> {
                stackPane.setCursor(Cursor.DEFAULT);
                legend.hide();
            });

            legend.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            legend.setDetachable(false);

            stackPane.setOnMouseEntered(mouseDragEvent -> {
                legend.show(stackPane);
                stackPane.setCursor(Cursor.HAND);
            });

            d1.setNode(stackPane);
            series.getData().add(d1);
        }

        lineChart.getData().add(series);
    }

    public void openHistoryPane(Event mouseEvent, CashierDao cashier) {
        AppFactory.getToolBarController().submenuHistoricoCaixa(mouseEvent);
        AppFactory.getCashierHistoryController().buildTableOrderDetails(cashier);
    }
}
