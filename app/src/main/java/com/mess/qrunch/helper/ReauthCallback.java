package com.mess.qrunch.helper;

public interface ReauthCallback {
    void onComplete(boolean success, Exception exception);
}
