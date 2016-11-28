package com.jobbs.jobsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.model.SignUpCatagory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Inzimam on 10/5/2016.
 */
public class SignUpHomeServiceDialogFragment extends DialogFragment {

    private Button buttonOk;
    private RadioButton selectedOption;
    private RadioGroup radioGroup;
    private Employee employee;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.hoe_service_dialog,null);
        alertDialogBuilder.setView(view);



        employee = TabFragment2.employee;

        radioGroup = (RadioGroup) view.findViewById(R.id.home_service_radiogroup);

//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);



        alertDialogBuilder.setTitle("Home service?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        selectedOption = (RadioButton) view.findViewById(selectedId);
                        String homeService = selectedOption.getText().toString();

                        if(homeService.equals("yes")){
                            employee.setHomeService(true);
                        }else{
                            employee.setHomeService(false);
                        }

                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        SignUpLanguageDialogFragment dialog = new SignUpLanguageDialogFragment();
                        dialog.show(manager, "Language");
                        dialog.setCancelable(false);
                        getDialog().dismiss();
                    }
                });


        return alertDialogBuilder.create();


    }
}
