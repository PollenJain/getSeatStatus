package com.example.paragjai.firestore_recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyRecyclerViewHolderForStops extends RecyclerView.ViewHolder {

    public TextView mStopName;
    public TextView mExpectedPickUpTime;
    public RelativeLayout mViewForeground;
    public RelativeLayout mViewBackground;

    public MyRecyclerViewHolderForStops(View itemView) {
        super(itemView);
        mStopName = itemView.findViewById(R.id.tvStopName1);
        mExpectedPickUpTime = itemView.findViewById(R.id.tvExpectedPickupTime1);
        mViewForeground = itemView.findViewById(R.id.view_foreground);
        mViewForeground = itemView.findViewById(R.id.view_background);
    }




}
