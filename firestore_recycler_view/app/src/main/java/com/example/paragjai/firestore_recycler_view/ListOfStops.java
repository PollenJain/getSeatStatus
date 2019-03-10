package com.example.paragjai.firestore_recycler_view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.paragjai.firestore_recycler_view.Helper.RecyclerItemTouchHelper;
import com.example.paragjai.firestore_recycler_view.Helper.RecyclerItemTouchHelperListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.paragjai.firestore_recycler_view.MyRecyclerViewAdapterForStops.selectedPosition;

public class ListOfStops extends AppCompatActivity implements RecyclerItemTouchHelperListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, NumberKeyboardListener {

    ArrayList<StopDetailsFromFirestore> stopDetailsList;
    MyRecyclerViewAdapterForStops myRecyclerViewAdapterForStops;
    LinearLayoutManager linearLayoutManager;
    RecyclerView mRecyclerView;
    CoordinatorLayout coordinatorLayout;
    String stopName;

    Bundle bundle;
    String rootCollectionName = "routes";
    FirebaseFirestore db;

    final String stopURL = "https://us-central1-firestore-recycler-view.cloudfunctions.net/DriverCrossedStop?";
    String requiredURL;
    private String response;
    String selected_route_name;
    String current_date_and_time;
    String crossedStopAtTime;
    Double stopLatitude;
    Double stopLongitude;
    private Location myLastLocation;
    private static final int MY_PERMISSION_REQUEST_LOCATION_PERMISSION = 1;

    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    GoogleApiClient.Builder builder;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_stops);
        bundle = getIntent().getExtras();
        selected_route_name = bundle.getString("selected_route_name");
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


        /* BEGIN : Location related code */
        builder = new GoogleApiClient.Builder(ListOfStops.this);
        builder = builder.addApi(LocationServices.API);
        builder = builder.addConnectionCallbacks(ListOfStops.this);
        builder = builder.addOnConnectionFailedListener(ListOfStops.this);

        googleApiClient = builder.build();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        /* END : Location related code */

        stopDetailsList = new ArrayList<StopDetailsFromFirestore>();
        setUpRecyclerView();
        readFromFirestore(selected_route_name);
    }



    private void readFromFirestore(String selected_route_name)
    {
        final String partial_key1 = "stop_";
        final String partial_key2 = "morn_pickup_time_";
        final String partial_key3_1 = "stop_";
        final String partial_key3_2 = "_lat_lng";
        final String selectedRouteName = selected_route_name;

        Log.d("readFromFirestore: ", selected_route_name);
        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(rootCollectionName).document(selected_route_name).collection("stops").document(selected_route_name);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null)
                {
                    Log.w("onEvent Called", "Listen failed", e);
                }

                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    String value1, value2;
                    String key1, key2, key3;
                    Map<String, Double> value3_lat_lng;
                    Log.d("onEvent Called", "Current data: " + documentSnapshot.getData());
                    /* Reading key value pairs from the documentSnapshot */
                    int no_of_records = documentSnapshot.getData().size();
                    int no_of_stops = no_of_records/3; // Equal to number of morn_pickup_time

                    for(int i=0; i<no_of_stops; ++i)
                    {
                            key1 = partial_key1+Integer.toString(i+1);
                            key2 = partial_key2+Integer.toString(i+1);
                            key3 = partial_key3_1 + Integer.toString(i+1) + partial_key3_2;
                            value1 = documentSnapshot.getString(key1); //stopName
                            Log.d("reading key value pairs", key1+":"+value1);
                            value2 = documentSnapshot.getString(key2); //expectedPickUpTime
                            Log.d("reading key value pairs", key2+": "+value2);
                            value3_lat_lng = (Map<String,Double>)documentSnapshot.getData().get(key3);
                            Log.d("reading key value pairs", key3+": "+value3_lat_lng.get("lat"));
                            Log.d("reading key value pairs", key3+": "+value3_lat_lng.get("lng"));
                            StopDetailsFromFirestore stopDetailsFromFirestore = new StopDetailsFromFirestore(value1, value2);
                            stopDetailsList.add(stopDetailsFromFirestore);
                    }

                    /*As far as recycler view is concerned,
                                  once we get the data in a list, we pass it to the recycler adapter ctor
                                  and then set the adapter to the recycler view
                    */

                    myRecyclerViewAdapterForStops = new MyRecyclerViewAdapterForStops(ListOfStops.this, selectedRouteName, stopDetailsList);
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

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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
            stopName = stopDetailsList.get(viewHolder.getAdapterPosition()).getStopName();
            String expectedPickUpTime = stopDetailsList.get(viewHolder.getAdapterPosition()).getExpectedPickupTime();

            final StopDetailsFromFirestore deletedItem = stopDetailsList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            stopName = deletedItem.getStopName();
            myRecyclerViewAdapterForStops.removeItem(deletedIndex, stopName);
            getLocation();

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


    public void getLocation()
    {
        Log.d("ListOfStops", "getLocation");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (myLastLocation != null)
            {
                stopLatitude = myLastLocation.getLatitude();
                stopLongitude = myLastLocation.getLongitude();
                //Toast.makeText(this, "stopLat: " + stopLatitude + ",stopLong: " + stopLongitude, Toast.LENGTH_SHORT).show();
                getCurrentTime();
            }
        }
        else
        {
            final String perm[] = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                /*since your app doesnot have the permission you prompt the user to provide the permission
                by calling requestPermissions
                 */
            ActivityCompat.requestPermissions(this, perm, MY_PERMISSION_REQUEST_LOCATION_PERMISSION);
        }
    }

    public void getCurrentTime()
    {

        Log.d("ListOfStops", "getCurrentTime");
        Log.d("ListOfStops", selected_route_name);

                /* 1. Get current time.
                      Send the selected_route_name and time and current location of the driver to all those subscribed to this route.
                 */

        /*1. Start : Get current time. */
        Date currentTime = Calendar.getInstance().getTime();
        current_date_and_time = currentTime.toString();
        String []parts = current_date_and_time.split(" ");
        String []time_parts = parts[3].split(":");
        String am_or_pm = "am";
        Integer hour = Integer.parseInt(time_parts[0]);
        if(hour > 12)
        {
            am_or_pm = "pm";
            time_parts[0] = Integer.toString(hour%12);
        }
        crossedStopAtTime = time_parts[0]+":"+time_parts[1]+" "+am_or_pm;
        Log.d("crossedStopAtTime: ", "" + crossedStopAtTime);
        /*1. End : Get current time. */
        publish();

    }

    public void publish()
    {
        /*It will be published to the topic named:
                    "/topics/"+selected_route_name
                 where selected_route_name variable will have the value selected by the driver.
        */

        if(myLastLocation!=null)
        {
            requiredURL = stopURL + "routeName=" + selected_route_name + "&&crossedStop=" + stopName + "&&crossedStopAtTime=" + crossedStopAtTime + "&&stopLatitude=" + stopLatitude + "&&stopLongitude=" + stopLongitude;
        }
        else
        {
            requiredURL = stopURL + "routeName=" + selected_route_name + "&&crossedStop=" + stopName + "&&crossedStopAtTime=" + crossedStopAtTime + "&&stopLatitude=" + 0.0 + "&&stopLongitude=" + 0.0;
        }
        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();

        //Perform the doInBackground method, passing in our  URL
        try
        {
            response = getRequest.execute(requiredURL).get();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("ListOfStops", "onConnected called : calling getLocation");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    /*  BEGIN : Android activity lifecycle related functions useful for location */
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("ListOfStops", "onStart called");
        googleApiClient.connect(); /*onConnected gets called when the client actually gets connected*/
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("ListOfStops","onResume");
        if(googleApiClient.isConnected())
        {
            Log.d("ListOfStops", "yes connected");
        }
        else
        {
            Log.d("ListOfStops", "Google client not yet connected");
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) ListOfStops.this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        switch(id)
        {
            case R.id.home:
                /*Toast.makeText(this, "home clicked", Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(this, MainActivityWithRoutesList.class);
                startActivity(intent);
                break;
            default:
                /*Toast.makeText(this, "nothing clicked", Toast.LENGTH_SHORT).show();*/
                Intent intent1 = new Intent(this, StartButtonActivityWithContinousLocationPublishing.class);
                intent1.putExtra("selected_route_name", selected_route_name);
                startActivity(intent1);
                break;
        }
        return true;
    }

    @Override
    public void onLeftAuxButtonClicked() {
        //Toast.makeText(this, "onLeftAux", Toast.LENGTH_SHORT).show();
        TextView digitTV = (TextView) findViewById(R.id.digit);
        String numberOfSeats = digitTV.getText().toString();
        myRecyclerViewAdapterForStops.removeItem(selectedPosition, numberOfSeats);
    }

    @Override
    public void onNumberClicked(int digit) {
        TextView digitTV = (TextView) findViewById(R.id.digit);
        String number = digitTV.getText().toString();
        if(number.length()>0) {
            String firstDigit = number.substring(0, 1);
            if (firstDigit.equals(Integer.toString(0))) {
                digitTV.setText(Integer.toString(digit));
            } else {
                digitTV.setText(digitTV.getText().toString() + Integer.toString(digit));
            }
        }
        else
        {
            digitTV.setText(Integer.toString(digit));
        }
    }

    @Override
    public void onRightAuxButtonClicked() {
        //Toast.makeText(this, "onRightAuxButtonClicked", Toast.LENGTH_SHORT).show();
        TextView digitTV = (TextView) findViewById(R.id.digit);
        String number = digitTV.getText().toString();
        if(number!=null && number.length() == 0)
        {
            //DO NOTHING
        }
        else
        {
            digitTV.setText(number.substring(0, number.length()-1));
        }

    }
    /*  END : Android activity lifecycle related functions useful for location */
}
