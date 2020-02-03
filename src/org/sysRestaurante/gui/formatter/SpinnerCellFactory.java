package org.sysRestaurante.gui.formatter;

import javafx.application.Platform;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import org.sysRestaurante.dao.ProductDao;

public class SpinnerCellFactory extends TableCell<ProductDao, Integer> {
    private final Spinner<Integer> spinner;

    public SpinnerCellFactory(int min, int max, int initial, int step) {
        spinner = new Spinner<>();
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initial, step));
        spinner.setMaxWidth(60);
        spinner.setMaxHeight(15);
        spinner.setOpacity(0.80);
        setEditable(true);
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            spinner.getValueFactory().setValue(getItem());

            setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    Platform.runLater(() -> commitEdit(spinner.getValue()));
                }
            });

            focusedProperty().addListener((observable, old, newValue) -> {
                if (newValue) return;
                commitEdit(spinner.getValue());
            });

            setText(null);
            setGraphic(spinner);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(getItem().toString());
        setGraphic(null);
    }

    @Override
    public void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                setText(null);
                setGraphic(spinner);
            } else {
                setText(getItem().toString());
                setGraphic(null);
            }
        }
    }

    public int getValue() {
        return spinner.getValue();
    }
}