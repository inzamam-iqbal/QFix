package com.jobbs.jobsapp;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.JobsConstants;



/**
\ */
public class TaxiDialogFragment extends DialogFragment {

    private ImageView buttonVan;
    private ImageView buttonThreeWheeler;
    private ImageView buttonNano;
    private ImageView buttonCar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.taxi_dialog_fragment,null);
        alertDialogBuilder.setView(view);

        buttonCar = (ImageView)view.findViewById(R.id.button_car);
        buttonNano = (ImageView)view.findViewById(R.id.button_nano);
        buttonThreeWheeler = (ImageView)view.findViewById(R.id.button_threeWheeler);
        buttonVan = (ImageView)view.findViewById(R.id.button_van);

        buttonNano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(),Tab1onClick.class);
                i.putExtra("name","Taxi-Nano");
                startActivity(i);
            }
        });

        buttonCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(),Tab1onClick.class);
                i.putExtra("name","Taxi-Car");
                startActivity(i);
            }
        });

        buttonThreeWheeler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(),Tab1onClick.class);
                i.putExtra("name","Taxi-Three Wheeler");
                startActivity(i);
            }
        });

        buttonVan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(),Tab1onClick.class);
                i.putExtra("name","Taxi-Van");
                startActivity(i);
            }
        });




        return alertDialogBuilder.create();

    }
}
