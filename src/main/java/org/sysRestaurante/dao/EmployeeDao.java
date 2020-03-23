package org.sysRestaurante.dao;

public class EmployeeDao extends UserDao {
    private int idEmployee;
    private String name;
    private double salary;
    private int paymentDay;

    public EmployeeDao(String name, String username, String email, String pass) {
        super();
    }

    public EmployeeDao () {
        super();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    public int getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(int paymentDay) {
        this.paymentDay = paymentDay;
    }
}
