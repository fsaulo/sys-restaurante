package org.sysRestaurante.dao;

import java.time.LocalDate;
import java.time.LocalTime;

public class CashierDao {

    private int idCashier;
    private int idUser;
    private double revenue;
    private LocalDate dateOpening;
    private LocalDate dateClosing;
    private LocalTime timeOpening;
    private LocalTime timeClosing;
    private boolean isOpenned;

    public CashierDao() {
    }

    public CashierDao(int idCashier) {
        this.idCashier = idCashier;
    }

    public int getIdCashier() {
        return idCashier;
    }

    public void setIdCashier(int idCashier) {
        this.idCashier = idCashier;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public LocalDate getDateOpening() {
        return dateOpening;
    }

    public void setDateOpening(LocalDate dateOpening) {
        this.dateOpening = dateOpening;
    }

    public LocalDate getDateClosing() {
        return dateClosing;
    }

    public void setDateClosing(LocalDate dateClosing) {
        this.dateClosing = dateClosing;
    }

    public LocalTime getTimeOpening() {
        return timeOpening;
    }

    public void setTimeOpening(LocalTime timeOpening) {
        this.timeOpening = timeOpening;
    }

    public LocalTime getTimeClosing() {
        return timeClosing;
    }

    public void setTimeClosing(LocalTime timeClosing) {
        this.timeClosing = timeClosing;
    }

    public boolean isOpenned() {
        return isOpenned;
    }

    public void setOpenned(boolean openned) {
        isOpenned = openned;
    }
}
