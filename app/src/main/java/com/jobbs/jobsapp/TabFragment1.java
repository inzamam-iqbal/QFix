package com.jobbs.jobsapp;


import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jobbs.jobsapp.Adapter.GridAdapter;
import com.jobbs.jobsapp.model.Catagaries;
import com.jobbs.jobsapp.utils.JobsConstants;

import java.util.ArrayList;


public class TabFragment1 extends Fragment {

    ArrayList<Catagaries> catagaries;
    ArrayList<String> catagariesIds;
    DatabaseReference mRef;
    private GridAdapter gridAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab1, container, false);
        catagaries = new ArrayList<Catagaries>();
        catagariesIds = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference().child(JobsConstants.FIREBASE_REFERANCE_CATAGORY);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    rootView.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    catagaries.add(data.getValue(Catagaries.class));
                    catagariesIds.add(data.getKey());
                    gridAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        gridAdapter = new GridAdapter(getActivity(), catagaries );
        GridView list=(GridView) rootView.findViewById(R.id.gridView);
        list.setAdapter(gridAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){

                }else{
                    Intent d=new Intent(getActivity(),Tab1onClick.class);
                    d.putExtra("name",catagaries.get(position).getName());
                    startActivity(d);
                }


            }
        });
        return rootView;
    }


    public void showTaxiDialog(View view) {

        FragmentManager manager = getActivity().getSupportFragmentManager();

        SignUpJobCatagaryDialogFragment dialog = new SignUpJobCatagaryDialogFragment();
        dialog.setCancelable(false);
        dialog.show(manager, "Job Catagory");

    }

}