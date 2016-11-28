package com.jobbs.jobsapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.JobsConstants;

import java.util.HashMap;

/**
 * Created by Inzimam on 10/9/2016.
 */
public class EditDobDialogFragment extends DialogFragment {

    private Button buttonOk;
    private DatePicker datePicker;
    private String content;
    private int day;
    private int month;
    private int year;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try{
            content = getArguments().getString("content");
        }catch (Exception e){

        }


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
        View view = layoutInflater.inflate(R.layout.edit_date_of_birth_dialog,null);
        alertDialogBuilder.setView(view);

        datePicker = (DatePicker) view.findViewById(R.id.edit_dob_date);

        alertDialogBuilder.setTitle("Change DOB")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        day = datePicker.getDayOfMonth();
                        month = datePicker.getMonth() + 1;
                        year = datePicker.getYear();


                        ref.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                                child(JobsConstants.FIREBASE_KEY_DOB).
                                setValue(Integer.toString(year)+"-"+ Integer.toString(month)
                                        +"-"+ Integer.toString(day));

                        getDialog().dismiss();
                    }
                });


        return alertDialogBuilder.create();


    }


}
