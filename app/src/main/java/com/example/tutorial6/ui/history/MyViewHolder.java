package com.example.tutorial6.ui.history;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorial6.R;
import com.example.tutorial6.databinding.FragmentGalleryBinding;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView startTimeView,stopTimeView,numMovingView;
    ImageView imageView;

    public MyViewHolder(View itemView) {
        super(itemView);

        startTimeView = itemView.findViewById(R.id.startTime);
        stopTimeView = itemView.findViewById(R.id.stopTime);
        numMovingView = itemView.findViewById(R.id.numMoving);
        imageView = itemView.findViewById(R.id.imageView);


    }
}
