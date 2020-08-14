package org.sysRestaurante.applet;

import org.sysRestaurante.dao.*;
import org.sysRestaurante.gui.*;

import java.util.ArrayList;

public class AppFactory {
    private static UserDao userDao;
    private static AppController appController;
    private static DashboardController dashboardController;
    private static LoginController loginController;
    private static CashierDao cashierDao;
    private static OrderDao orderDao;
    private static ProductDao productDao;
    private static SessionDao sessionDao;
    private static ComandaDao comandaDao;
    private static MainGUIController mainController;
    private static CashierController cashierController;
    private static CashierPOSController cashierPOSController;
    private static ArrayList<ProductDao> selectedProducts;
    private static ManageComandaController manageComandaController;
    private static ComandaPOSController comandaPOSController;
    private static POS pos;
    private static ReceiptViewController receiptViewController;
    private static ToolBarController toolBarController;
    private static CashierHistoryController cashierHistoryController;
    private static ProductManagementController productManagementController;
    private static MainGUI mainGUI;

    public static void clearWorkspace() {
        setReceiptViewController(null);
        setSelectedProducts(null);
        setOrderDao(null);
        setComandaDao(null);
        setComandaPOSController(null);
        setCashierDao(null);
        setUserDao(null);
        setCashierController(null);
        setPos(null);
        setMainGUI(null);
        setCashierPOSController(null);
        setSessionDao(null);
        setManageComandaController(null);
        setToolBarController(null);
    }

    public static MainGUI getMainGUI() {
        return mainGUI;
    }

    public static void setMainGUI(MainGUI mainGUI) {
        AppFactory.mainGUI = mainGUI;
    }

    public static CashierHistoryController getCashierHistoryController() {
        return cashierHistoryController;
    }

    public static void setCashierHistoryController(CashierHistoryController cashierHistoryController) {
        AppFactory.cashierHistoryController = cashierHistoryController;
    }

    public static ToolBarController getToolBarController() {
        return toolBarController;
    }

    public static void setToolBarController(ToolBarController toolBarController) {
        AppFactory.toolBarController = toolBarController;
    }

    public static SessionDao getSessionDao() {
        return sessionDao;
    }

    public static void setSessionDao(SessionDao sessionDao) {
        AppFactory.sessionDao = sessionDao;
    }

    public static ComandaDao getComandaDao() {
        return comandaDao;
    }

    public static void setComandaDao(ComandaDao comandaDao) {
        AppFactory.comandaDao = comandaDao;
    }

    public static POS getPos() {
        return pos;
    }

    public static void setPos(POS pos) {
        AppFactory.pos = pos;
    }

    public static ManageComandaController getManageComandaController() {
        return manageComandaController;
    }

    public static void setManageComandaController(ManageComandaController manageComandaController) {
        AppFactory.manageComandaController = manageComandaController;
    }

    public static OrderDao getOrderDao() {
        return orderDao;
    }

    public static void setOrderDao(OrderDao orderDao) {
        AppFactory.orderDao = orderDao;
    }

    public static ArrayList<ProductDao> getSelectedProducts() {
        return selectedProducts;
    }

    public static void setSelectedProducts(ArrayList<ProductDao> selectedProducts) {
        AppFactory.selectedProducts = selectedProducts;
    }

    public static CashierPOSController getCashierPOSController() {
        return cashierPOSController;
    }

    public static void setCashierPOSController(CashierPOSController cashierPOSController) {
        AppFactory.cashierPOSController = cashierPOSController;
    }

    public static CashierController getCashierController() {
        return cashierController;
    }

    public static void setCashierController(CashierController cashierController) {
        AppFactory.cashierController = cashierController;
    }

    public static MainGUIController getMainController() {
        return mainController;
    }

    public static void setMainController(MainGUIController mainController) {
        AppFactory.mainController = mainController;
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

    public static CashierDao getCashierDao() {
        return cashierDao;
    }

    public static void setCashierDao(CashierDao cashierDao) {
        AppFactory.cashierDao = cashierDao;
    }

    public static LoginController getLoginController() {
        return loginController;
    }

    public static UserDao getUserDao() {
        return userDao;
    }

    public static void setUserDao(UserDao userDao) {
        AppFactory.userDao = userDao;
    }

    public static AppController getAppController() {
        return appController;
    }

    public static void setAppController(AppController appController) {
        AppFactory.appController = appController;
    }

    public static void setComandaPOSController(ComandaPOSController comandaPOSController) {
        AppFactory.comandaPOSController = comandaPOSController;
    }

    public static ComandaPOSController getComandaPOSController() {
        return comandaPOSController;
    }

    public static ReceiptViewController getReceiptViewController() {
        return receiptViewController;
    }

    public static void setReceiptViewController(ReceiptViewController receiptViewController) {
        AppFactory.receiptViewController = receiptViewController;
    }

    public static void setProductManagementController(ProductManagementController productManagementController) {
        AppFactory.productManagementController = productManagementController;
    }

    public static ProductManagementController getProductManagementController() {
        return AppFactory.productManagementController;
    }

    public static ProductDao getProductDao() {
        return productDao;
    }

    public static void setProductDao(ProductDao productDao) {
        AppFactory.productDao = productDao;
    }
}
