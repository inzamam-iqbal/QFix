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
public class EditProfileBasicDialogFragment extends DialogFragment {

    private Button buttonOk;
    private EditText editText;
    private TextView header;
    private String headertxt;
    private String content;
    private String type;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headertxt = getArguments().getString("header");
        content = getArguments().getString("content");
        type = getArguments().getString("type");
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
        View view = layoutInflater.inflate(R.layout.edit_profile_basic,null);
        alertDialogBuilder.setView(view);

        editText = (EditText) view.findViewById(R.id.edit_profile_basic_edit_text);
        header = (TextView) view.findViewById(R.id.edit_profile_basic_header);

        if (type.equals("email")){
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }

        editText.setText(content);
        header.setText(headertxt);




        alertDialogBuilder.setTitle("Change status")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        String newContent = editText.getText().toString();

                        if (type.equals("name")){
                            if (newContent.length()<3){
                                Toast.makeText(getActivity(),"Invalid name",Toast.LENGTH_SHORT);
                                return;
                            }
                            HashMap<String,Object> childUpdates = new HashMap<>();

                            childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_EMPLOYEE + "/" +
                                    userId + "/" + JobsConstants.FIREBASE_KEY_NAME, newContent);

                            childUpdates.put("/" + JobsConstants.FIREBASE_REFERANCE_CATEGARYEMPLOYEE + "/" +
                                    userId + "/" + JobsConstants.FIREBASE_KEY_NAME, newContent);

                            ref.updateChildren(childUpdates);
                        }else {
                            ref.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                                    child(type).setValue(newContent);
                        }
                        getDialog().dismiss();
                    }
                });


        return alertDialogBuilder.create();


    }

}
