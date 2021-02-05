package com.nicholas.virtravel;

public class TourPackage {
    private String title, desc, imgLink, tourID, videoLink;
    private double price, lat, lon;
    private boolean expanded;

    public TourPackage(String title, String desc, String imgLink, double price, String videoLink, double lat, double lon, String tourID) {
        this.title = title;
        this.desc = desc;
        this.imgLink = imgLink;
        this.price = price;
        this.lat = lat;
        this.lon = lon;
        this.videoLink = videoLink;
        this.tourID = tourID;
        this.expanded = false;
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

    public String getVideoLink() {
        return videoLink;
    }

    public String getTourID() {
        return tourID;
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

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
