package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import org.sysRestaurante.dao.TableDao;
import org.sysRestaurante.model.Order;

public class NewComandaDialogController {

    @FXML
    private ListView<TableDao> tableListView;

    private final ObservableList<TableDao> tables = FXCollections.observableArrayList(new Order().getTables());

    public void initialize() {
        tableListView.setItems(tables);
        tableListView.setCellFactory(tlv -> new TableListViewCell());
    }
}
