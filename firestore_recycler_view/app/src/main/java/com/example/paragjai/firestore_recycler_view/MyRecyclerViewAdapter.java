package com.example.paragjai.firestore_recycler_view;

import android.content.Context;
import android.content.Intent;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewHolder> {

    Context context;
    ArrayList<RouteDetailsFromFirestore> routeDetailsFromFirestoreArrayList_;

    public int  lastSelectedPosition=-1;


    public MyRecyclerViewAdapter(Context context, ArrayList<RouteDetailsFromFirestore> routeDetailsFromFirestoreArrayList)
    {
        this.context = context;
        this.routeDetailsFromFirestoreArrayList_ = routeDetailsFromFirestoreArrayList;
        lastSelectedPosition = -1;
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
        String routeName = routeDetailsFromFirestoreArrayList_.get(i).getRouteName();
        String[] routeNameArray = routeName.split("_");
        String alteredRouteName = "";
        for(String word: routeNameArray)
        {
            alteredRouteName += word+" ";
        }

        myRecyclerViewHolder.mRouteName.setText(alteredRouteName);
        myRecyclerViewHolder.mMorningStartTime.setText(routeDetailsFromFirestoreArrayList_.get(i).getMornStartTime());
        Log.d("MyRecyclerViewAdapter: ", "lastSelectedPosition : " + myRecyclerViewHolder.lastSelectedPosition + ", position : " + i );
        //myRecyclerViewHolder.mRadioButton.setChecked(myRecyclerViewHolder.lastSelectedPosition == i);
        myRecyclerViewHolder.mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("onCheckedChanged called", "b: " + b);
                if(b) {
                    if (lastSelectedPosition != -1) {
                        //RadioButton rb = (RadioButton) ((MainActivityWithRoutesList) context).linearLayoutManager.getChildAt(lastSelectedPosition).findViewById(R.id.rbRadioButton);
                        RadioButton rb = (RadioButton) ((MainActivityWithRoutesList) myRecyclerViewHolder.mRadioButton.getContext()).linearLayoutManager.getChildAt(lastSelectedPosition).findViewById(R.id.rbRadioButton);
                        rb.setChecked(false);
                    }
                        lastSelectedPosition = i;
                        myRecyclerViewHolder.mRadioButton.setChecked(true);
                        final Intent intent = new Intent(context, StartButtonActivityWithContinousLocationPublishing.class);
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

