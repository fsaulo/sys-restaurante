package org.sysRestaurante.applet;

import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.EmployeeDao;
import org.sysRestaurante.dao.ManagerDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.gui.AppController;
import org.sysRestaurante.gui.DashboardController;
import org.sysRestaurante.gui.LoginController;
import org.sysRestaurante.model.Authentication;

public class AppFactory {
    private static ManagerDao managerDao;
    private static EmployeeDao employeeDao;
    private static UserDao userDao;
    private static AppController appController;
    private static DashboardController dashboardController;
    private static LoginController loginController;
    private static Authentication authentication;
    private static CashierDao cashierDao;

    public static CashierDao getCashierDao() {
        return cashierDao;
    }

    public static void setCashierDao(CashierDao cashierDao) {
        AppFactory.cashierDao = cashierDao;
    }

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

    public static DashboardController getDashboardController() {
        return dashboardController;
    }

    public static void setDashboardController(DashboardController dashboardController) {
        AppFactory.dashboardController = dashboardController;
    }

    public static AppController getAppController() {
        return appController;
    }

    public static void setAppController(AppController appController) {
        AppFactory.appController = appController;
    }


    public static ManagerDao getManagerDao() {
        return managerDao;
    }

    public static void setManagerDao(ManagerDao managerDao) {
        AppFactory.managerDao = managerDao;
    }

    public static EmployeeDao getEmployeeDao() {
        return employeeDao;
    }

    public static void setEmployeeDao(EmployeeDao employeeDao) {
        AppFactory.employeeDao = employeeDao;
    }

    public static UserDao getUserDao() {
        return userDao;
    }

    public static void setUserDao(UserDao userDao) {
        AppFactory.userDao = userDao;
    }
}
