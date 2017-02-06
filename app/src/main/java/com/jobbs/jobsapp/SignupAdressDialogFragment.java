package com.jobbs.jobsapp;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobbs.jobsapp.model.Employee;
import com.jobbs.jobsapp.utils.JobsConstants;



/**
 * Created by Inzimam on 11/29/2016.
 */
public class SignupAdressDialogFragment extends DialogFragment {

    private Employee employee;
    private boolean isEdit = false;
    private String oldAddress;
    private String userId;
    private String address;
    private EditText editTextAddress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            isEdit = getArguments().getBoolean("edit");
            oldAddress = getArguments().getString("address");
        }catch (Exception e){

        }

        if (isEdit){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.signup_address_dialog_frag,null);
        alertDialogBuilder.setView(view);

        editTextAddress = (EditText) view.findViewById(R.id.signUp_address);
        if (isEdit){
            editTextAddress.setText(oldAddress);
        }

        alertDialogBuilder.setTitle("Address")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        //MainActivity.pagerAdapter.notifyDataSetChanged();
                        address = editTextAddress.getText().toString();
                        employee = TabFragment2.employee;

                        if(isEdit){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                                    child(JobsConstants.FIREBASE_KEY_ADDRESS).setValue(address);



                        }else{
                            employee.setAddress(address);

                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            SignupAboutDialogFragment dialog = new SignupAboutDialogFragment();
                            dialog.setCancelable(false);
                            dialog.show(manager, "About your self");
                        }


                    }
                });


        return alertDialogBuilder.create();

    }
}
