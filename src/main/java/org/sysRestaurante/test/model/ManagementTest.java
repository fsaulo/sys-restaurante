package org.sysRestaurante.test.model;

import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.MetadataDao;
import org.sysRestaurante.model.Management;

import static org.junit.jupiter.api.Assertions.*;

class ManagementTest {

    MetadataDao businessInfo;
    int id = 0;
    String address = "String: Address";
    String name = "String: Business Name";
    String phone = "String: Phone";
    String cnpj = "String: CNPJ";

    MetadataDao createBusinessInfo() {
        MetadataDao info = new MetadataDao();
        info.setId(id);
        info.setBusinessAddress(address);
        info.setBusinessName(name);
        info.setBusinessPhone(phone);
        info.setBusinessCNPJ(cnpj);
        return info;
    }

    @Test
    void shouldGetBusinessInfo() {
        businessInfo = Management.getBusinessInfo(id);
        assertNotNull(businessInfo);
    }

    @Test
    void shouldInsertMetadata() {
        MetadataDao info = createBusinessInfo();

        assertNotNull(info);
        Management.updateBusinessInfo(info);
    }

    @Test
    void shouldUpdateBusinessInfo() {
        MetadataDao info = createBusinessInfo();
        assertNotNull(info);

        Management.setMetadata(info.getId());
        Management.updateBusinessInfo(info);
        MetadataDao updatedInfo = Management.getBusinessInfo(info.getId());

        assertNotNull(updatedInfo);
        assertEquals(address, updatedInfo.getBusinessAddress());
        assertEquals(name, updatedInfo.getBusinessName());
        assertEquals(phone, updatedInfo.getBusinessPhone());
        assertEquals(cnpj, updatedInfo.getBusinessCNPJ());

        Management.deleteMetadata(info.getId());
    }

    @Test
    void shouldDeleteBusinessInfo() {
        MetadataDao info = createBusinessInfo();
        assertNotNull(info);
        Management.updateBusinessInfo(info);
        Management.deleteMetadata(info.getId());
        MetadataDao emptyInfo = Management.getBusinessInfo(info.getId());
        assertNotNull(emptyInfo);
        assertNull(emptyInfo.getBusinessAddress());
        assertNull(emptyInfo.getBusinessName());
        assertNull(emptyInfo.getBusinessPhone());
        assertEquals(0, emptyInfo.getId());
    }
}