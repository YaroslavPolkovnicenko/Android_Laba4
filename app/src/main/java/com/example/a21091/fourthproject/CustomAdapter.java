package com.example.a21091.fourthproject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<File> {

    public CustomAdapter(@NonNull Context context, List<File> files) {
        super(context, 0, files);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        File file = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView viewById = (TextView) convertView.findViewById(R.id.text_view);

        if (file != null) {
            viewById.setText(file.getName());
            ImageView img = (ImageView) convertView.findViewById(R.id.img_view);

            Drawable drawable;

            if (file.isDirectory()) {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.folder);

            } else {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.file);
            }

            img.setImageDrawable(drawable);
        }
        return convertView;
    }
}