package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.sysRestaurante.dao.TableDao;
import org.sysRestaurante.model.Order;

public class RegisterTableController {

    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ListView<TableDao> tableListView;
    @FXML
    private VBox fieldVBox;

    private final ObservableList<TableDao> tables = FXCollections.observableArrayList(Order.getTables());

    @FXML
    public void initialize() {
        tableListView.setItems(tables);
        tableListView.setCellFactory(tlv -> new TableListViewCell());
        TextField tableCodTextField = new TextField();
        tableCodTextField.setPrefHeight(35);
        tableCodTextField.setPromptText("Digite o código da mesa");
        fieldVBox.getChildren().add(tableCodTextField);
        Platform.runLater(() -> fieldVBox.requestFocus());
    }
}
