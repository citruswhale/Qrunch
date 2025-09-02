package com.example.test.model;

import java.util.UUID;

public class User {
    private String linkedAuthUid;
    private String rollNo;
    private String name;
    private UUID vendorId;

    public User() {
        vendorId = null;
    }

    public User(String linkedAuthUid, String rollNo, String name, UUID vendorId) {
        this.linkedAuthUid = linkedAuthUid;
        this.rollNo = rollNo;
        this.name = name;
        this.vendorId = vendorId;
    }

    public String getLinkedAuthUid() {
        return linkedAuthUid;
    }

    public void setLinkedAuthUid(String linkedAuthUid) {
        this.linkedAuthUid = linkedAuthUid;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getVendorId() {
        return vendorId;
    }

    public void setVendorId(UUID vendorId) {
        this.vendorId = vendorId;
    }
}
