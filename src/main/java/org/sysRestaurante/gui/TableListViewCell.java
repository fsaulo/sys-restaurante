package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import org.sysRestaurante.dao.TableDao;

import java.io.IOException;

public class TableListViewCell extends ListCell<TableDao> {

    @FXML
    private Label codTable;
    @FXML
    private Label id;
    @FXML
    private Label availability;
    @FXML
    private HBox wrapperBox;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(TableDao table, boolean empty) {
        super.updateItem(table, empty);

        if (table == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource(SceneNavigator.TABLE_LIST_CELL));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            codTable.setText("MESA #" + table.getIdTable());
            availability.setText("Disponível");

            setText(null);
            setGraphic(wrapperBox);
        }
    }
}
