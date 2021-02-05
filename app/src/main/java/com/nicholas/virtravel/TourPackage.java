package com.nicholas.virtravel;

import org.json.JSONObject;

public class TourPackage {
    private String title, desc, imgLink;
    private double price, lat, lon;

    public TourPackage(JSONObject tourPackage) {

    }

    public TourPackage(String title, String desc, String imgLink, double price, double lat, double lon) {
        this.title = title;
        this.desc = desc;
        this.imgLink = imgLink;
        this.price = price;
        this.lat = lat;
        this.lon = lon;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImgLink() {
        return imgLink;
    }

    public double getPrice() {
        return price;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}