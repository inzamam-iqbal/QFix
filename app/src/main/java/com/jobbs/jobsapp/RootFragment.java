package com.jobbs.jobsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class RootFragment extends Fragment {


    public RootFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_root, container, false);


    /*
     * When this container fragment is created, we fill it with our first
     * "real" fragment
     */
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user !=null){
            transaction.replace(R.id.root_frame, new ViewOwnProfileFragment());

        }else{
            transaction.replace(R.id.root_frame, new TabFragment2());

        }

        transaction.commit();

        return view;
    }

}
