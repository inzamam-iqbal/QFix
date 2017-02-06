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
import java.util.Collections;
import java.util.Comparator;


public class Tab1onClick extends AppCompatActivity {

    ArrayList<CatagaryEmployee> catagaryEmployees;
    private LocationManager locationManager;
    private String catagoryName;
    private Tab1onClickAdapter tab1onClickAdapter;
    private ListView list;
    private Location userLocation;
    private Location employeeLoc;
    private LocationListener locationListner;
    private String lastKey;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab1on_click);

        //get the name of the catagory selected
        catagoryName = getIntent().getStringExtra("name");

        if (!utils.IsNetworkConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        employeeLoc = new Location("");
        catagaryEmployees = new ArrayList<>();

        getData(catagoryName);

    }


    private void getData(final String catagory){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("permission","no");
            // TODO: Change to EasyPerm
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else{
            Log.e("permmission","yes");
        }


        locationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("changed","chrr");
                userLocation = location;
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

        Location location = locationManager.getLastKnownLocation(getProviderName());
        //Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.e("Currenttime",System.currentTimeMillis()+"");
        if (location != null && location.getLatitude()!=0){
            userLocation = location;
            getDataFromDb(catagory);
            Log.e("gotfrom",location.getProvider());

            //userLocation.setLatitude(Double.valueOf(String.format("%.7f",userLocation.getLatitude())));
            //userLocation.setLongitude(Double.valueOf(String.format("%.7f",userLocation.getLongitude())));

        }else{
            Log.e("kk","k");
            if (getProviderName().equals("passive")){
                Toast.makeText(getApplicationContext(),
                        "Please turn on location service",Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }else{
                Log.e("kk","kl");
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

        tab1onClickAdapter = new Tab1onClickAdapter(Tab1onClick.this, catagaryEmployees );
        list=(ListView) findViewById(R.id.listView);
        list.setAdapter(tab1onClickAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!utils.IsNetworkConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("clicked",position+"");
                Intent d=new Intent(Tab1onClick.this,ViewProfile.class);
                d.putExtra("EmployeeId",catagaryEmployees.get(position).getKey());
                d.putExtra("catagory",catagoryName);
                d.putExtra("distance",catagaryEmployees.get(position).getDistanceAsString());
                startActivity(d);

            }
        });

        GeoFire geoFire = new GeoFire(locRef);
        count=0;

        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(userLocation.getLatitude(),
                userLocation.getLongitude()), 5);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                lastKey = key;
                count++;
                userRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CatagaryEmployee employee = dataSnapshot.getValue(CatagaryEmployee.class);
                        employeeLoc.setLatitude(location.latitude);
                        employeeLoc.setLongitude(location.longitude);

                        employee.setDistance((double)employeeLoc.distanceTo(userLocation));
                        employee.setKey(dataSnapshot.getKey());
                        catagaryEmployees.add(employee);
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        if (dataSnapshot.getKey().equals(lastKey)){
                            Collections.sort(catagaryEmployees, new Comparator<CatagaryEmployee>() {
                                @Override
                                public int compare(CatagaryEmployee ce1, CatagaryEmployee ce2) {
                                    return ce1.getDistance().compareTo(ce2.getDistance());
                                }
                            });
                            Log.e("Tab1OnClick:sorted",dataSnapshot.getKey());
                        }
                        tab1onClickAdapter.notifyDataSetChanged();

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
                if (count<15 && geoQuery.getRadius()<150){
                    if (geoQuery.getRadius()==5){
                        geoQuery.setRadius(10);
                    }else if (geoQuery.getRadius()==10){
                        geoQuery.setRadius(25);
                    }else{
                        geoQuery.setRadius(150);
                    }
                }
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

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Log.e("latDIs",latDistance+"");
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Log.e("longDIs",lonDistance+"");
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
