package com.jobbs.jobsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.ImageUtils;
import com.jobbs.jobsapp.utils.JobsConstants;
import com.squareup.picasso.Picasso;

public class ViewProfile extends AppCompatActivity {
    //UI stuff
    private TextView text_callButton,text_about,text_contacts,text_age,text_email,text_address,
            textView_catogory,textView_language, txt_name;
    private Button btn_call;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        final String employeeId = getIntent().getStringExtra("EmployeeId");

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().
                child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(employeeId);

        //init UI stuff
        txt_name = (TextView) findViewById(R.id.textView_name);
        btn_call = (Button) findViewById(R.id.button_call);
        text_about=(TextView)findViewById(R.id.textView_about);
        text_contacts=(TextView)findViewById(R.id.textView_contactNumber);
        text_age=(TextView)findViewById(R.id.textView_age);
        text_email=(TextView)findViewById(R.id.textView_email);
        text_address=(TextView)findViewById(R.id.textView_address);
        textView_catogory=(TextView) findViewById(R.id.textView_catogory);
        textView_language=(TextView) findViewById(R.id.textView_language);
        profilePic=(ImageView)findViewById(R.id.imageView_profilePic);


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                findViewById(R.id.view_profile_main_layout).setVisibility(View.VISIBLE);
                Employee employee = dataSnapshot.getValue(Employee.class);
                txt_name.setText(employee.getName());
                text_about.setText(employee.getAbout());
                text_contacts.setText(employee.getPhoneNumSecondary());
                text_age.setText(employee.getAgeFromDOB());
                text_email.setText(employee.getEmail());
                text_address.setText(employee.getAddress());
                textView_catogory.setText(employee.getCatagoryAsString());
                textView_language.setText(employee.getLanguageAsString());
                btn_call.setText("Call "+ employee.getName());

                btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+employeeId));
                        startActivity(callIntent);
                    }
                });

                if (employee.getImageUrl().equals("default")){
                    profilePic.setImageResource(R.drawable.camera);
                }else{
                    Picasso.with(getApplicationContext()).load(employee.getImageUrl()).into(profilePic);
                }
                //textView_status.setText(employee);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
