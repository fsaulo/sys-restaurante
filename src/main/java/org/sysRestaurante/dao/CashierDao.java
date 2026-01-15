package org.sysRestaurante.dao;

import org.sysRestaurante.util.ExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CashierDao {

    private int idCashier;
    private int idUser;
    private double revenue;
    private double inCash;
    private double byCard;
    private double withdrawal;
    private double initialAmount;
    private boolean isOpenned;
    private String note;
    private LocalDate dateOpening;
    private LocalDate dateClosing;
    private LocalTime timeOpening;
    private LocalTime timeClosing;
    private LocalDateTime dateTimeOpening;
    private LocalDateTime dateTimeClosing;

    public CashierDao() { }

    public void configDateTimeEvent() {
        try {
            dateTimeClosing = dateClosing.atTime(timeClosing);
            dateTimeOpening = dateOpening.atTime(timeOpening);
        } catch (Exception exception) {
            ExceptionHandler.doNothing();
        }
    }

    public LocalDateTime getDateTimeOpening() {
        return dateTimeOpening;
    }

    public LocalDateTime getDateTimeClosing() {
        return dateTimeClosing;
    }

    public CashierDao(int idCashier) {
        this.idCashier = idCashier;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public double getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(double withdrawal) {
        this.withdrawal = withdrawal;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getInCash() {
        return inCash;
    }

    public void setInCash(double inCash) {
        this.inCash = inCash;
    }

    public double getByCard() {
        return byCard;
    }

    public void setByCard(double byCard) {
        this.byCard = byCard;
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

    public void setDateTimeOpening(LocalDateTime dateTimeOpening) {
        this.dateTimeOpening = dateTimeOpening;
    }

    public void setDateTimeClosing(LocalDateTime dateTimeClosing) {
        this.dateTimeClosing = dateTimeClosing;
    }
}
