package org.sysRestaurante.applet;

import org.sysRestaurante.etc.Employee;
import org.sysRestaurante.etc.Manager;
import org.sysRestaurante.etc.User;
import org.sysRestaurante.gui.AppController;
import org.sysRestaurante.gui.DashbordController;
import org.sysRestaurante.gui.LoginController;
import org.sysRestaurante.model.Authentication;

public class AppFactory {
    private static Manager manager;
    private static Employee employee;
    private static User user;
    private static AppController appController;
    private static DashbordController dashbordController;
    private static LoginController loginController;
    private static Authentication authentication;

    public static Authentication getAuthentication() {
        return authentication;
    }

    public static void setAuthentication(Authentication authentication) {
        AppFactory.authentication = authentication;
    }

    public static LoginController getLoginController() {
        return loginController;
    }

    public static void setLoginController(LoginController loginController) {
        AppFactory.loginController = loginController;
    }

    public static DashbordController getDashbordController() {
        return dashbordController;
    }

    public static void setDashbordController(DashbordController dashbordController) {
        AppFactory.dashbordController = dashbordController;
    }

    public static AppController getAppController() {
        return appController;
    }

    public static void setAppController(AppController appController) {
        AppFactory.appController = appController;
    }


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
