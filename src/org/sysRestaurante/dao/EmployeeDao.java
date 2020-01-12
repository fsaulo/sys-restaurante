package org.sysRestaurante.dao;

public class EmployeeDao extends UserDao {
    private int idEmployee;

    public EmployeeDao(String name, String username, String email, String pass) {
        super();
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }
}
