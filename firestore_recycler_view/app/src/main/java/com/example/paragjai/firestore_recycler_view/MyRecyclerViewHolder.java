package com.example.paragjai.firestore_recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView mRouteName;
    public TextView mMorningStartTime;
    public RadioButton mRadioButton;
    public int  lastSelectedPosition;

    public MyRecyclerViewHolder(View itemView)
    {
        super(itemView);
        mRouteName = itemView.findViewById(R.id.tvRouteName);
        mMorningStartTime = itemView.findViewById(R.id.tvMorningStartTime);
        mRadioButton = itemView.findViewById(R.id.rbRadioButton);

/*        mRadioButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v)
                {
                    MyRecyclerViewAdapter myRecyclerViewAdapter;
                    lastSelectedPosition = getAdapterPosition();
                    //notifyDataSetChanged();
                    Toast.makeText(v.getContext(), "selected route :" + mRouteName.getText() + " lastSelectedPosition : " + lastSelectedPosition, Toast.LENGTH_LONG).show();
                }
        });*/

    }



}



