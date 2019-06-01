package com.deliverycircuit.ehtp.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomViewHolder extends RecyclerView.ViewHolder {

    TextView nameTextView;
    TextView departTextView;
    TextView arriveTextView;
    ImageView crossButtonImageView;

    public CustomViewHolder(View itemView) {
        super(itemView);

        nameTextView = itemView.findViewById(R.id.nameTextView);
        arriveTextView = itemView.findViewById(R.id.arrivee);
        departTextView = itemView.findViewById(R.id.depart);
        crossButtonImageView = itemView.findViewById(R.id.crossImageView);

    }
}
