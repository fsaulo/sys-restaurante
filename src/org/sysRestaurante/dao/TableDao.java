package org.sysRestaurante.dao;

public class TableDao {
    private int idTable;
    private boolean busy;

    public int getIdTable() {
        return idTable;
    }

    public void setIdTable(int idTable) {
        this.idTable = idTable;
    }

    public boolean isStatus() {
        return busy;
    }

    public void setStatus(boolean status) {
        this.busy = status;
    }
}
