package org.sysRestaurante.test.model;

import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.MetadataDao;
import org.sysRestaurante.model.Management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagementTest {

    MetadataDao businessInfo;
    int id = 0;

    @Test
    void shouldGetBusinessInfo() {
        this.businessInfo = Management.getBusinessInfo();
        assertNotNull(businessInfo);
    }

    @Test
    void shouldInsertMetadata() {
        Management.setMetadata(id);
    }

    @Test
    void shouldUpdateBusinessInfo() {
        MetadataDao info = new MetadataDao();
        assertNotNull(info);

        info.setId(id);
        info.setBusinessAddress("String: Adress");
        info.setBusinessName("String: Business Name");
        info.setBusinessPhone("String: Phone");
        info.setBusinessCNPJ("String: CNPJ");

        Management.updateBusinessInfo(info);
        MetadataDao updatedInfo = Management.getBusinessInfo();

        assert updatedInfo != null;
        assertEquals(updatedInfo.getBusinessAddress(), "String: Adress");
        assertEquals(updatedInfo.getBusinessName(), "String: Business Name");
        assertEquals(updatedInfo.getBusinessPhone(), "String: Phone");
        assertEquals(updatedInfo.getBusinessCNPJ(), "String: CNPJ");
    }
}