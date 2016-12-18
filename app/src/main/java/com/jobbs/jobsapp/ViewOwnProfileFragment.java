package com.jobbs.jobsapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompatBase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jobbs.jobsapp.database.DbHelper;
import com.jobbs.jobsapp.model.Catagaries;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.ImageCompressionAsyncTask;
import com.jobbs.jobsapp.utils.ImageUtils;
import com.jobbs.jobsapp.utils.JobsConstants;
import com.jobbs.jobsapp.utils.utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Inzimam on 9/25/2016.
 */
public class ViewOwnProfileFragment extends Fragment {

    private TextView text_callButton,text_about,text_contacts,text_age,text_email,text_address,
            textView_catogory,textView_language, textView_status,textView_name;
    private Button btn_status;
    private ImageView profilePic;
    private File imageFile;
    private Uri imageToUploadUri;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int CAMERA_REQUEST =2;
    private String imagePath;
    private Employee employee;
    private HashMap<String,Boolean> catagoryNames;
    private WeakReference<ProgressDialog> loadingDialog;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_labours_view, container, false);


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().
                child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId);

        textView_name = (TextView) view.findViewById(R.id.textView_name);
        text_callButton=(TextView)view.findViewById(R.id.button_status);
        text_about=(TextView)view.findViewById(R.id.textView_about);
        text_contacts=(TextView)view.findViewById(R.id.textView_contactNumber);
        text_age=(TextView)view.findViewById(R.id.textView_age);
        text_email=(TextView)view.findViewById(R.id.textView_email);
        text_address=(TextView)view.findViewById(R.id.textView_address);
        textView_catogory=(TextView) view.findViewById(R.id.textView_catogory);
        textView_language=(TextView) view.findViewById(R.id.textView_language);
        profilePic=(ImageView)view.findViewById(R.id.imageView_profilePic);
        textView_status = (TextView) view.findViewById(R.id.textView_status);
        btn_status = (Button) view.findViewById(R.id.button_status);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                view.findViewById(R.id.labours_view_main_layout).setVisibility(View.VISIBLE);
                employee = dataSnapshot.getValue(Employee.class);
                textView_name.setText(employee.getName());
                text_about.setText(employee.getAbout());
                text_contacts.setText(employee.getPhoneNumSecondary());
                text_age.setText(employee.getAgeFromDOB());
                text_email.setText(employee.getEmail());
                text_address.setText(employee.getAddress());
                textView_catogory.setText(employee.getCatagoryAsString());
                textView_language.setText(employee.getLanguageAsString());
                textView_status.setText(employee.getStatus());
                if (employee.getImageUrl().equals("default")){
                    profilePic.setImageResource(R.drawable.camera);
                }else{
                    Picasso.with(getActivity()).load(employee.getImageUrl()).into(profilePic);
                }

                catagoryNames = employee.getCatagary();
                //textView_status.setText(employee);

                btn_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("txt", employee.getStatus());

                        ChangeStatusDialogFragment dialog = new ChangeStatusDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "Change Status");
                    }
                });

                textView_name.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("header", "Change Name");
                        args.putString("content", employee.getName());
                        args.putString("type", "name");

                        EditProfileBasicDialogFragment dialog = new EditProfileBasicDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "Change Status");
                        return false;
                    }
                });

                text_email.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("header", "Change Email");
                        args.putString("content", employee.getEmail());
                        args.putString("type", "email");

                        EditProfileBasicDialogFragment dialog = new EditProfileBasicDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "Change Status");
                        return false;
                    }
                });

                textView_catogory.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putBoolean("edit", true);

                        ArrayList<String> jobNames = new ArrayList<>(catagoryNames.keySet());

                        args.putStringArrayList("catagories",jobNames);

                        SignUpJobCatagaryDialogFragment dialog = new SignUpJobCatagaryDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "Job Catagory");
                        return false;
                    }
                });

                text_about.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Bundle args = new Bundle();
                        args.putString("about", employee.getAbout());
                        args.putBoolean("edit", true);

                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        SignupAboutDialogFragment dialog = new SignupAboutDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "Address and About");
                        return false;
                    }
                });

                text_address.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("header", "Change Address");
                        args.putString("content", employee.getAddress());
                        args.putString("type", "address");

                        EditProfileBasicDialogFragment dialog = new EditProfileBasicDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "Change Address");
                        return false;
                    }
                });

                textView_language.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putBoolean("edit", true);

                        SignUpLanguageDialogFragment dialog = new SignUpLanguageDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "Select Language");

                        return false;
                    }
                });

                text_contacts.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("content", employee.getPhoneNumSecondary());

                        ChangeSecondaryNumberDialogFrag dialog = new ChangeSecondaryNumberDialogFrag();
                        dialog.setArguments(args);
                        dialog.show(manager, "Change Secondary number");
                        return false;
                    }
                });

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

                text_age.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();

                        Bundle args = new Bundle();
                        args.putString("content", employee.getDob());

                        EditDobDialogFragment dialog = new EditDobDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "Change Date of Birth");
                        return false;
                    }
                });

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }


    protected  void showInputDialog(final Activity context) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View promptView = layoutInflater.inflate(R.layout.camera_options, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);


        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
//
//        WindowManager.LayoutParams lp= new WindowManager.LayoutParams();
//        lp.width=500;
//        lp.height=200;
//        lp.x=0;
//        lp.y=0;
//        alert.getWindow().setAttributes(lp);


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

        ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
            @Override
            protected void onPostExecute(byte[] imageBytes) {
                // image here is compressed & ready to be sent to the server
                //Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                DbHelper dbHelper = new DbHelper(getActivity().getApplicationContext());
                ShowLoadingMessage(true);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://jobs-8d5e5.appspot.com");


                UploadTask uploadTask = storageRef.child(employee.getPhoneNum()).
                        child(JobsConstants.STORAGE_REFERANCE_PROFILEPIC).putBytes(imageBytes);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                                child(JobsConstants.FIREBASE_KEY_IMAGE_URL).
                                setValue(downloadUrl);
                        ShowLoadingMessage(false);

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
}
