package com.seventhmoon.tennisscoreboard.Data;

import android.graphics.Bitmap;


public class PageItem {
    private String name;
    private double longitude;
    private double latitude;
    private int type;
    private byte court_usage;
    private byte light;
    private int court_num;
    private String charge;
    private float maintenance;
    private float traffic;
    private float parking;
    private Bitmap pic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte getCourt_usage() {
        return court_usage;
    }

    public void setCourt_usage(byte court_usage) {
        this.court_usage = court_usage;
    }

    public byte getLight() {
        return light;
    }

    public void setLight(byte light) {
        this.light = light;
    }

    public int getCourt_num() {
        return court_num;
    }

    public void setCourt_num(int court_num) {
        this.court_num = court_num;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public float getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(float maintenance) {
        this.maintenance = maintenance;
    }

    public float getTraffic() {
        return traffic;
    }

    public void setTraffic(float traffic) {
        this.traffic = traffic;
    }

    public float getParking() {
        return parking;
    }

    public void setParking(float parking) {
        this.parking = parking;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }
}
