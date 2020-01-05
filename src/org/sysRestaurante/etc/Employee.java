package org.sysRestaurante.etc;

public class Employee extends User {
    private int idEmployee;

    public Employee(String name, String username, String email, String pass) {
        super(name, username, email, pass);
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }
}
