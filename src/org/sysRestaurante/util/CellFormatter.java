package org.sysRestaurante.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public interface CellFormatter <E, T> extends Callback<TableColumn<E, T>, TableCell<E, T>> {

    @Override
    default TableCell<E, T> call(TableColumn<E, T> param) {
        return new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(convert(item));
                }
            }
        };
    }
    String convert(T value);
}