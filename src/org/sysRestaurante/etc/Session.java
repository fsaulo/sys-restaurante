package org.sysRestaurante.etc;

import java.time.LocalDate;
import java.time.LocalTime;

public class Session {
    private int idSsession;
    private int idLastSession;
    private int idUser;
    private LocalDate dateSession;
    private LocalTime timeSession;
    private long sessionDuration;

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

    public int getIdSsession() {
        return idSsession;
    }

    public void setIdSsession(int idSsession) {
        this.idSsession = idSsession;
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
}
