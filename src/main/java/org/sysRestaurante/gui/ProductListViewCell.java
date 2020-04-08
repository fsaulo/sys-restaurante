package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CurrencyField;

import java.io.IOException;
import java.text.NumberFormat;

public class ProductListViewCell extends ListCell<ProductDao> {

    @FXML
    private Label description;
    @FXML
    private Label price;
    @FXML
    private Label id;
    @FXML
    private Label category;
    @FXML
    private HBox wrapperBox;
    @FXML
    private Separator sep;

    private FXMLLoader mLLoader;
    private boolean setLabel = true;

    public ProductListViewCell() {
    }

    public ProductListViewCell(boolean noLabel) {
        this.setLabel = noLabel;
    }

    @Override
    protected void updateItem(ProductDao product, boolean empty) {
        super.updateItem(product, empty);

        if (product == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource(SceneNavigator.PRODUCT_LIST_CELL));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            NumberFormat brlCurrencyFormat = CurrencyField.getBRLCurrencyFormat();
            description.setText(product.getDescription());
            price.setText(brlCurrencyFormat.format(product.getSellPrice()));

            if (setLabel) {
                description.setTooltip(new Tooltip(product.getDescription()));
                id.setText(String.valueOf(product.getIdProduct()));
                category.setText(product.getCategory());
                category.setOnMouseClicked(event -> AppFactory.getPos().searchByCategory(category.getText()));
                wrapperBox.setOnMouseClicked(event -> {
                    if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        if (product != null) {
                            AppFactory.getPos().addToSelectedProductsList(product);
                        } else {
                            event.consume();
                        }
                    }
                });
            } else {
                wrapperBox.getChildren().removeAll(category, id, sep);
            }

            setText(null);
            setGraphic(wrapperBox);
        }
    }
}
