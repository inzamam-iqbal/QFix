package com.jobbs.jobsapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobbs.jobsapp.database.DbHelper;
import com.jobbs.jobsapp.model.CatagaryEmployee;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.model.SignUpCatagory;
import com.jobbs.jobsapp.model.SignUpLanguage;
import com.jobbs.jobsapp.utils.JobsConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Inzimam on 9/24/2016.
 */
public class ChangeStatusDialogFragment extends DialogFragment {

    Button buttonOk;
    EditText editTxtStatus;
    String status;
    String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = getArguments().getString("txt");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }






    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.change_status_dialog_fragment,null);
        alertDialogBuilder.setView(view);

        editTxtStatus = (EditText)view.findViewById(R.id.text_change_status) ;
        editTxtStatus.setText(status);

        alertDialogBuilder.setTitle("Change status")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newStatus = editTxtStatus.getText().toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        HashMap<String,Object> childUpdates = new HashMap<>();

                        childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_EMPLOYEE + "/" +
                                userId + "/" + JobsConstants.FIREBASE_KEY_STATUS, newStatus);

                        childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_CATEGARYEMPLOYEE + "/" +
                                userId + "/" + JobsConstants.FIREBASE_KEY_STATUS, newStatus);

                        ref.updateChildren(childUpdates);
                        getDialog().dismiss();
                    }
                });


        return alertDialogBuilder.create();


    }


}