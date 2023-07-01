package com.example.tutorial6.ui.history;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorial6.R;
import com.example.tutorial6.databinding.FragmentGalleryBinding;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView,emailView;

    public MyViewHolder(View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imageView);
        nameView = itemView.findViewById(R.id.name);
        emailView = itemView.findViewById(R.id.email);


    }
}
