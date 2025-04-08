package org.sysRestaurante.dao;

public class MetadataDao {
    private int id;
    private String businessName;
    private String businessPhone;
    private String businessCNPJ;
    private String businessAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessCNPJ() {
        return businessCNPJ;
    }

    public void setBusinessCNPJ(String businessCNPJ) {
        this.businessCNPJ = businessCNPJ;
    }

    public String getBusinessAddress() {
        return this.businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }
}
