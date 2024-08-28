package com.example.navbotdialog;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<ListData> {


    public ListAdapter(@NonNull Context context, ArrayList<ListData> dataArrayList) {
        super(context, R.layout.listitem, dataArrayList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListData listData = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
        }

        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listName = view.findViewById(R.id.listFirstname);
        TextView listLastname = view.findViewById(R.id.listLastname);

        assert listData != null;
        if (listData.image.equals(""))
        {
            listImage.setImageResource(R.drawable.person);
        }
        else
        {
            Uri uri = Uri.parse(listData.image);
            listImage.setImageURI(uri);
        }
        listName.setText(listData.firstname);
        listLastname.setText(listData.lastname);

        return view;
    }
}
