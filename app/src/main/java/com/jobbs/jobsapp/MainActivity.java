package com.jobbs.jobsapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jobbs.jobsapp.Adapter.PagerAdapter;
import com.jobbs.jobsapp.model.Catagaries;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.ImageUtils;
import com.jobbs.jobsapp.utils.JobsConstants;
import com.jobbs.jobsapp.utils.utils;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.EventListener;


public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.



//TODO: make oldLanguage selected
    public boolean isSignedIn=false;
    public PagerAdapter pagerAdapter;
    private LocationManager locationManager;
    private GeoFire geoFire;
    public static String userId;
    private ArrayList<String> catagoryArray;
    private LocationListener locationListener;
    private TabLayout tabLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TabLayout.Tab tab1;
    private ValueEventListener employeeEventListner;
    private DatabaseReference employeeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //initialize DB,Auth and geoFire
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference().
                child(JobsConstants.FIREBASE_REFERANCE_LOCATION);

        geoFire = new GeoFire(locationRef);

        mAuth = FirebaseAuth.getInstance();

        //setup the page
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Services"));
        tab1 = tabLayout.newTab().setText("SignUp");
        tabLayout.addTab(tab1);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
/*
        DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child(JobsConstants.FIREBASE_REFERANCE_CATAGORY);

        String key = temp.push().getKey();
        Catagaries cat = new Catagaries("Taxi","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FTaxi.png?alt=media&token=392f8c66-8e2e-446e-9c9f-2b5638cb7227");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("Cataring","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FFood-Catering.png?alt=media&token=710df740-e938-41a6-ab7c-5fa712894f30");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("Laundry","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FLaundry.png?alt=media&token=d22d0f12-944b-412f-83a3-350ccf74dbc8");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("A/C Repair","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FAC.png?alt=media&token=d0af188c-7374-4bfa-b7d6-5a105491f190");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("Electrician","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FElectrician.png?alt=media&token=e47527b7-5244-4879-b557-187ff5cfb31c");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("Auto-mobile","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2Fautomobile.png?alt=media&token=035c6e26-b1fd-45c5-b273-4926fd27819f");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("Photography","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FPhotography.png?alt=media&token=d464f274-2b58-4956-a2ef-320320b450e3");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("IT-Services","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FIT-Service.png?alt=media&token=eb74d5e6-0cc6-4ee2-bf41-e5025f42498a");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("Teacher","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FTeacher.png?alt=media&token=ebc17721-dc7f-4e70-8046-36f6ee6aac15");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("Painter","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FPainter.png?alt=media&token=27d765b1-fee5-4172-a6d0-68026407deb7");
        temp.child(key).setValue(cat);


        key = temp.push().getKey();
        cat = new Catagaries("Plumber","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FPlumber.png?alt=media&token=d792eb9e-dce7-4c17-89ab-961545baa0be");
        temp.child(key).setValue(cat);

        key = temp.push().getKey();
        cat = new Catagaries("Repair","https://firebasestorage.googleapis.com/v0/b/jobs-8d5e5.appspot.com/o/catagories%2FHome-application.png?alt=media&token=32097aa4-1666-4c77-8327-6b917df8e6b2");
        temp.child(key).setValue(cat);
*/


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                isSignedInAndRegistrationComplete(user);
            }
        };
        Log.e("Main:pinn", ImageUtils.getName(getApplicationContext()));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(),isSignedIn);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        isSignedInAndRegistrationComplete(user);
        getData();

        if (!utils.IsNetworkConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }

    }


    public void getData(){

        if (isSignedIn){
            employeeRef = FirebaseDatabase.getInstance().getReference().getRef().
                    child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                    child(JobsConstants.FIREBASE_KEY_CATAGORY);

            employeeEventListner = employeeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    catagoryArray = new ArrayList<String>();

                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        catagoryArray.add(data.getKey());
                    }
                    getLocation();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            getLocation();
        }


    }



    private void getLocation(){
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Main:permission","no");
            // TODO: change to easy perm
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else{
            Log.e("Main:permmission","yes");
        }

        locationListener =new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("changed","chhh");

                if (isSignedIn){
                    for (String catagory : catagoryArray){
                        geoFire.setLocation(catagory + "/" + userId, new GeoLocation(location.getLatitude(),location.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    System.err.println("There was an error saving the location to GeoFire: " + error);
                                } else {
                                    System.out.println("Location saved on server successfully!");
                                }
                            }
                        });
                    }
                }



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.e("Main:locationListner","status changed");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.e("long","enabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.e("Main:locationListner","provider disabled");
                turnOnLocation();
            }
        };

        if (getProviderName() == null || getProviderName().equals("passive")){
            turnOnLocation();
        }else{
            if (isSignedIn) {
                locationManager.requestLocationUpdates(getProviderName(), 600000, 0, locationListener);
            }else{
                locationManager.requestSingleUpdate(getProviderName(), locationListener,null);

            }
        }



    }

    String getProviderName() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(false); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        String provider = locationManager.getBestProvider(criteria, true);
        Log.e("Main:Location Provider",provider);
        return provider;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isSignedIn && locationListener != null){
            locationManager.removeUpdates(locationListener);
        }
        if (employeeEventListner != null){
            employeeRef.removeEventListener(employeeEventListner);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
    }

    private void turnOnLocation(){
        Toast.makeText(getApplicationContext(),
                "Please turn on location service",Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(myIntent);
    }

    private void isSignedInAndRegistrationComplete(FirebaseUser user){
        if (user != null){
            isSignedIn = true;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String done=sharedPref.getString("done","no");
            if(!done.equals("yes")){
                Log.e("Main:logOut","yes");
                mAuth.signOut();
            }else{
                userId = user.getUid();
                tab1.setText("Profile");
                Log.e("signed","gotcha");
            }
        }
    }
}

