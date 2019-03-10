package com.example.paragjai.firestore_recycler_view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class StartButtonActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private ImageView imageView;
    private Bundle bundle;
    private String URL = "https://us-central1-firestore-recycler-view.cloudfunctions.net/DriverStartedJourney?";
    private String response;
    String current_date_and_time;
    String requiredURL;

    /* BEGIN : Location related fields */
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    GoogleApiClient.Builder builder;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    String selected_route_name;
    private Double myLatitude;
    private Double myLongitude;
    private Location myLastLocation;
    private static final int MY_PERMISSION_REQUEST_LOCATION_PERMISSION = 1;
    /* END : Location related fields */



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_button);
        imageView = (ImageView) findViewById(R.id.imageView);
        bundle = getIntent().getExtras();
        selected_route_name = bundle.getString("selected_route_name");

        /* BEGIN : Location related code */
        builder = new GoogleApiClient.Builder(StartButtonActivity.this);
        builder = builder.addApi(LocationServices.API);
        builder = builder.addConnectionCallbacks(StartButtonActivity.this);
        builder = builder.addOnConnectionFailedListener(StartButtonActivity.this);

        googleApiClient = builder.build();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        /* END : Location related code */

        imageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                // Pass this selected_route_name to next activity and load all the stops from firestore in recycler view in next activity.
                publish();
                Intent intent = new Intent(StartButtonActivity.this, ListOfStops.class);
                intent.putExtra("selected_route_name", selected_route_name);
                startActivity(intent);
            }
        });
    }

    /* BEGIN : Location related functions over-ridden as part of implementing the interfaces */

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.d("StartButtonActivity", "onConnected called : calling getLocation");
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("onConnectionFailed", "cause : " + connectionResult);
    }

    /* END : Location related functions over-ridden as part of implementing the interfaces */

    /*  BEGIN : Android activity lifecycle related functions useful for location */
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("StartButtonActivity", "onStart called");
        googleApiClient.connect(); /*onConnected gets called when the client actually gets connected*/
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("StartButtonActivity","onResume");
        if(googleApiClient.isConnected())
        {
            Log.d("StartButtonActivity", "yes connected");
        }
        else
        {
            Log.d("StartButtonActivity", "Google client not yet connected");
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) StartButtonActivity.this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        googleApiClient.disconnect();
    }
    /*  END : Android activity lifecycle related functions useful for location */

    public void publish()
    {
        /*It will be published to the topic named:
                    "/topics/"+selected_route_name

                 where selected_route_name variable will have the value selected by the driver.
        */
            if(myLastLocation!=null)
            {
                requiredURL = URL + "routeName=" + selected_route_name + "&&startTime=" + current_date_and_time + "&&latitude=" + myLatitude + "&&longitude=" + myLongitude;
            }
            else
            {
                requiredURL = URL + "routeName=" + selected_route_name + "&&startTime=" + current_date_and_time + "&&latitude=" + 0.0 + "&&longitude=" + 0.0;
            }
            //Instantiate new instance of our class
            HttpGetRequest getRequest = new HttpGetRequest();


            //Perform the doInBackground method, passing in our  URL
            try {
                response = getRequest.execute(requiredURL).get();

            } catch (Exception e)
            {
                e.printStackTrace();
            }

    }

    @Override
    public void onLocationChanged(Location location)
    {

    }

    public void getLocation()
    {
        Log.d("StartButtonActivity", "getLocation");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (myLastLocation != null) {
                myLatitude = myLastLocation.getLatitude();
                myLongitude = myLastLocation.getLongitude();
                Toast.makeText(this, "lat: " + myLatitude + ",long: " + myLongitude, Toast.LENGTH_SHORT).show();
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

        Log.d("StartButtonActivity", "getCurrentTime");
        Log.d("StartButtonActivity", selected_route_name);

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
        current_date_and_time = parts[0] + " " + parts[1] + " " + parts[2] + " " + time_parts[0]+":"+time_parts[1]+" "+am_or_pm;
        Log.d("Current time and date: ", "" + current_date_and_time);
        /*1. End : Get current time. */


    }
}
