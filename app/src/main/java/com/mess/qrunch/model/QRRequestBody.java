package com.mess.qrunch.model;

public class QRRequestBody {
    private String vendorId;
    private String rollNo;

    public QRRequestBody(String vendorId, String rollNo) {
        this.vendorId = vendorId;
        this.rollNo = rollNo;
    }

    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
}
