package com.jobbs.jobsapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jobbs.jobsapp.model.CatagaryEmployee;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.JobsConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Inzimam on 8/18/2016.
 */
public class DbHelper {
    private DatabaseReference dbRef;
    private Employee employee;
    Context context;

    public DbHelper(){
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public DbHelper(Context c){
        context = c;
    }

    public Employee getEmployeeDetail(){
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employee = dataSnapshot.getValue(Employee.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return employee;
    }

    public void saveEmployeeDetail(Employee employee, CatagaryEmployee catagaryEmployee, ArrayList<String> catagaries,
                                   final SharedPreferences sharedPref, final Handler handler){
        String phoneNum="";
        catagaryEmployee.setImageUrl(employee.getImageUrl());
        HashMap<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_EMPLOYEE + "/" + employee.getPhoneNum(),employee.toMap());

        String loginKey = dbRef.child(JobsConstants.FIREBASE_REFERANCE_LOGIN_KEY).child(employee.getPhoneNum()).push().getKey();
        childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_CATAGORYEMPLOYEE + "/" + employee.getPhoneNum(), catagaryEmployee.toMap());
        childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_LOGIN_KEY + "/" + employee.getPhoneNum() + "/" + loginKey, true);

        dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("updated","up");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("done", "yes");
                editor.commit();
                Message message = Message.obtain();
                message.arg1 = 1;
                handler.sendMessage(message);
            }
        });

    }

    public void updateEmployeeDetail(String name, HashMap<String,Boolean> catagory, String country, String gender,
                                     int age, String phoneNum, String email, HashMap<String,Boolean> languages, String address){

        //Employee employee = new Employee(name,catagory,gender,age,phoneNum,email,languages,address,null);
        dbRef.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(phoneNum).setValue(employee);
    }

    public void updateCurrentLocation(){

    }

    public void getNearbyEmployees(){

    }

    public String saveProfilePicAndLoad (final String userId, byte[] img, final Context context,
                                       final ImageView imageView){
        Log.e("eee","eee");
        final String[] url = new String[1];

        return url[0];
    }

}
