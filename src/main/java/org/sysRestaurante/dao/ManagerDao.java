package org.sysRestaurante.dao;

public class ManagerDao extends UserDao {
    private boolean isAdmin;

    public ManagerDao(String name, String username, String email, String pass, boolean isAdmin) {
        super();
        setAdmin(isAdmin);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

}
