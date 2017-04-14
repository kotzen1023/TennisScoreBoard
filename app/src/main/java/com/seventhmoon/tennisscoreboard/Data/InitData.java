package com.seventhmoon.tennisscoreboard.Data;

import android.util.Log;

import com.seventhmoon.tennisscoreboard.Sql.Jdbc;

public class InitData {
    private static final String TAG = InitData.class.getName();
    // for find court(map)
    public Jdbc jdbc;
    private int upload_remain = 0;
    private String wifiMac;
    private double current_longitude;
    private double current_latitude;
    private boolean match_mac;

    public InitData() {
        jdbc = new Jdbc();
    }

    public void setJdbcUserMac(String userMac) {
        jdbc.setMacAddress(userMac);
    }

    public int getUpload_remain() {
        return upload_remain;
    }

    public void setUpload_remain(int upload_remain) {
        this.upload_remain = upload_remain;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public void setWifiMac(String wifiMac) {
        Log.d(TAG, "=== set mac "+wifiMac+ " ===");

        this.wifiMac = wifiMac;
    }

    public double getCurrent_longitude() {
        return current_longitude;
    }

    public void setCurrent_longitude(double current_longitude) {
        this.current_longitude = current_longitude;
    }

    public double getCurrent_latitude() {
        return current_latitude;
    }

    public void setCurrent_latitude(double current_latitude) {
        this.current_latitude = current_latitude;
    }

    public boolean isMatch_mac() {
        return match_mac;
    }

    public void setMatch_mac(boolean match_mac) {
        this.match_mac = match_mac;
    }
}
