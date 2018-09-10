package com.example.paragjai.firestore_recycler_view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.support.v7.widget.Toolbar;

import com.example.paragjai.firestore_recycler_view.Helper.RecyclerItemTouchHelper;
import com.example.paragjai.firestore_recycler_view.Helper.RecyclerItemTouchHelperListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOfStops extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    ArrayList<StopDetailsFromFirestore> stopDetailsList;
    MyRecyclerViewAdapterForStops myRecyclerViewAdapterForStops;
    LinearLayoutManager linearLayoutManager;
    RecyclerView mRecyclerView;
    CoordinatorLayout coordinatorLayout;

    Bundle bundle;
    String rootCollectionName = "routes";
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_stops);
        bundle = getIntent().getExtras();
        String selected_route_name = bundle.getString("selected_route_name");
        String[] route_name = selected_route_name.split("_");
        String selected_route_name_ = "";
        Log.d("selected_route_name: " ,selected_route_name);
        for(int i=0; i< route_name.length; ++i)
        {
            selected_route_name_ = selected_route_name_ + route_name[i] + " ";
        }

        selected_route_name_ = selected_route_name_.trim();
        Log.d("selected_route_name_ :" , selected_route_name_);
        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(selected_route_name_);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);



        stopDetailsList = new ArrayList<StopDetailsFromFirestore>();
        setUpRecyclerView();
        readFromFirestore(selected_route_name);
        }

    private void readFromFirestore(String selected_route_name)
    {
        final String partial_key1 = "stop_";
        final String partial_key2 = "morn_pickup_time_";
        Log.d("readFromFirestore: ", selected_route_name);
        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(rootCollectionName).document(selected_route_name).collection("stops").document(selected_route_name);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("onEvent Called", "Listen failed", e);
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String value1, value2;
                    String key1, key2;
                    Log.d("onEvent Called", "Current data: " + documentSnapshot.getData());
                    /* Reading key value pairs from the documentSnapshot */
                    int no_of_records = documentSnapshot.getData().size(); /*It will always be even*/
                    int no_of_stops = no_of_records/2; // Equal to number of morn_pickup_time

                    for(int i=0; i<no_of_stops; ++i)
                    {
                            key1 = partial_key1+Integer.toString(i+1);
                            key2 = partial_key2+Integer.toString(i+1);
                            value1 = documentSnapshot.getString(key1); //stopName
                            Log.d("reading key value pairs", key1+":"+value1);
                            value2 = documentSnapshot.getString(key2); //expectedPickUpTime
                            Log.d("reading key value pairs", key2+": "+value2);
                            StopDetailsFromFirestore stopDetailsFromFirestore = new StopDetailsFromFirestore(value1, value2);
                            stopDetailsList.add(stopDetailsFromFirestore);
                    }

                    /*As far as recycler view is concerned,
                                  once we get the data in a list, we pass it to the recycler adapter ctor
                                  and then set the adapter to the recycler view
                    */

                    myRecyclerViewAdapterForStops = new MyRecyclerViewAdapterForStops(ListOfStops.this, stopDetailsList);
                    mRecyclerView.setAdapter(myRecyclerViewAdapterForStops);


                } else {
                    Log.d("onEvent Called", "Current data: null");
                }
            }
        });
    }



    private void setUpRecyclerView()
    {
        /*To add a divider after every item in the list :
            https://www.youtube.com/watch?v=kSDLfxt_QZE
         */

        linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        mRecyclerView = findViewById(R.id.recycler_view_for_stops);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.addItemDecoration(dividerItemDecoration);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback
                = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, (RecyclerItemTouchHelperListener) this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position)
    {
        if(viewHolder instanceof MyRecyclerViewHolderForStops)
        {
            String stopName = stopDetailsList.get(viewHolder.getAdapterPosition()).getStopName();
            String expectedPickUpTime = stopDetailsList.get(viewHolder.getAdapterPosition()).getExpectedPickupTime();

            final StopDetailsFromFirestore deletedItem = stopDetailsList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            myRecyclerViewAdapterForStops.removeItem(deletedIndex);

            Snackbar snackbar = Snackbar.make(coordinatorLayout, stopName + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        myRecyclerViewAdapterForStops.restoreItem(deletedItem, deletedIndex);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }





}
