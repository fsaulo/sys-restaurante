package org.sysRestaurante.dao;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessionDao {
    private int idSsession;
    private int idCashier;
    private int idLastSession;
    private int idUser;
    private int busyTablesCount = 0;
    private int availableTablesCount = 0;
    private int averagePermanencyInMinutes;
    private double totalByCard = 0;
    private double totalByCash = 0;
    private double totalWithdrawals = 0;
    private double totalComandaIncome = 0;
    private LocalDate dateSession;
    private LocalTime timeSession;
    private long sessionDuration;
    private String businessName;
    private String businessPhone;
    private String businessCNPJ;
    private String businessAddress;

    public int getAveragePermanencyInMinutes() {
        return averagePermanencyInMinutes;
    }

    public void setAveragePermanencyInMinutes(int averagePermanencyInMinutes) {
        this.averagePermanencyInMinutes = averagePermanencyInMinutes;
    }

    public int getIdCashier() {
        return idCashier;
    }

    public void setIdCashier(int idCashier) {
        this.idCashier = idCashier;
    }

    public int getIdSsession() {
        return idSsession;
    }

    public void setIdSsession(int idSsession) {
        this.idSsession = idSsession;
    }

    public int getIdLastSession() {
        return idLastSession;
    }

    public void setIdLastSession(int idLastSession) {
        this.idLastSession = idLastSession;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getBusyTablesCount() {
        return busyTablesCount;
    }

    public void setBusyTablesCount(int busyTablesCount) {
        this.busyTablesCount = busyTablesCount;
    }

    public int getAvailableTablesCount() {
        return availableTablesCount;
    }

    public void setAvailableTablesCount(int availableTablesCount) {
        this.availableTablesCount = availableTablesCount;
    }

    public double getTotalByCard() {
        return totalByCard;
    }

    public void setTotalByCard(double totalByCard) {
        this.totalByCard = totalByCard;
    }

    public double getTotalByCash() {
        return totalByCash;
    }

    public void setTotalByCash(double totalByCash) {
        this.totalByCash = totalByCash;
    }

    public double getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(double totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public double getTotalComandaIncome() {
        return totalComandaIncome;
    }

    public void setTotalComandaIncome(double totalComandaIncome) {
        this.totalComandaIncome = totalComandaIncome;
    }

    public LocalDate getDateSession() {
        return dateSession;
    }

    public void setDateSession(LocalDate dateSession) {
        this.dateSession = dateSession;
    }

    public LocalTime getTimeSession() {
        return timeSession;
    }

    public void setTimeSession(LocalTime timeSession) {
        this.timeSession = timeSession;
    }

    public long getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(long sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessCNPJ() {
        return businessCNPJ;
    }

    public void setBusinessCNPJ(String businessCNPJ) {
        this.businessCNPJ = businessCNPJ;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }
}
