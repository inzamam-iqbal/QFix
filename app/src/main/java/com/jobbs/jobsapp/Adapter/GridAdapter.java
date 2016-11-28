package com.jobbs.jobsapp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobbs.jobsapp.R;
import com.jobbs.jobsapp.model.Catagaries;
import com.jobbs.jobsapp.model.CatagaryEmployee;
import com.jobbs.jobsapp.utils.ImageUtils;

import java.util.ArrayList;


public class GridAdapter extends ArrayAdapter<Catagaries> {

    private final Activity context;
    private final ArrayList<Catagaries> catagaries;

    public GridAdapter(Activity context, ArrayList<Catagaries> catagaries) {
        super(context, R.layout.grid_jobs, catagaries);
        this.context = context;
        this.catagaries = catagaries;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.grid_jobs, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.jobsImage);

        txtTitle.setText(catagaries.get(position).getName());
        try{
            ImageUtils.LoadImage(context,catagaries.get(position).getImage(),imageView,catagaries.get(position).getName()+"pic");
        }catch (Exception e){

        }
        return rowView;
    }
}