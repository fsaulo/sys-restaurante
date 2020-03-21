package org.sysRestaurante.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.text.NumberFormat;

public class PercentageField extends TextField {

    private NumberFormat format;
    private final SimpleDoubleProperty amount;

    public PercentageField() {
        this(0.00);
    }

    public PercentageField(Double initialAmount) {
        setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        amount = new SimpleDoubleProperty(this, "amount", initialAmount);
        format = NumberFormat.getPercentInstance();
        setText(format.format(initialAmount));

        focusedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                        Platform.runLater(() -> { int lenght = getText().length();
                            selectRange(lenght, lenght);
                            positionCaret(lenght);
                        }));

        setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                formatText(format.format(0));
                positionCaret(getText().length());
            }

            if (amount.getValue() == 0) {
                positionCaret(getText().length());
            }
        });

        textProperty().addListener((observable, oldValue, newValue) -> formatText(newValue));
    }

    private void formatText(String text) {
        if(text != null && !text.isEmpty()) {
            StringBuilder plainText = new StringBuilder(text.replaceAll("[^0-9]", ""));

            while(plainText.length() < 3) {
                plainText.insert(0, "0");
            }

            StringBuilder builder = new StringBuilder(plainText.toString());
            builder.insert(plainText.length() - 2, ".");

            double newValue = Double.parseDouble(builder.toString());

            if (newValue > 1) {
                newValue = 1;
            }

            amount.set(newValue);
            setText(format.format(newValue));
        }
    }

    public Double getAmount() {
        return amount.get();
    }

    @Override
    public void deleteText(int start, int end) {
        StringBuilder builder = new StringBuilder(getText());
        builder.delete(start, end);
        formatText(builder.toString());
        selectRange(start, start);
    }

    public void setAmount(double value) {
        if (value >= 0.0) {
            this.amount.setValue(value);
            setText(format.format(value));
        }
    }
}
