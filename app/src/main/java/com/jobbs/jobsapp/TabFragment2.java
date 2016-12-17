package com.jobbs.jobsapp;


import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.jobbs.jobsapp.database.DbHelper;
import com.jobbs.jobsapp.model.Catagaries;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.model.SignUpCatagory;
import com.jobbs.jobsapp.model.SignUpLanguage;
import com.jobbs.jobsapp.signUp.CallReceiver;
import com.jobbs.jobsapp.utils.ImageCompressionAsyncTask;
import com.jobbs.jobsapp.utils.ImageUtils;
import com.jobbs.jobsapp.utils.JobsConstants;
import com.jobbs.jobsapp.utils.utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;


public class TabFragment2 extends Fragment {

    private EditText name;
    private EditText email;
    private DatePicker datePicker;
    private CountryCodePicker primaryCountryCode;
    private CountryCodePicker secondaryCountryCode;
    private EditText primaryNum;
    private EditText secondaryNum;
    private ImageView profilePic;
    private Button submitBtn;
    private RadioGroup genderGroup;
    private DatabaseReference mRef;
    public static Employee employee;
    private Uri imageToUploadUri;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int CAMERA_REQUEST =2;
    private String imageDownloadUrl;
    private String imagePath;
    private int day;
    private int month;
    private int year;
    private RadioButton selctedGender;
    private File imageFile;
    private WeakReference<ProgressDialog> loadingDialog;
    private Timer timer;
    private boolean gotPin = false;
    private String dialingNumber;
    private String nameKey;
    private String linkv;
    private FirebaseAuth mAuth;
    private TimerTask timerTask;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String link;
    OkHttpClient client;
    private SharedPreferences sharedPref;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals(CallReceiver.MSG_CALL_START)) {
                Log.e("detected", "t");
                if (intent.getBooleanExtra("incoming", false) && gotPin) {
                    HangupCall();
                    Log.e("detected", "in");
                    gotPin=false;
                    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Map<String, String> params = new HashMap<String, String>();
                    String recivedNum = intent.getStringExtra("number");
                    params.put("myKey", nameKey);
                    params.put("num", dialingNumber);
                    params.put("id", requestId.toString());
                    params.put("pin", recivedNum.substring(recivedNum.length() - 4));
                    JSONObject parameter = new JSONObject(params);
                    Log.e("sending", parameter.toString());
                    //OkHttpClient client = new OkHttpClient();


                    RequestBody body = RequestBody.create(JSON, parameter.toString());
                    Request request = new Request.Builder()
                            .url("http://" + linkv)
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

                            mAuth.signInWithCustomToken(token)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Log.e("haha", "signInWithCustomToken:onComplete:" + task.isSuccessful());
                                            ShowLoadingMessage(false);
                                            gotPin = false;
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

            }
            if (intent.getAction().equals(CallReceiver.MSG_CALL_END)) {
                if (!intent.getBooleanExtra("incoming", false)) {
                    Log.e("detected", "other");
                }

            }
        }
    };
    private Object requestId;


    private void StopReverseCliTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
    }

    private void HangupCall() {
        try {
            TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm);
            c = Class.forName(telephonyService.getClass().getName());
            m = c.getDeclaredMethod("endCall");
            m.setAccessible(true);
            m.invoke(telephonyService);
        } catch (Exception ex) {
            Log.e("Failed to hangup the", ex.getMessage());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        nameKey = "-11980787113102610";

       // name = ImageUtils.getName(getActivity().getApplicationContext());
        link = "jobsappserver.herokuapp.com/a";
        linkv = "jobsappserver.herokuapp.com/b";

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e("logged","in");
                } else {
                    // User is signed out
                }
                // ...
            }
        };


        //////////////////////





        getActivity().registerReceiver(receiver, new IntentFilter(CallReceiver.MSG_CALL_START));
        getActivity().registerReceiver(receiver, new IntentFilter(CallReceiver.MSG_CALL_END));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab2, container, false);

        name = (EditText) rootView.findViewById(R.id.signup_name);
        email = (EditText) rootView.findViewById(R.id.signup_email);
        primaryNum = (EditText) rootView.findViewById(R.id.signUp_phone_txt);
        secondaryNum = (EditText) rootView.findViewById(R.id.signUp_phone_txt1);
        datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
        primaryCountryCode = (CountryCodePicker) rootView.findViewById(R.id.ccp);
        secondaryCountryCode = (CountryCodePicker) rootView.findViewById(R.id.ccp1);
        profilePic = (ImageView) rootView.findViewById(R.id.imageView);
        submitBtn = (Button) rootView.findViewById(R.id.button);
        genderGroup = (RadioGroup) rootView.findViewById(R.id.signUp_gender_group);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!utils.IsNetworkConnected(getActivity())){
                    Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String[] perms = {android.Manifest.permission.CAMERA};
                        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
                            // Already have permission, do the thing
                            showInputDialog(getActivity());
                            // ...
                        } else {
                            // Do not have permissions, request them now
                            EasyPermissions.requestPermissions(getActivity(), "We need Camera permission to send images",
                                    101, perms);
                            return;
                        }
                    }else{
                        showInputDialog(getActivity());
                    }

                }

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(!utils.IsNetworkConnected(getActivity())){
                    Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    return;
                }




                int selectedId = genderGroup.getCheckedRadioButtonId();
                selctedGender = (RadioButton) rootView.findViewById(selectedId);

                day = datePicker.getDayOfMonth();
                month = datePicker.getMonth() + 1;
                year = datePicker.getYear();

                //showJobDialog(v);
                //showLanguageDialog(v);
                if(name.getText().toString().length()<3 || name.getText().toString() == null){
                    Toast.makeText(getActivity().getApplicationContext(),"Invalid name", Toast.LENGTH_LONG).show();
                }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    Toast.makeText(getActivity().getApplicationContext(),"Invalid email", Toast.LENGTH_LONG).show();
                }else if (primaryNum.getText().toString().length()<7){
                    Toast.makeText(getActivity().getApplicationContext(),"Invalid primary Phone Number", Toast.LENGTH_LONG).show();
                }else if (year<1930 || year>2005){
                    Toast.makeText(getActivity().getApplicationContext(),"Invalid Date of birth", Toast.LENGTH_LONG).show();
                }else if(selctedGender.getText().toString()==null){
                    Toast.makeText(getActivity().getApplicationContext(),"No Gender selected", Toast.LENGTH_LONG).show();
                }else{
                    OnClickValidate();

                    ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                        @Override
                        protected void onPostExecute(byte[] imageBytes) {
                            // image here is compressed & ready to be sent to the server
                            //Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            DbHelper dbHelper = new DbHelper(getActivity().getApplicationContext());

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://jobs-8d5e5.appspot.com");



                            UploadTask uploadTask = storageRef.child("users").child(primaryNum.getText().toString()).
                                    child(JobsConstants.STORAGE_REFERANCE_PROFILEPIC).putBytes(imageBytes);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                                    if (secondaryNum.length()>7){
                                        employee = new Employee(name.getText().toString(),selctedGender.getText().toString(),
                                                primaryCountryCode.getFullNumberWithPlus()+primaryNum.getText().toString(),
                                                secondaryCountryCode.getFullNumberWithPlus()+secondaryNum.getText().toString(),
                                                email.getText().toString(), Integer.toString(year)+"-"+ Integer.toString(month)
                                                +"-"+ Integer.toString(day),downloadUrl);
                                    }else{
                                        employee = new Employee(name.getText().toString(),selctedGender.getText().toString(),
                                                primaryCountryCode.getFullNumberWithPlus()+primaryNum.getText().toString(),
                                                null,email.getText().toString(),
                                                Integer.toString(year)+"-"+ Integer.toString(month)
                                                +"-"+ Integer.toString(day),downloadUrl);
                                    }


                                    ShowLoadingMessage(false);
                                    showJobDialog(v);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        }
                    };
                    if (imagePath !=null){
                        imageCompression.execute(imagePath);// imagePath as a string
                        ShowLoadingMessage(true);
                    }else{
                        employee = new Employee(name.getText().toString(),selctedGender.getText().toString(),
                                primaryCountryCode.getFullNumberWithPlus()+primaryNum.getText().toString(),
                                secondaryCountryCode.getFullNumberWithPlus()+primaryNum.getText().toString(),
                                email.getText().toString(), Integer.toString(year)+"-"+ Integer.toString(month)
                                +"-"+ Integer.toString(day),"default");
                        showJobDialog(v);
                    }

                    
                }




            }

        });

        return rootView;
    }

    public void showJobDialog(View view) {

        FragmentManager manager = getActivity().getSupportFragmentManager();

        SignUpJobCatagaryDialogFragment dialog = new SignUpJobCatagaryDialogFragment();
        dialog.setCancelable(false);
        dialog.show(manager, "Job Catagory");

    }

    protected  void showInputDialog(final Activity context) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View promptView = layoutInflater.inflate(R.layout.camera_options, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);


        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        ImageButton x=(ImageButton)promptView.findViewById(R.id.imageButton);
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = new File(Environment.getExternalStorageDirectory(), "POST_IMAGE.jpg");
                chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                imageToUploadUri = Uri.fromFile(imageFile);
                startActivityForResult(chooserIntent, CAMERA_REQUEST);

//                Intent i=new Intent(promptView.getContext(),Main2Activity.class);
//                father.startActivity(i);
                alert.cancel();

            }
        });

        ImageButton x1=(ImageButton)promptView.findViewById(R.id.imageButton2);
        x1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);

//                Intent i=new Intent(promptView.getContext(),Main2Activity.class);
//                father.startActivity(i);
                alert.cancel();

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
            imagePath = ImageUtils.getImagePathFromGallery(getActivity().getApplicationContext(), data);
            //String imagePath = db.getImageUrlWithAuthority(getApplicationContext(),data.getData());
            Log.e("f", imagePath);
            Uri imageUri = Uri.parse(imagePath);
            //bitmap = ImageUtils.getImageFromPath(getApplicationContext(), imagePath);
            Picasso.with(getActivity()).load(new File(imageUri.getPath())).into(profilePic);


        }else if(requestCode == CAMERA_REQUEST && resultCode == -1 ) {
            Uri selectedImage = imageToUploadUri;
            Log.e("atleast",selectedImage.getPath());
            getActivity().getContentResolver().notifyChange(selectedImage, null);
            imagePath = selectedImage.getPath();
            Picasso.with(getActivity()).load(imageFile).into(profilePic);

        }

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

    private void OnClickValidate() {

        Log.e("onClick","came");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(getActivity(), perms)) {
                // Already have permission, do the thing
                // ...
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(this, "We need Phone State permission to verify your account" +
                                "and  write storage permissions are essential for the functioning of this app",
                        100, perms);
                return;
            }
        }

        Log.e("onClick","came3");
        dialingNumber = primaryCountryCode.getFullNumberWithPlus().toString()+primaryNum.getText().toString();
        if (!utils.IsNetworkConnected(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
            return;
        }

        if (dialingNumber.length() < 8) {
            Toast.makeText(getActivity().getApplicationContext(), "Invalid phone number", Toast.LENGTH_LONG).show();
            return;
        }



        Log.e("onClick","came4");
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("myKey", nameKey);
        Log.e("keeey",nameKey);
        params.put("num", dialingNumber);
        JSONObject parameter = new JSONObject(params);

        Builder b = new Builder();
        b.readTimeout(30000, TimeUnit.MILLISECONDS);
        b.writeTimeout(30000, TimeUnit.MILLISECONDS);

        client = b.build();


        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request request = new Request.Builder()
                .url("http://" + link)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();
        Log.e("link",link);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                //Toast.makeText(getApplicationContext(),"Please try again",Toast.LENGTH_LONG).show();
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
                String reply = response.body().string();
                Log.e("response", reply);
                try {
                    JSONObject resp = new JSONObject(reply);
                    requestId = resp.get("id");
                    gotPin = true;
                    Log.e("gotpin", gotPin + "");

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("requestedId", requestId.toString());
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}