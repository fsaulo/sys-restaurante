package org.sysRestaurante.applet;

import org.sysRestaurante.etc.Employee;
import org.sysRestaurante.etc.Manager;
import org.sysRestaurante.etc.User;

public class AppFactory {
    private static Manager manager;
    private static Employee employee;
    private static User user;

    public static Manager getManager() {
        return manager;
    }

    public static void setManager(Manager manager) {
        AppFactory.manager = manager;
    }

    public static Employee getEmployee() {
        return employee;
    }

    public static void setEmployee(Employee employee) {
        AppFactory.employee = employee;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        AppFactory.user = user;
    }
}
