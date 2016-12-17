package com.jobbs.jobsapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobbs.jobsapp.database.DbHelper;
import com.jobbs.jobsapp.model.CatagaryEmployee;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.JobsConstants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Inzimam on 10/5/2016.
 */
public class SignupAboutDialogFragment extends DialogFragment {

    private Button buttonOk;
    private EditText aboutText;
    private Employee employee;
    private boolean isEdit = false;
    private String oldAbout;
    private String userId;
    private Handler handler;
    private WeakReference<ProgressDialog> loadingDialog;
    private MainActivity activity;
    private SharedPreferences sharedPref;
    private Handler handler1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        try{
            isEdit = getArguments().getBoolean("edit");
            oldAbout = getArguments().getString("about");
        }catch (Exception e){

        }

        if (isEdit){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }



    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.signup_about_dialog,null);
        alertDialogBuilder.setView(view);
        aboutText = (EditText) view.findViewById(R.id.signup_about);
        activity = (MainActivity) getActivity();

        final FragmentTransaction trans = getFragmentManager().beginTransaction();

        alertDialogBuilder.setTitle("About your self")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String about = aboutText.getText().toString();

                        if(isEdit){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                                    child(JobsConstants.FIREBASE_KEY_ABOUT).setValue(about);


                        }else{
                            employee = TabFragment2.employee;

                            if (about != null){
                                employee.setAbout(about);
                            }

                            ShowLoadingMessage(true);

                            Timer timer = new Timer();
                            TimerTask timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Log.e("ca","came3");
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user!=null){
                                                employee.setStatus("Hi there I'm using jobbs");
                                                Log.e("ca","came2");
                                                CatagaryEmployee catagaryEmployee = new CatagaryEmployee(employee.getName(),
                                                        employee.getGender(), employee.getDob(), employee.getHomeService(),
                                                        employee.getStatus());

                                                ArrayList<String> catagories = new ArrayList<String>();
                                                for (String key: employee.getCatagary().keySet()){
                                                    catagories.add(key);
                                                }

                                                handler1 = new Handler(){
                                                    @Override
                                                    public void handleMessage(Message msg) {
                                                        if (msg.arg1==1){
                                                            SharedPreferences.Editor editor = sharedPref.edit();
                                                            editor.putString("done", "yes");
                                                            editor.commit();

                                                            ShowLoadingMessage(false);
                                                            trans.replace(R.id.root_frame, new ViewOwnProfileFragment());
                                                            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                            trans.addToBackStack(null);
                                                            trans.commit();
                                                            activity =null;
                                                        }else{
                                                            ShowLoadingMessage(false);
                                                            Toast.makeText(getActivity().getApplicationContext(), "Signup failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                };

                                                DbHelper dbHelper = new DbHelper();
                                                dbHelper.saveEmployeeDetail(employee,catagaryEmployee,catagories, sharedPref,handler1);

                                                activity =null;

                                            }else{
                                                changeFragment();
                                            }

                                        }
                                    });

                                }
                            };

                            timer.schedule(timerTask, 40000);

                        }
                    }
                });



        try{
            if (isEdit){
                aboutText.setText(oldAbout);
            }
        }catch (Exception e){

        }



        return alertDialogBuilder.create();


    }

    private void changeFragment(){
        FragmentManager manager = activity.getSupportFragmentManager();
        ShowLoadingMessage(false);
        Log.e("ca","came");
        SignUpMobileNumDialogFragment dialog = new SignUpMobileNumDialogFragment();
        dialog.show(manager, "Mobile Number Verification");
        activity = null;
        //getDialog().dismiss();
    }

    private void ShowLoadingMessage(boolean show) {
        if (show) {
            if (this.loadingDialog == null)
                this.loadingDialog = new WeakReference<>(ProgressDialog.show(getActivity(), "", "Please wait...", true));
        } else {
            if (this.loadingDialog != null) {
                this.loadingDialog.get().dismiss();
                this.loadingDialog = null;
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


    }

}
