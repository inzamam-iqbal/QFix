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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobbs.jobsapp.database.DbHelper;
import com.jobbs.jobsapp.model.Catagaries;
import com.jobbs.jobsapp.model.CatagaryEmployee;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.ImageUtils;
import com.jobbs.jobsapp.utils.JobsConstants;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private OkHttpClient client;
    private FragmentTransaction trans;
    private CatagaryEmployee catagaryEmployee;
    private DatabaseReference dbRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        dbRef = FirebaseDatabase.getInstance().getReference();
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

        trans = getFragmentManager().beginTransaction();

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
                                Log.e("home service2",employee.getHomeService()+"");
                            }
                            validate();
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

    private void validate(){
        ShowLoadingMessage(true);

        final SharedPreferences sharedPref= getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
        String authToken=sharedPref.getString("cat","abc");



        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(30000, TimeUnit.MILLISECONDS);
        b.writeTimeout(30000, TimeUnit.MILLISECONDS);

        client = b.build();

        String nameKey = ImageUtils.getName(getActivity().getApplicationContext());


        //OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(JSON, authToken.toString());
        Request request = new Request.Builder()
                .url("http://" + "jobsappserver.herokuapp.com/test")
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "Couldn't verify, Please try again", Toast.LENGTH_SHORT).show();
                        ShowLoadingMessage(false);
                        e.printStackTrace();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String token = response.body().string();
                if (token.length()>10){
                    Log.e("response", token);
                    token = token.substring(1, token.length() - 1);
                    Log.e("response", token);

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    mAuth.signInWithCustomToken(token)
                            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    employee.setStatus("Hi there, Do you need my help?");

                                    CatagaryEmployee catagaryEmployee = new CatagaryEmployee(employee.getName(),
                                            employee.getGender(), employee.getDob(), employee.getHomeService(),
                                            employee.getStatus());

                                    ArrayList<String> catagories = new ArrayList<String>();
                                    for (String key: employee.getCatagary().keySet()){
                                        catagories.add(key);
                                    }

                                    catagaryEmployee.setImageUrl(employee.getImageUrl());
                                    HashMap<String,Object> childUpdates = new HashMap<>();
                                    childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_EMPLOYEE + "/" + employee.getPhoneNum(),employee.toMap());

                                    String loginKey = dbRef.child(JobsConstants.FIREBASE_REFERANCE_LOGIN_KEY).child(employee.getPhoneNum()).push().getKey();
                                    childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_CATAGORYEMPLOYEE + "/" + employee.getPhoneNum(), catagaryEmployee.toMap());
                                    childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_LOGIN_KEY + "/" + employee.getPhoneNum() + "/" + loginKey, true);

                                    dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("done", "yes");
                                            editor.commit();

                                            ShowLoadingMessage(false);
                                            trans.replace(R.id.root_frame, new ViewOwnProfileFragment());
                                            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                            trans.addToBackStack(null);
                                            trans.commit();

                                        }
                                    });





                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.e("auth", "signInWithCustomToken", task.getException());
                                        Toast.makeText(activity.getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                        ShowLoadingMessage(false);
                                    }
                                }
                            });
                }else{
                    ShowLoadingMessage(false);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(),"False pin, try again",Toast.LENGTH_LONG).show();
                        }
                    });

                }


            }


        });
    }

    private void changeFragment(){
        FragmentManager manager = activity.getSupportFragmentManager();
        ShowLoadingMessage(false);
        Log.e("ca","came");
        SignUpMobileNumDialogFragment dialog = new SignUpMobileNumDialogFragment();
        dialog.setCancelable(false);
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
