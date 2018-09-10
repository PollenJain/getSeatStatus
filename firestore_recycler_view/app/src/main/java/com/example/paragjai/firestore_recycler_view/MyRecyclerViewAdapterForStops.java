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

//CardListAdapter
public class MyRecyclerViewAdapterForStops extends RecyclerView.Adapter<MyRecyclerViewHolderForStops> {

    Context context;
    ArrayList<StopDetailsFromFirestore> stopDetailsFromFirestoreArrayList_;

    public MyRecyclerViewAdapterForStops(Context context, ArrayList<StopDetailsFromFirestore> stopDetailsFromFirestoreArrayList)
    {
        this.context = context;
        this.stopDetailsFromFirestoreArrayList_ = stopDetailsFromFirestoreArrayList;
    }

    @NonNull
    @Override
    public MyRecyclerViewHolderForStops onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        // LayoutInflater layoutInflater = LayoutInflater.from(mainActivity_.getBaseContext());
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.card_list_item_stops, viewGroup, false);
        return new MyRecyclerViewHolderForStops(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRecyclerViewHolderForStops myRecyclerViewHolderForStops, final int i) {
        myRecyclerViewHolderForStops.mStopName.setText(stopDetailsFromFirestoreArrayList_.get(i).getStopName());
        myRecyclerViewHolderForStops.mExpectedPickUpTime.setText(stopDetailsFromFirestoreArrayList_.get(i).getExpectedPickupTime());
    }

    @Override
    public int getItemCount() {
        return stopDetailsFromFirestoreArrayList_.size();
    }

    public void removeItem(int position)
    {
        stopDetailsFromFirestoreArrayList_.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(StopDetailsFromFirestore stopDetailsFromFirestore, int position)
    {
        stopDetailsFromFirestoreArrayList_.add(position, stopDetailsFromFirestore);
        notifyItemInserted(position);
    }

}

