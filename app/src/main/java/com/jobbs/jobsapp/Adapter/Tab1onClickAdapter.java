package com.jobbs.jobsapp.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobbs.jobsapp.R;
import com.jobbs.jobsapp.model.CatagaryEmployee;
import com.jobbs.jobsapp.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class Tab1onClickAdapter extends ArrayAdapter<CatagaryEmployee> {

    private final Activity context;
    private final ArrayList<CatagaryEmployee> catagaryEmployees;


    public Tab1onClickAdapter(Activity context,ArrayList<CatagaryEmployee> catagaryEmployees) {
        super(context, R.layout.list_tab1on_click, catagaryEmployees);
        this.context = context;
        this.catagaryEmployees = catagaryEmployees;


    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_tab1on_click, null, true);

        CatagaryEmployee catagaryEmployee = catagaryEmployees.get(position);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_title);
        txtTitle.setText(catagaryEmployee.getName());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_listView);

        ImageView homeServiceImage = (ImageView) rowView.findViewById(R.id.catagory_employee_homeservice_img);

        TextView ageTxt = (TextView) rowView.findViewById(R.id.catagory_employee_age);
        ageTxt.setText("Age "+catagaryEmployee.getAgeFromDOB());

        TextView statusTxt = (TextView) rowView.findViewById(R.id.catagory_employee_status);
        statusTxt.setText(catagaryEmployee.getStatus());
        Log.e("status ",catagaryEmployee.getStatus());

        TextView distanceTxt = (TextView) rowView.findViewById(R.id.distance_listView);
        try{
            if (catagaryEmployee.getImageUrl().equals("default")){
                imageView.setImageResource(R.drawable.camera);
            }else{
                Picasso.with(context).load(catagaryEmployee.getImageUrl()).into(imageView);
            }
        }catch (Exception e){

        }

        distanceTxt.setText(catagaryEmployees.get(position).getDistanceAsString());

        if (catagaryEmployee.getHomeService()){
            homeServiceImage.setImageResource(R.drawable.home);
        }


        return rowView;
    }
}