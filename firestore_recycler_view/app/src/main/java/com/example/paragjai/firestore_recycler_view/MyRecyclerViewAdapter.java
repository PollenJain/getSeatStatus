package com.example.paragjai.firestore_recycler_view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewHolder> {

    Context context;
    ArrayList<RouteDetailsFromFirestore> routeDetailsFromFirestoreArrayList_;

    public int  lastSelectedPosition=-1;


    public MyRecyclerViewAdapter(Context context, ArrayList<RouteDetailsFromFirestore> routeDetailsFromFirestoreArrayList)
    {
        this.context = context;
        this.routeDetailsFromFirestoreArrayList_ = routeDetailsFromFirestoreArrayList;
    }


    @NonNull
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        // LayoutInflater layoutInflater = LayoutInflater.from(mainActivity_.getBaseContext());
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.route_details, viewGroup, false);
        return new MyRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRecyclerViewHolder myRecyclerViewHolder, final int i) {

        myRecyclerViewHolder.mRouteName.setText(routeDetailsFromFirestoreArrayList_.get(i).getRouteName());
        myRecyclerViewHolder.mMorningStartTime.setText(routeDetailsFromFirestoreArrayList_.get(i).getMornStartTime());
        Log.d("MyRecyclerViewAdapter: ", "lastSelectedPosition : " + myRecyclerViewHolder.lastSelectedPosition + ", position : " + i );
        //myRecyclerViewHolder.mRadioButton.setChecked(myRecyclerViewHolder.lastSelectedPosition == i);
        myRecyclerViewHolder.mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if (lastSelectedPosition != -1) {
                        // RadioButton rb = (RadioButton) ((MainActivity) context).linearLayoutManager.getChildAt(lastSelectedPosition).findViewById(R.id.rbRadioButton);
                        RadioButton rb = (RadioButton) ((MainActivity) myRecyclerViewHolder.mRadioButton.getContext()).linearLayoutManager.getChildAt(lastSelectedPosition).findViewById(R.id.rbRadioButton);
                        rb.setChecked(false);
                    }
                        lastSelectedPosition = i;
                        myRecyclerViewHolder.mRadioButton.setChecked(true);
                        final Intent intent = new Intent(context, StartButtonActivity.class);
                        intent.putExtra("selected_route_name", routeDetailsFromFirestoreArrayList_.get(i).getRouteName());
                        context.startActivity(intent);
                    }
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeDetailsFromFirestoreArrayList_.size();
    }

}

