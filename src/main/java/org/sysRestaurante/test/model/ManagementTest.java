package org.sysRestaurante.test.model;

import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.SessionDao;
import org.sysRestaurante.model.Management;

import static org.junit.jupiter.api.Assertions.*;

class ManagementTest {

    @Test
    void shouldGetBusinessInfo() {
        SessionDao businessInfo = new SessionDao();
        businessInfo = Management.getBusinessInfo();
        assertNotNull(businessInfo);
    }
}