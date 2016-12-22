package com.jobbs.jobsapp.model;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inzimam on 8/18/2016.
 */
public class CatagaryEmployee {
    String name;
    String gender;
    String dob;
    String imageUrl;
    Double distance;
    String status;
    double updateTime;
    Boolean homeService;
    @Exclude
    String key;

    public CatagaryEmployee() {
    }

    public CatagaryEmployee(String name, String gender, String dob,Boolean homeService,
                            String status) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.homeService =homeService;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(double updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getHomeService() {
        return homeService;
    }

    public void setHomeService(Boolean homeService) {
        this.homeService = homeService;
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public String getAgeFromDOB(){
        SimpleDateFormat day = new SimpleDateFormat("yyyy");
        Long now = System.currentTimeMillis();
        int year  = Integer.parseInt(day.format(now));
        return (year - Integer.parseInt(dob.substring(0,4))) +" years";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("gender", gender);
        result.put("imageUrl",imageUrl);
        result.put("dob",dob);
        result.put("updateTime",updateTime);
        result.put("status",status);
        result.put("homeService",homeService);
        return result;
    }
}
