package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
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

            availability.getStylesheets().add("css/menu.css");
            if (table.getIdStatus() != 1) {
                wrapperBox.setOpacity(0.4);
                wrapperBox.setDisable(true);
                availability.setText("Ocupada/Reservada");
                availability.getStyleClass().add("availability-label-red");
            } else {
                availability.setTooltip(new Tooltip("Mesa disponível"));
                availability.getStyleClass().add("availability-label-green");
                availability.setText("Disponível");
            }

            codTable.setText("MESA #" + table.getIdTable());

            setText(null);
            setGraphic(wrapperBox);
        }
    }
}
