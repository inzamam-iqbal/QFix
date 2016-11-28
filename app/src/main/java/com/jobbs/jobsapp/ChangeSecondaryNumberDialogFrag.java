package com.jobbs.jobsapp;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.JobsConstants;

import java.util.HashMap;

/**
 * Created by Inzimam on 10/9/2016.
 */
public class ChangeSecondaryNumberDialogFrag extends DialogFragment {

    private Button buttonOk;
    private EditText editText;
    private CountryCodePicker ccp;
    private String content;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = getArguments().getString("content");
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
        View view = layoutInflater.inflate(R.layout.edit_secondary_phone_dialog,null);
        alertDialogBuilder.setView(view);

        editText = (EditText) view.findViewById(R.id.change_secondary_phone_numtxt);

        editText.setText(content);

        alertDialogBuilder.setTitle("Change your secondary number")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        String newContent = editText.getText().toString();
                        ref.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                                child(JobsConstants.FIREBASE_KEY_SECONDARY_PHONE).setValue(newContent);
                        getDialog().dismiss();
                    }
                });


        return alertDialogBuilder.create();


    }




}
