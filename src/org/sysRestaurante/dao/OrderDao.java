package org.sysRestaurante.dao;

import java.time.LocalDate;

public class OrderDao {

    private int idOrder;
    private int idUser;
    private int idCategory;
    private double byCard;
    private double inCash;
    private LocalDate orderDate;
    private String note;

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
