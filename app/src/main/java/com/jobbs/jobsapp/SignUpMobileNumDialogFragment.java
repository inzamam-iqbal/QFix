package com.jobbs.jobsapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jobbs.jobsapp.database.DbHelper;
import com.jobbs.jobsapp.model.CatagaryEmployee;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.model.SignUpCatagory;
import com.jobbs.jobsapp.model.SignUpLanguage;
import com.jobbs.jobsapp.utils.ImageUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Inzimam on 9/24/2016.
 */
public class SignUpMobileNumDialogFragment extends DialogFragment {

    Button buttonOk;
    private String userId;
    private OkHttpClient client;
    private WeakReference<ProgressDialog> loadingDialog;
    private Employee employee;
    private EditText editText;
    private  FragmentTransaction trans;
    private SharedPreferences sharedPreferences;
    private String pin;
    private Activity activity;
    private Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        activity =getActivity();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    private void checkButtonClick(View view) {




    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        trans = getFragmentManager().beginTransaction();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.signup_phone_num_dialog_fragment,null);
        alertDialogBuilder.setView(view);

        editText = (EditText) view.findViewById(R.id.last_four_digit);

        alertDialogBuilder.setTitle("Phone number to verify")
                .setCancelable(false)
                .setMessage("Enter the last four digits of the miss call you recived to your primary number")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        //MainActivity.adapter.notifyDataSetChanged();
                        pin = editText.getText().toString();
                        employee = TabFragment2.employee;

                        userId =employee.getPhoneNum();

                        employee.setStatus("Hi there I'm using jobbs");


                        validate();

                    }
                });


        return alertDialogBuilder.create();


    }


    private void validate(){
        ShowLoadingMessage(true);
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(30000, TimeUnit.MILLISECONDS);
        b.writeTimeout(30000, TimeUnit.MILLISECONDS);

        client = b.build();

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String requestId=sharedPref.getString("requestedId","requestedId");

        String nameKey = ImageUtils.getName(getActivity().getApplicationContext());


        params.put("myKey", nameKey);
        params.put("num", employee.getPhoneNum());
        params.put("id", requestId);
        params.put("pin", pin);
        JSONObject parameter = new JSONObject(params);
        Log.e("sending", parameter.toString());
        Log.e("myKey", nameKey);
        Log.e("num", employee.getPhoneNum());
        Log.e("id", requestId);
        Log.e("pin", pin);
        //OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url("http://" + "jobsappserver.herokuapp.com/b")
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
                Log.e("response", token);
                token = token.substring(1, token.length() - 1);
                Log.e("response", token);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                mAuth.signInWithCustomToken(token)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("done", "yes");
                                editor.commit();

                                handler = new Handler(){
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.arg1==1){
                                            ShowLoadingMessage(false);
                                            trans.replace(R.id.root_frame, new ViewOwnProfileFragment());
                                            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                            trans.addToBackStack(null);
                                            trans.commit();
                                        }else{
                                            ShowLoadingMessage(false);
                                            Toast.makeText(getActivity().getApplicationContext(), "Signup failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                };


                                CatagaryEmployee catagaryEmployee = new CatagaryEmployee(employee.getName(),
                                        employee.getGender(), employee.getDob(), employee.getHomeService(),
                                        employee.getStatus());

                                ArrayList<String> catagories = new ArrayList<String>();
                                for (String key: employee.getCatagary().keySet()){
                                    catagories.add(key);
                                }

                                DbHelper dbHelper = new DbHelper();
                                dbHelper.saveEmployeeDetail(employee,catagaryEmployee,catagories,
                                        sharedPreferences,handler);

                                Log.e("haha", "signInWithCustomToken:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.e("auth", "signInWithCustomToken", task.getException());
                                    Toast.makeText(getActivity().getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                    ShowLoadingMessage(false);
                                }
                            }
                        });

            }


        });
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
}
