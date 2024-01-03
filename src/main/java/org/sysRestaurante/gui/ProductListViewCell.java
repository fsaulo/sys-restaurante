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
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.NotificationHandler;

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
                category.setText(product.getCategoryDao().getCategoryDescription());
                category.setOnMouseClicked(event -> {
                    try {
                        AppFactory.getPos().searchByCategory(category.getText());
                    } catch (NullPointerException ignored) {
                        ExceptionHandler.doNothing();
                    }

                    try {
                        AppFactory.getProductManagementController().searchByCategory(category.getText());
                    } catch (NullPointerException ignored1) {
                        ExceptionHandler.doNothing();
                    }
                });
                wrapperBox.setOnMouseClicked(event -> {
                    if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        try {
                            AppFactory.getPos().addToSelectedProductsList(product);
                            int productType = product.getCategoryDao().getIdCategory();
                            if (product.isMenuItem() || productType == ProductDao.CategoryDao.Type.LUNCH.getValue()
                                    || productType == ProductDao.CategoryDao.Type.TASTE.getValue()
                                    || productType == ProductDao.CategoryDao.Type.EXTRA_PORTION.getValue()) {
                                int idOrder = Order.newKitchenOrder(
                                        AppFactory.getComandaDao().getIdComanda(),
                                        1,
                                        "Sem observações"
                                );
                                if (idOrder > 0) {
                                    Order.addProductToKitchenOrder(idOrder, product);
                                    NotificationHandler.showInfo("Pedido enviado para cozinha");
                                } else {
                                    NotificationHandler.showInfo("Não foi possível registrar o pedido na cozinha");
                                }
                            }
                        } catch (NullPointerException ignored) {
                            ExceptionHandler.doNothing();
                        }
                    }
                });
            } else {
                description.setText(product.getQuantity() + " x " + product.getDescription());
                price.setText(brlCurrencyFormat.format(product.getSellPrice()*product.getQuantity()));
                wrapperBox.getChildren().removeAll(category, id, sep);
            }

            setText(null);
            setGraphic(wrapperBox);
        }
    }
}
