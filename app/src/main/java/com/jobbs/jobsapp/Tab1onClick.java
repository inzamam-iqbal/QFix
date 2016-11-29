package com.jobbs.jobsapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jobbs.jobsapp.Adapter.Tab1onClickAdapter;
import com.jobbs.jobsapp.model.CatagaryEmployee;
import com.jobbs.jobsapp.utils.JobsConstants;
import com.jobbs.jobsapp.utils.utils;

import java.util.ArrayList;


public class Tab1onClick extends AppCompatActivity {

    ArrayList<CatagaryEmployee> catagaryEmployees;
    ArrayList<String> employeeIds;
    private LocationManager locationManager;
    private String catagoryName;
    private Tab1onClickAdapter tab1onClickAdapter;
    private ListView list;
    private Location now;
    private Location employeeLoc;
    private LocationListener locationListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab1on_click);

        catagoryName = getIntent().getStringExtra("name");

        catagaryEmployees = new ArrayList<>();
        employeeIds = new ArrayList<>();

        getData(catagoryName);

        now= new Location("");

        employeeLoc = new Location("");

        if (!utils.IsNetworkConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }




    }


    private void getData(final String catagory){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("perm","no");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else{
            Log.e("perm","yes");
        }
        Log.e("perm","came");


        locationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("changed","chrr");
                now= location;
                getDataFromDb(catagory);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.e("long",s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.e("long","enabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.e("long","disabled");
            }
        };

        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location != null && (System.currentTimeMillis()-location.getTime())<5*60*1000 ){
            now= location;
            getDataFromDb(catagory);
            Log.e("gotfrom","last");
        }else{
            if (getProviderName().equals("passive")){
                Toast.makeText(getApplicationContext(),
                        "Please turn on location service",Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }else{
                locationManager.requestSingleUpdate(getProviderName(), locationListner,null);

            }
        }

    }

    String getProviderName() {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(false); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }

    private void getDataFromDb(String catagory){
        DatabaseReference locRef= FirebaseDatabase.getInstance().getReference().
                child(JobsConstants.FIREBASE_REFERANCE_LOCATION).child(catagory);

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().
                child(JobsConstants.FIREBASE_REFERANCE_CATAGORYEMPLOYEE);

        tab1onClickAdapter = new Tab1onClickAdapter(Tab1onClick.this, catagaryEmployees, employeeIds );
        list=(ListView) findViewById(R.id.listView);
        list.setAdapter(tab1onClickAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!utils.IsNetworkConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent d=new Intent(Tab1onClick.this,ViewProfile.class);
                d.putExtra("EmployeeId",employeeIds.get(position));
                startActivity(d);

            }
        });

        GeoFire geoFire = new GeoFire(locRef);


        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(now.getLatitude(),
                now.getLongitude()), 200);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                userRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CatagaryEmployee employee = dataSnapshot.getValue(CatagaryEmployee.class);
                        employeeLoc.setLatitude(location.latitude);
                        employeeLoc.setLongitude(location.longitude);
                        employee.setDistance(now.distanceTo(employeeLoc));
                        catagaryEmployees.add(employee);
                        employeeIds.add(dataSnapshot.getKey());
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        tab1onClickAdapter.notifyDataSetChanged();
                        Log.e("foundd",dataSnapshot.getKey());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                Log.e("done","geoDone");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListner);
    }
}
