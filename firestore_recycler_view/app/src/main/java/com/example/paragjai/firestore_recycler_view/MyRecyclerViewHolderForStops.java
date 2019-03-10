package com.example.paragjai.firestore_recycler_view;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davidmiguel.numberkeyboard.NumberKeyboard;

import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.parseColor;

public class MyRecyclerViewHolderForStops extends RecyclerView.ViewHolder {

    public TextView mStopName;
    public TextView mExpectedPickUpTime;
    public RelativeLayout mViewForeground;
    public RelativeLayout mViewBackground;
    public TextView mDigit;
    public NumberKeyboard mNumberKeyboard;
    public Context mContext;

    public MyRecyclerViewHolderForStops(View itemView, Context context) {
        super(itemView);
        mContext = context;
        mStopName = itemView.findViewById(R.id.tvStopName1);
        mExpectedPickUpTime = itemView.findViewById(R.id.tvExpectedPickupTime1);
        mViewForeground = itemView.findViewById(R.id.view_foreground);
        mViewForeground = itemView.findViewById(R.id.view_background);
        mDigit = itemView.findViewById(R.id.digit);
        mNumberKeyboard = itemView.findViewById(R.id.numberKeyboard);
        mNumberKeyboard.showLeftAuxButton();
        mNumberKeyboard.setLeftAuxButtonBackground(R.drawable.send_icon_green_24);
    }


}
