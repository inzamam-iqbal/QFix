package com.jobbs.jobsapp.model;

/**
 * Created by Inzimam on 8/23/2016.
 */
public class SignUpLanguage {
    String name;
    boolean selected;

    public SignUpLanguage(String name) {
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
}
