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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.ImageUtils;
import com.jobbs.jobsapp.utils.JobsConstants;
import com.jobbs.jobsapp.utils.utils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

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
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

    }


    public void getData(){

        if (isSignedIn){
            DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference().getRef().
                    child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                    child(JobsConstants.FIREBASE_KEY_CATAGORY);

            employeeRef.addValueEventListener(new ValueEventListener() {
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

