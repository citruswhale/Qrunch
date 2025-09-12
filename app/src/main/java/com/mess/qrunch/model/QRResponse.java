package com.mess.qrunch.model;

import com.google.gson.annotations.SerializedName;

public class QRResponse {
    @SerializedName("qrImage")
    private String qrImage;

    public String getQrImage() {
        return qrImage;
    }

    public void setQrImage(String qrImage) {
        this.qrImage = qrImage;
    }
}
