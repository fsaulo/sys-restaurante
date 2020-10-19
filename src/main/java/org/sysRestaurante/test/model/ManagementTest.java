package org.sysRestaurante.test.model;

import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.SessionDao;
import org.sysRestaurante.model.Management;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ManagementTest {

    @Test
    void shouldGetBusinessInfo() {
        SessionDao businessInfo = Management.getBusinessInfo();
        assertNotNull(businessInfo);
    }

    @Test
    void shouldInsertMetadata() {
        Management.setMetadata(new Random().nextInt());
    }
}