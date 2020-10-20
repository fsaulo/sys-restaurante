package org.sysRestaurante.test.model;

import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.MetadataDao;
import org.sysRestaurante.model.Management;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagementTest {

    @Test
    void shouldGetBusinessInfo() {
        MetadataDao businessInfo = Management.getBusinessInfo();
        assertNotNull(businessInfo);
    }

    @Test
    void shouldInsertMetadata() {
        Management.setMetadata(new Random().nextInt());
    }

    @Test
    void shouldUpdateBusinessInfo() {
        MetadataDao businessInfo = new MetadataDao();
        businessInfo.setId(1);
        businessInfo.setBusinessAddress("String: Adress");
        businessInfo.setBusinessName("String: Business Name");
        businessInfo.setBusinessPhone("String: Phone");
        businessInfo.setBusinessCNPJ("String: CNPJ");
        Management.updateBusinessInfo(businessInfo);
    }
}