package com.jobbs.jobsapp.model;

/**
 * Created by Inzimam on 8/22/2016.
 */
public class SignUpCatagory {

    String name;
    String id;
    boolean selected;

    public SignUpCatagory(String name, String id) {
        this.id = id;
        this.name = name;
        this.selected =false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
