package org.sysRestaurante.gui.formatter;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyField extends TextField {

    private NumberFormat format;
    private final SimpleDoubleProperty amount;
    private NodeOrientation orientation = NodeOrientation.RIGHT_TO_LEFT;

    public CurrencyField(Locale locale) {
        this(locale, 0.00);
    }

    public CurrencyField(Locale locale, NodeOrientation orientation) {
        this(locale, 0.00);
        setNodeOrientation(orientation);
    }

    public CurrencyField(Locale locale, Double initialAmount) {
        setNodeOrientation(orientation);
        amount = new SimpleDoubleProperty(this, "amount", initialAmount);
        format = NumberFormat.getCurrencyInstance(locale);
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

    public Double getAmount() {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty() {
        return this.amount;
    }

    public void setAmount(Double newAmount) {
        if(newAmount >= 0.0) {
            amount.set(newAmount);
            formatText(format.format(newAmount));
        }
    }

    public void setCurrencyFormat(Locale locale) {
        format = NumberFormat.getCurrencyInstance(locale);
        formatText(format.format(getAmount()));
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
            amount.set(newValue);
            setText(format.format(newValue));
        }
    }

    @Override
    public void deleteText(int start, int end) {
        StringBuilder builder = new StringBuilder(getText());
        builder.delete(start, end);
        formatText(builder.toString());
        selectRange(start, start);
    }

    public static NumberFormat getBRLCurrencyFormat() {
        Currency brl = Currency.getInstance("BRL");
        NumberFormat brlCurrencyFormat = NumberFormat.getCurrencyInstance();
        brlCurrencyFormat.setCurrency(brl);
        brlCurrencyFormat.setMaximumFractionDigits(brl.getDefaultFractionDigits());
        return brlCurrencyFormat;
    }

    public void setOrientation(NodeOrientation orientation) {
        this.orientation = orientation;
    }
}