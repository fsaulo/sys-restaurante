package org.sysRestaurante.dao;

import java.time.LocalDate;
import java.time.LocalTime;

public class ComandaDao extends OrderDao {
    private int idComanda;
    private int idTable;
    private int idEmployee;
    private LocalTime timeOpening;
    private LocalDate dateOpening;
    private LocalTime timeClosing;
    private LocalDate dateClosing;

    public LocalTime getTimeOpening() {
        return timeOpening;
    }

    public void setTimeOpening(LocalTime timeOpening) {
        this.timeOpening = timeOpening;
    }

    public LocalDate getDateOpening() {
        return dateOpening;
    }

    public void setDateOpening(LocalDate dateOpening) {
        this.dateOpening = dateOpening;
    }

    public LocalTime getTimeClosing() {
        return timeClosing;
    }

    public void setTimeClosing(LocalTime timeClosing) {
        this.timeClosing = timeClosing;
    }

    public LocalDate getDateClosing() {
        return dateClosing;
    }

    public void setDateClosing(LocalDate dateClosing) {
        this.dateClosing = dateClosing;
    }

    public int getIdComanda() {
        return idComanda;
    }

    public void setIdComanda(int idComanda) {
        this.idComanda = idComanda;
    }

    public int getIdTable() {
        return idTable;
    }

    public void setIdTable(int idTable) {
        this.idTable = idTable;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }
}
