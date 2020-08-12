package org.sysRestaurante.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class OrderDao {

    private int idOrder;
    private int idUser;
    private int idCategory;
    private double byCard;
    private double inCash;
    private double total = byCard + inCash;
    private double discount;
    private double taxes;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private LocalDateTime orderDateTime;
    private String details;
    private String note;
    private String status;
    private String customerName;

    public void setDetails(int idCategory) {
        switch (idCategory) {
            case 1:
                details = "Pedido no caixa";
                break;
            case 2:
                details = "Pedido em comanda";
                break;
            case 3:
                details = "Avariado";
                break;
            case 4:
                details = "Retirada";
                break;
            case 5:
                details = "Doação";
                break;
            case 6:
                details = "Sem retorno";
                break;
            default:
                details = "Sem categoria";
        }
    }

    public void setStatus(int idStatus) {
        switch (idStatus) {
            case 1:
                status = "Concluído";
                break;
            case 2:
                status = "Aguardando pagamento";
                break;
            case 3:
                status = "Cancelado";
                break;
            case 4:
                status = "Aguardando preparo";
                break;
            case 5:
                status = "Pedido pronto";
                break;
            case 6:
                status = "Produto avariado";
                break;
            default:
                status = "Desconhecido";
                break;
        }
    }

    public double getTaxes() {
        return taxes;
    }

    public void setTaxes(double taxes) {
        this.taxes = taxes;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String custumerName) {
        this.customerName = custumerName;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public LocalTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalTime orderTime) {
        this.orderTime = orderTime;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return  status;
    }

    public String getDetails() {
        return details;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public double getByCard() {
        return byCard;
    }

    public void setByCard(double byCard) {
        this.byCard = byCard;
    }

    public double getInCash() {
        return inCash;
    }

    public void setInCash(double inCash) {
        this.inCash = inCash;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
