package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.sqlite.SQLiteErrorCode;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.TableDao;
import org.sysRestaurante.model.Management;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.sql.SQLException;

public class RegisterTableController {

    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button removeButton;
    @FXML
    private ListView<TableDao> tableListView;
    @FXML
    private VBox fieldVBox;

    private TextField tableCodTextField;
    private final ObservableList<TableDao> tables = FXCollections.observableArrayList(Management.getTables());

    @FXML
    public void initialize() {
        tableListView.setItems(tables);
        tableListView.setCellFactory(tlv -> new TableListViewCell());

        tableCodTextField = new TextField();
        tableCodTextField.setPrefHeight(35);
        tableCodTextField.setPromptText("Digite o código da mesa");
        fieldVBox.getChildren().add(tableCodTextField);
        Platform.runLater(() -> {
            fieldVBox.requestFocus();
            tableListView.focusModelProperty().addListener(event -> {
                if (tableListView.getSelectionModel().isEmpty()) {
                    removeButton.setDisable(true);
                } else removeButton.setDisable(false);
            });
        });
        confirmButton.setOnMouseClicked(event -> onNewTableClicked());
        cancelButton.setOnMouseClicked(event -> fieldVBox.getScene().getWindow().hide());
    }

    public void onNewTableClicked() {
        Window owner = AppFactory.getMainController().getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alerta do sistema");
        try {
            if (!tableCodTextField.getText().isEmpty()) {
                int cod = Integer.parseInt(tableCodTextField.getText());
                try {
                    Management.newTable(cod);
                    fieldVBox.getScene().getWindow().hide();
                    NotificationHandler.showInfo("Mesa adicionada com sucesso!");
                    AppFactory.getManageComandaController().refreshTileList();
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
                        alert.setHeaderText("Não foi possível adicionar mesa");
                        alert.setContentText("Já existe uma mesa com o código fornecido!");
                        alert.initOwner(owner);
                        alert.showAndWait();
                    } else {
                        ExceptionHandler.incrementGlobalExceptionsCount();
                        ex.printStackTrace();
                    }
                }
            }
        } catch (NumberFormatException  ex) {
            alert.setHeaderText("Não foi possível adicionar mesa");
            alert.setContentText("O código da mesa deve ser um número!");
            alert.initOwner(owner);
            alert.showAndWait();
        }
    }
}
