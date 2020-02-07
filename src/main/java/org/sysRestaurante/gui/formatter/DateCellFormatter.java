package org.sysRestaurante.gui.formatter;

import javafx.scene.control.TableCell;
import org.sysRestaurante.dao.OrderDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateCellFormatter extends TableCell<OrderDao, LocalDate> {

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(DateTimeFormatter.ofPattern("dd-MM-yyyy").format(item));
        }
    }
}

