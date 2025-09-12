package com.mess.qrunch.api;

public enum baseApiURL {
    FETCH_MENU_IMAGE_BASE_URL("https://tlhqgr3g47.execute-api.eu-north-1.amazonaws.com"),
    GENERATE_QR_BASE_URL("https://vtr3u7yvff.execute-api.eu-north-1.amazonaws.com");

    private final String url;

    baseApiURL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
