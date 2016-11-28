package com.jobbs.jobsapp.model;

/**
 * Created by Inzimam on 8/18/2016.
 */
public class Catagaries {
    String name;
    String image;

    public Catagaries() {
    }

    public Catagaries(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
