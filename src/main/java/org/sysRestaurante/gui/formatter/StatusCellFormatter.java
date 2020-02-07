package org.sysRestaurante.gui.formatter;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import org.sysRestaurante.dao.OrderDao;

public class StatusCellFormatter extends TableCell<OrderDao, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            switch (item) {
                case "Concluído":
                    setTextFill(Color.GREEN);
                    break;
                case "Cancelado":
                    setTextFill(Color.DARKRED);
                    break;
                case "Aguardando pagamento":
                    setTextFill(Color.DARKORANGE);
                    break;
                default:
                    setTextFill(Color.GRAY);
                    break;
            }
            setText(item);
        }
    }
}
