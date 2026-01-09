package org.sysRestaurante.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.util.BRLFormat;
import org.sysRestaurante.util.EscPos;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.sysRestaurante.gui.formatter.DateFormatter.DATE_FORMAT;

public class Receipt {

    public StringBuilder receipt;
    public String strSubtotal;
    public String strDiscount;
    public String strTotal;
    public final String strFuncName;
    public String strDate;
    public String strTime;
    public String strCompanyName = "Bar & Restaurante Frutos do Mar";;
    public String strCompanyAddress1 = "Av. C, Orlinha do São Brás, 27";
    public String strCompanyAddress2 = "Nossa Senhora do Socorro-SE";
    public String strCompanyTel = "CONTATO: (79) 99983-2971";
    public String strCompanyCNPJ = "CNPJ: 12.345.678/0001-00";
    public String strTaxes;
    public String strEmployeeName = "";
    public ArrayList<ProductDao> productList;
    public static final String NON_THIN = "[^iIl1\\.,']";
    public int length;
    static final int COL_ITEM  = 25;
    static final int COL_QTD   = 5;
    static final int COL_PRECO = 8;
    static final int COL_TOTAL = 10;

    public Receipt(OrderDao order, ArrayList<ProductDao> productList) {
        UserDao func = AppFactory.getUserDao();
        double subtotal = order.getTotal();
        double discount = order.getDiscount();
        double taxes = order.getTaxes();
        double total = subtotal - discount + taxes;
        this.productList = productList;
        strFuncName = func.getName();
        strDate = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(order.getOrderDate());
        strTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(order.getOrderTime());
        strSubtotal = CurrencyField.getBRLCurrencyFormat().format(subtotal);
        strTotal = CurrencyField.getBRLCurrencyFormat().format(total);
        strTaxes = CurrencyField.getBRLCurrencyFormat().format(taxes);
        strDiscount = CurrencyField.getBRLCurrencyFormat().format(discount);
        strEmployeeName = Personnel.getEmployeeNameById(((ComandaDao) order).getIdEmployee());
        buildReceipt();
    }

    public Receipt() {
        strCompanyName = "NOME DA EMPRESA";
        strCompanyAddress1 = "Av. Brasil, 2113";
        strCompanyAddress2 = "Aracaju-SE";
        strCompanyTel = "CONTATO: (44) 91214-5566";
        strCompanyCNPJ = "CNPJ: 00.000.000/0001-00";
        strSubtotal = "R$ 0,00";
        strDiscount = "0%";
        strTotal = "R$ 0,00";
        strFuncName = "";
        strDate = "01/01/2020";
        strTime = "00:00:00";
        strTaxes = "";
        buildReceipt();
    }

    private static String padRight(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return text + " ".repeat(width - text.length());
    }

    private static String padLeft(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return " ".repeat(width - text.length()) + text;
    }

    public static String leftRight(String left, String right, int totalCols) {
        int spaces = totalCols - left.length() - right.length();
        if (spaces < 1) spaces = 1;
        return left + " ".repeat(spaces) + right;
    }

    public byte[] buildReceiptForPrint(ComandaDao order, ArrayList<ProductDao> productList) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(EscPos.INIT);
        out.write(EscPos.CODEPAGE_CP860);

        out.write(EscPos.FONT_NORMAL);
        out.write(EscPos.alignCenter());
        out.write(EscPos.boldOn());
        out.write(EscPos.text(strCompanyName));
        out.write(EscPos.newLine());
        out.write(EscPos.boldOff());

        out.write(EscPos.text(strCompanyAddress1));
        out.write(EscPos.newLine());
        out.write(EscPos.text(strCompanyAddress2));
        out.write(EscPos.newLine());
        out.write(EscPos.text(strCompanyCNPJ));
        out.write(EscPos.newLine());
        out.write(EscPos.text(strCompanyTel));
        out.write(EscPos.newLine());

        out.write(EscPos.alignCenter());
        out.write(EscPos.horizontalLine('-', 48));
        out.write((EscPos.newLine()));
        out.write(EscPos.text("ESSE RECIBO NÃO É CUPOM FISCAL"));
        out.write(EscPos.newLine());
        out.write(EscPos.horizontalLine('-', 48));
        out.write(EscPos.newLine());

        String header = padRight("Item", COL_ITEM) +
                        padLeft("Qtd", COL_QTD) +
                        padLeft("Preço", COL_PRECO) +
                        padLeft("Total", COL_TOTAL);

        out.write(EscPos.alignLeft());
        out.write(EscPos.newLine());
        out.write(EscPos.text(header));
        out.write(EscPos.horizontalLine('-', 48));

        if (productList != null) {
            for (ProductDao product : productList) {
                String productStr = padRight(product.getDescription(), COL_ITEM) +
                        padLeft(String.valueOf(product.getQuantity()), COL_QTD) +
                        padLeft(BRLFormat.value(product.getSellPrice()), COL_PRECO) +
                        padLeft(BRLFormat.value(product.getSellPrice() * product.getQuantity()), COL_TOTAL);
                out.write(EscPos.text(productStr));
                out.write(EscPos.newLine());
            }
        }

        out.write(EscPos.newLine());

        String subtotal = padRight("Subtotal", 38) +
                padLeft(BRLFormat.value(order.getTotal()), 10);

        String discount = padRight("Descontos aplicados", 38) +
                padLeft(BRLFormat.value(order.getDiscount()), 10);

        String service = padRight("Taxa de serviço", 38) +
                padLeft(BRLFormat.value(order.getTaxes()), 10);

        double totalValue = order.getTotal() - order.getDiscount() + order.getTaxes();
        String total = padRight("Total", 12) +
                padLeft("R$ " + BRLFormat.value(totalValue), 12);

        out.write(EscPos.horizontalLine('=', 48));
        out.write(EscPos.text(subtotal));
        out.write(EscPos.newLine());
        out.write(EscPos.text(service));
        out.write(EscPos.newLine());
        out.write(EscPos.text(discount));
        out.write(EscPos.newLine());

        out.write(EscPos.FONT_DOUBLE);
        out.write(EscPos.newLine());
        out.write(EscPos.boldOn());
        out.write(EscPos.text(total));
        out.write(EscPos.boldOff());
        out.write(EscPos.FONT_NORMAL);
        out.write(EscPos.newLine());
        out.write(EscPos.horizontalLine('-', 48));
        out.write(EscPos.newLine());

        String tableOrderStr = leftRight(
                "Mesa: " + order.getIdTable(),
                "Pedido: #" + order.getIdOrder(),
                48
        );

        String funcDateStr = leftRight(
                "Funcionário: " + strEmployeeName,
                DATE_FORMAT.format(order.getOrderDateTime()),
                48
        );

        out.write(EscPos.text(tableOrderStr));
        out.write(EscPos.newLine());
        out.write(EscPos.text(funcDateStr));
        out.write(EscPos.newLine());

        out.write(EscPos.newLine());
        out.write(EscPos.alignCenter());
        out.write(EscPos.text("Obrigado pela preferência"));
        out.write(EscPos.newLine());
        out.write(EscPos.text("Volte sempre!"));
        out.write(EscPos.newLine());

        out.write(EscPos.feed(4));
        out.write(EscPos.CUT);
        return out.toByteArray();
    }

    public String getReceipt() {
        return receipt.toString();
    }

    public void buildReceipt() {
        receipt = new StringBuilder();
        receipt.append(getHeader());
        receipt.append(getBody());
        receipt.append(getFooter());
    }

    public String center(String string) {
        length = 50;
        char pad = ' ';
        StringBuilder sb = new StringBuilder(length);
        sb.setLength((length - string.length()) / 2);
        sb.append(string);
        sb.setLength(length);
        return sb.toString().replace('\0', pad);
    }

    private static int textWidth(String str) {
        return str.length() - str.replaceAll(NON_THIN, "").length() / 2;
    }

    public static String ellipsize(String text, int max) {
        if (textWidth(text) <= max)
            return text;
        int end = text.lastIndexOf(' ', max - 3);
        if (end == -1)
            return text.substring(0, max-3) + "..";
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);
            if (newEnd == -1) newEnd = text.length();
        } while (textWidth(text.substring(0, newEnd) + "..") < max);
        return text.substring(0, end) + "..";
    }

    private String getHeader() {
        String sep1 = "--------------------------------------------------";
        String msg1 = center("ESSE RECIBO NÃO É CUPOM FISCAL");
        return center(strCompanyName) + "\n" +
                        center(strCompanyAddress1) + "\n" +
                        center(strCompanyTel) + "\n" +
                        center(strCompanyCNPJ) + "\n\n" +
                        sep1 + "\n" +
                        msg1 + "\n" +
                        sep1 + "\n\n";
    }

    private String getBody() {
        String sep1 = "--------------------------------------------------";
        String strItem;
        String strQty;
        String strPrice;
        String title = String.format("%-28s %10s %10s\n", "Item", "Qtd", "Preço");
        StringBuilder body = new StringBuilder();
        body.append(title).append(sep1);
        body.append("\n");

        if (productList != null) {
            for (ProductDao item : productList) {
                strItem = ellipsize(item.getDescription(), 25);
                strQty = "x" + item.getQuantity();
                strPrice = CurrencyField.getBRLCurrencyFormat().format(item.getSellPrice());
                body.append(String.format("%-28s %10s %10s\n", strItem, strQty, strPrice));
            }
        }

        return body.toString();
    }

    public String getFooter() {
        String sep1 = "--------------------------------------------------";
        String sep2 = "==================================================";
        String subtotal = String.format("%-24s %25s", "SUBTOTAL:", strSubtotal);
        String total = String.format("%-24s %25s", "TOTAL:", strTotal);
        String discount = String.format("%-24s %25s", "DESCONTOS APLICADOS:", strDiscount);
        String taxes = String.format("%-24s %25s", "TAXA DE SERVICO: ", strTaxes);
        String msg1 = center("OBRIGADO PELA PREFERÊNCIA.");
        String msg2 = center("VOLTE SEMPRE!");
        String dateTime = String.format("%-24s %25s", "DATA: " + strDate, "HORA: " + strTime);
        return "\n\n" + sep2 + "\n" +
                        subtotal + "\n" +
                        discount + "\n" +
                        taxes + "\n" +
                        total + "\n" +
                        sep1 + "\n" +
                        "NOME FUNC. " + strFuncName + "\n" +
                        dateTime + "\n" +
                        sep2 + "\n\n" +
                        msg1 + "\n" +
                        msg2;
    }

    public Node getReceiptAsNode() {
        Text rpp = new Text(getReceipt());
        rpp.setFont(Font.font("Consolas", FontWeight.THIN, 13));
        TextFlow receipt = new TextFlow();
        receipt.getChildren().add(rpp);
        receipt.setTextAlignment(TextAlignment.JUSTIFY);
        receipt.setMinWidth(calculateMinWidth(receipt));
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(3));
        stackPane.getChildren().add(receipt);
        return stackPane;
    }

    private double calculateMinWidth(TextFlow textFlow) {
        double minWidth = 0;
        for (javafx.scene.Node node : textFlow.getChildren()) {
            if (node instanceof Text) {
                Text text = (Text) node;
                minWidth += text.getLayoutBounds().getWidth();
            }
        }
        return minWidth;
    }

    public WritableImage getReceiptImageFile() {
        return getReceiptAsNode().snapshot(new SnapshotParameters(), null);
    }

    public void saveReceiptAsPng(Window owner) throws MalformedURLException {
        WritableImage nodeshot = getReceiptImageFile();
        int idOrder = AppFactory.getOrderDao().getIdOrder();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("SysRecibo_Caixa_Cod" + idOrder + "_" + LocalDate.now() + ".png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        fileChooser.setTitle("Salvar recibo");
        File file = fileChooser.showSaveDialog(owner);

        if (file != null) {
            if(!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".png");
            } else {
                if (!file.getName().endsWith(".png")) {
                    throw new MalformedURLException(file.getName() + " has no valid file-extension.");
                }
            }

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(nodeshot, null), "png", file);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informação do sistema");
                alert.setHeaderText(null);
                alert.setContentText("Recibo salvo com sucesso");
                alert.initOwner(owner);
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.addSuppressed(new Throwable());
            }
        }
    }

    public static class ThinReceipt extends Receipt {
        final ArrayList<ProductDao> productList;
        StringBuilder rc;

        public ThinReceipt(OrderDao order, ArrayList<ProductDao> pdL) {
            this.productList = pdL;
            double subtotal = order.getTotal();
            double discount = order.getDiscount();
            double taxes = order.getTaxes();
            double total = subtotal - discount + taxes;
            strCompanyName = "NOME DA EMPRESA";
            strCompanyAddress1 = "Av. Brasil, 2113, Aracaju-SE";
            strCompanyTel = "CONTATO: (44) 91214-5566";
            strCompanyCNPJ = "CNPJ: 00.000.000/0001-00";
            strDate = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(order.getOrderDate());
            strTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(order.getOrderTime());
            strSubtotal = CurrencyField.getBRLCurrencyFormat().format(subtotal);
            strTotal = CurrencyField.getBRLCurrencyFormat().format(total);
            strTaxes = CurrencyField.getBRLCurrencyFormat().format(taxes);
            strDiscount = CurrencyField.getBRLCurrencyFormat().format(discount);
            buildReceipt();
        }

        public String center(String string) {
            length = 35;
            char pad = ' ';
            StringBuilder sb = new StringBuilder(length);
            sb.setLength((length - string.length()) / 2);
            sb.append(string);
            sb.setLength(length);
            return sb.toString().replace('\0', pad);
        }

        private String getHeader() {
            String sep1 = "----------------------------------";
            String msg1 = center("ESSE RECIBO NÃO É CUPOM FISCAL");
            return center(strCompanyName) + "\n" +
                    center(strCompanyAddress1) + "\n" +
                    center(strCompanyTel) + "\n" +
                    center(strCompanyCNPJ) + "\n\n" +
                    sep1 + "\n" +
                    msg1 + "\n" +
                    sep1 + "\n\n";
        }

        private String getBody() {
            String sep1 = "----------------------------------";
            String strItem;
            String strQty;
            String strPrice;
            String title = String.format("%-15s %8s %8s\n", "Item", "Qtd", "Preço");
            StringBuilder body = new StringBuilder();
            body.append(title).append(sep1);
            body.append("\n");

            if (productList != null) {
                for (ProductDao item : productList) {
                    strItem = ellipsize(item.getDescription(), 15);
                    strQty = "x" + item.getQuantity();
                    strPrice = CurrencyField.getBRLCurrencyFormat().format(item.getSellPrice());
                    body.append(String.format("%-15s %8s %8s\n", strItem, strQty, strPrice));
                }
            }

            return body.toString();
        }

        public String getFooter() {
            String sep1 = "----------------------------------";
            String sep2 = "==================================";
            String subtotal = String.format("%-15s %17s", "SUBTOTAL:", strSubtotal);
            String total = String.format("%-15s %17s", "TOTAL:", strTotal);
            String msg1 = center("OBRIGADO PELA PREFERÊNCIA.");
            String msg2 = center("VOLTE SEMPRE!");
            String dateTime = String.format("%-18s %10s", "DATA: " + strDate, "HORA: " + strTime);
            return "\n\n" + sep2 + "\n" +
                    subtotal + "\n" +
                    total + "\n" +
                    sep1 + "\n" +
                    "NOME FUNC. " + strFuncName + "\n" +
                    dateTime + "\n" +
                    sep2 + "\n\n" +
                    msg1 + "\n";
        }

        public void buildReceipt() {
            rc = new StringBuilder();
            rc.append(getHeader());
            rc.append(getBody());
            rc.append(getFooter());
        }

        public String getReceipt() {
            return rc.toString();
        }
    }
}
