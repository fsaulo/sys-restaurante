package org.sysRestaurante.etc;

public class Manager extends User {
    private boolean isAdmin;

    public Manager(String name, String username, String email, String pass, boolean isAdmin) {
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
