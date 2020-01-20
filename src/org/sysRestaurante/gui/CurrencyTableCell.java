package org.sysRestaurante.gui;


import javafx.scene.control.TableCell;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.util.CurrencyField;

public class CurrencyTableCell extends TableCell<ProductDao, Double> {

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(CurrencyField.getBRLCurrencyFormat().format(item));
        }
    }
}

