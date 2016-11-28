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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobbs.jobsapp.model.SignUpLanguage;
import com.jobbs.jobsapp.utils.JobsConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Inzimam on 8/22/2016.
 */
public class SignUpLanguageDialogFragment extends DialogFragment {

    ArrayList<SignUpLanguage> languageArray;

    ListView mylist;
    Button buttonOk;
    MyCustomAdapter dataAdapter;
    private boolean isEdit = false;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        languageArray = new ArrayList<>();
        languageArray.add(new SignUpLanguage("Sinhala"));
        languageArray.add(new SignUpLanguage("Tamil"));
        languageArray.add(new SignUpLanguage("English"));

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
    private class MyCustomAdapter extends ArrayAdapter<SignUpLanguage> {

        private ArrayList<SignUpLanguage> languageList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<SignUpLanguage> languageList) {
            super(context, textViewResourceId, languageList);
            this.languageList = new ArrayList<SignUpLanguage>();
            this.languageList.addAll(languageList);
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
                convertView = vi.inflate(R.layout.signup_language_listview, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.signUp_language_list_text);
                holder.name = (CheckBox) convertView.findViewById(R.id.signUp_language_list_checkBox);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        SignUpLanguage signUpLanguage = (SignUpLanguage) cb.getTag();
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        signUpLanguage.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            SignUpLanguage signUpLanguage = languageList.get(position);
            holder.name.setText(signUpLanguage.getName());
            holder.name.setChecked(signUpLanguage.isSelected());
            holder.name.setTag(signUpLanguage);

            return convertView;

        }

    }

    private void checkButtonClick(View view) {



    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.signup_languages_dialog_frag,null);
        alertDialogBuilder.setView(view);


        mylist = (ListView) view.findViewById(R.id.signup_languages_frag_list);

        dataAdapter = new MyCustomAdapter(getActivity(), R.layout.signup_language_listview,languageArray);

        mylist.setAdapter(dataAdapter);

//        checkButtonClick(view);


        alertDialogBuilder.setTitle("Select Language")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int count =0;
                        HashMap<String, Boolean> selectedLanguages = new HashMap<String, Boolean>();
                        for (SignUpLanguage language:languageArray){
                            if (language.isSelected()) {
                                selectedLanguages.put(language.getName(),true);
                                count++;
                            }
                        }

                        if (count>0){
                            if (isEdit){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                ref.child(JobsConstants.FIREBASE_REFERANCE_EMPLOYEE).child(userId).
                                        child(JobsConstants.FIREBASE_KEY_LANGUAGES).setValue(selectedLanguages);
                            }else{
                                TabFragment2.employee.setLanguages(selectedLanguages);
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                SignupAboutDialogFragment dialog = new SignupAboutDialogFragment();
                                dialog.setCancelable(false);
                                dialog.show(manager, "Address and About");
                            }

                            getDialog().dismiss();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),"you must select atleast one" +
                                    " language", Toast.LENGTH_LONG);
                            return;
                        }
                    }
                });


        return alertDialogBuilder.create();


    }

}
