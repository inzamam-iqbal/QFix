package com.jobbs.jobsapp.model;

import android.content.Intent;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inzimam on 8/18/2016.
 */
public class Employee {
    String name;
    HashMap<String,Boolean> catagary;
    String gender;
    String phoneNum;
    String phoneNumSecondary;
    String email;
    HashMap<String,Boolean> languages;
    String address;
    String imageUrl;
    String about;
    String dob;
    String status;
    Boolean homeService;
    String nic;

    public Employee() {
    }

    public Employee(String name, HashMap<String,Boolean> catagary, String gender, String phoneNum,
                    String email, HashMap<String,Boolean> languages, String address, String imageUrl, String about) {
        this.name = name;
        this.catagary = catagary;
        this.gender = gender;
        this.phoneNum = phoneNum;
        this.email = email;
        this.languages = languages;
        this.address = address;
        this.imageUrl = imageUrl;
        this.about = about;
    }

    public Employee(String name, String gender, String phoneNum, String phoneNumSecondary, String email,
                    String dob, String imageUrl) {
        this.name = name;
        this.gender = gender;
        this.phoneNum = phoneNum;
        this.phoneNumSecondary = phoneNumSecondary;
        this.email = email;
        this.dob = dob;
        this.imageUrl = imageUrl;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public Boolean getHomeService() {
        return homeService;
    }

    public void setHomeService(Boolean homeService) {
        this.homeService = homeService;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Boolean> getCatagary() {
        return catagary;
    }

    public void setCatagary(HashMap<String, Boolean> catagary) {
        this.catagary = catagary;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhoneNumSecondary() {
        return phoneNumSecondary;
    }

    public void setPhoneNumSecondary(String phoneNumSecondary) {
        this.phoneNumSecondary = phoneNumSecondary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, Boolean> getLanguages() {
        return languages;
    }

    public void setLanguages(HashMap<String, Boolean> languages) {
        this.languages = languages;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("catagary", catagary);
        result.put("gender", gender);
        result.put("phoneNum",phoneNum);
        result.put("phoneNumSecondary",phoneNumSecondary);
        result.put("email",email);
        result.put("languages",languages);
        result.put("address",address);
        result.put("imageUrl",imageUrl);
        result.put("about",about);
        result.put("dob",dob);
        result.put("status",status);
        return result;
    }

    @Exclude
    public String getAgeFromDOB(){
        SimpleDateFormat day = new SimpleDateFormat("yyyy");
        Long now = System.currentTimeMillis();
        int year  = Integer.parseInt(day.format(now));
        return (year - Integer.parseInt(dob.substring(0,4))) +" years";
    }

    @Exclude
    public String getCatagoryAsString(){
        int i=1;
        String results="";
        for (String key : catagary.keySet()){
            if (i<catagary.keySet().size()){
                results+=key+" / ";
            }else{
                results+=key;
            }
            i++;
        }
        return results;
    }

    @Exclude
    public String getLanguageAsString(){
        int i=1;
        String results="";
        for (String key : languages.keySet()){
            if (i<languages.keySet().size()){
                results+=key+" / ";
            }else{
                results+=key;
            }
            i++;
        }
        return results;
    }


}
