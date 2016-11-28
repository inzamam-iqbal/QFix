package com.jobbs.jobsapp;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jobbs.jobsapp.model.Catagaries;
import com.jobbs.jobsapp.model.SignUpCatagory;
import com.jobbs.jobsapp.utils.JobsConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


//Created by Inzimam on 8/21/2016.


public class SignUpJobCatagaryDialogFragment extends DialogFragment {

    ArrayList<SignUpCatagory> listitems =new ArrayList<>();
    MyCustomAdapter dataAdapter = null;
    ListView mylist;
    Button buttonOk;
    ProgressBar progressBar;
    private boolean isEdit = false;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            isEdit = getArguments().getBoolean("edit");
        }catch (Exception e){

        }

        if (isEdit){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


    }

    private class MyCustomAdapter extends ArrayAdapter<SignUpCatagory> {

        private ArrayList<SignUpCatagory> catagoryList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<SignUpCatagory> catagoryList) {
            super(context, textViewResourceId, catagoryList);
            this.catagoryList = new ArrayList<SignUpCatagory>();
            this.catagoryList.addAll(catagoryList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.signup_job_catagary_listview, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.signUp_jobs_cataogary_list_text);
                holder.name = (CheckBox) convertView.findViewById(R.id.signUp_jobs_cataogary_list_checkBox);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        SignUpCatagory signUpCatagory = (SignUpCatagory) cb.getTag();
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        signUpCatagory.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            SignUpCatagory signUpCatagory = catagoryList.get(position);
            holder.name.setText(signUpCatagory.getName());
            holder.name.setChecked(signUpCatagory.isSelected());
            holder.name.setTag(signUpCatagory);

            return convertView;

        }

    }




    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.signup_jobs_catagary_dialog_fragment,null);
        alertDialogBuilder.setView(view);


        mylist = (ListView) view.findViewById(R.id.jobs_cataogary_dialog_frag_list);
        mylist.setVisibility(View.GONE);

        progressBar = (ProgressBar) view.findViewById(R.id.signUp_jobs_cataogary_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(JobsConstants.FIREBASE_REFERANCE_CATEGARY);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Catagaries jobCatagory = data.getValue(Catagaries.class);
                    String jobName =jobCatagory.getName();
                    String jobId= data.getKey();
                    SignUpCatagory signUpCatagory = new SignUpCatagory(jobName, jobId);
                    if(!listitems.contains(signUpCatagory)){
                        listitems.add(signUpCatagory);
                    }
                }

                dataAdapter = new MyCustomAdapter(getActivity(), R.layout.signup_job_catagary_listview,listitems);

                mylist.setAdapter(dataAdapter);

                mylist.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

//                checkButtonClick(view);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        alertDialogBuilder.setTitle("Select Catagories (max:3)")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int count =0;
                        HashMap<String,Boolean> selectedJobs = new HashMap<String, Boolean>();
                        for (SignUpCatagory job:listitems){
                            if (job.isSelected()) {
                                selectedJobs.put(job.getName(),true);
                                count+=1;
                                if (count==4){
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Can't select more than 3 catogaries",Toast.LENGTH_LONG);
                                    return;
                                }
                            }
                        }

                        if (count>0){
                            if (isEdit){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                                        child(JobsConstants.FIREBASE_KEY_CATAGORY).setValue(selectedJobs);
                            }else{
                                TabFragment2.employee.setCatagary(selectedJobs);

                                FragmentManager manager = getActivity().getSupportFragmentManager();

                                SignUpHomeServiceDialogFragment dialog = new SignUpHomeServiceDialogFragment();
                                dialog.show(manager, "Home Service");
                            }


                            getDialog().dismiss();
                        }else {
                            Toast.makeText(getActivity().getApplicationContext(),"you must select " +
                                    "atleast one catagory",Toast.LENGTH_LONG);
                            return;
                        }


                    }
                });


        return alertDialogBuilder.create();


    }



}

