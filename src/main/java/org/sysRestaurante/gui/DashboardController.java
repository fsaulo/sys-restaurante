package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.NoteDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Reminder;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public void initialize() {
        AppFactory.setDashboardController(this);
        borderPane.setBottom(AppFactory.getAppController().getFooter());
        borderPane.setTop(AppFactory.getAppController().getHeader());
        clearNotesButton.setOnMouseClicked(e -> showClearAlertWindow());
        reloadNotes();
        updateCashierStatus();
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
}
