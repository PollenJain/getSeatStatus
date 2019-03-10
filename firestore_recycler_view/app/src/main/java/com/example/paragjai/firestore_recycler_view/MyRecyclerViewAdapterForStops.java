package com.example.paragjai.firestore_recycler_view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;


import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.auth.AuthUI.TAG;

//CardListAdapter
public class MyRecyclerViewAdapterForStops extends RecyclerView.Adapter<MyRecyclerViewHolderForStops> {

    Context context;
    String routeName_;
    ArrayList<StopDetailsFromFirestore> stopDetailsFromFirestoreArrayList_;
    static int selectedPosition;
    private String current_date_and_time;
    public static final String PREFS_NAME = "CountFile";
    public static Integer PREFS_COUNT_TILL_NOW = 0;
    public static String PREFS_COUNT_TILL_NOW_KEY = "PREFS_COUNT_TILL_NOW";
    private String URL = "https://us-central1-firestore-recycler-view.cloudfunctions.net/DriverCrossedStop?";
    String requiredURL;
    private String response;
    private static int stopNumber;
    private final String TAG = "MyRecyclerViewAdapterFo";

    public MyRecyclerViewAdapterForStops(Context context, String routeName, ArrayList<StopDetailsFromFirestore> stopDetailsFromFirestoreArrayList)
    {
        this.context = context;
        this.routeName_ = routeName;
        this.stopDetailsFromFirestoreArrayList_ = stopDetailsFromFirestoreArrayList;
        this.stopNumber = 0;
    }

    @NonNull
    @Override
    public MyRecyclerViewHolderForStops onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        // LayoutInflater layoutInflater = LayoutInflater.from(mainActivity_.getBaseContext());
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.card_list_item_stops, viewGroup, false);
        return new MyRecyclerViewHolderForStops(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRecyclerViewHolderForStops myRecyclerViewHolderForStops, final int i)
    {
        //Toast.makeText(context, "Position : " + i + ", Stop: " + stopDetailsFromFirestoreArrayList_.get(i).getStopName(), Toast.LENGTH_SHORT).show();
        myRecyclerViewHolderForStops.mStopName.setText(stopDetailsFromFirestoreArrayList_.get(i).getStopName());
        myRecyclerViewHolderForStops.mExpectedPickUpTime.setText(stopDetailsFromFirestoreArrayList_.get(i).getExpectedPickupTime());
        myRecyclerViewHolderForStops.mNumberKeyboard.setListener((ListOfStops) context);
        selectedPosition = i;
    }

    @Override
    public int getItemCount() {
        return stopDetailsFromFirestoreArrayList_.size();
    }

    public void removeItem(int position, String noOfSeatsOccupied)
    {
        Log.d(TAG, "remove item called at position " + position);
        String stopName = stopDetailsFromFirestoreArrayList_.get(position).getStopName();
        stopNumber = stopNumber + 1; /* static variable */
        send(stopName, noOfSeatsOccupied);
        getLocationOfStopWhichDriverCrossed(routeName_, stopName, stopNumber, Integer.parseInt(noOfSeatsOccupied));
        stopDetailsFromFirestoreArrayList_.remove(position);
        notifyItemRemoved(position);
        /*Intent intent = new Intent(context, MyNumberPicker.class);
        Bundle bundle = new Bundle();
        bundle.putString("stopName", stopName);
        bundle.putString("selected_route_name", routeName_);
        intent.putExtras(bundle);
        context.startActivity(intent);*/
    }

    public void getLocationOfStopWhichDriverCrossed(final String routeName,final String stopName, final Integer stopPosition, final int noOfSeatsOccupied) {

        final String functionTAG = "getLocationOfStopWhere";
        LocationListener currentLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    /*Once current location is received :
                     1. Add it to the firestore database.
                     */
                    Log.d(functionTAG, "onLocationChanged called");
                    Map<String, Double> latLngOfStopWhichDriverCrossed = new HashMap<>();
                    latLngOfStopWhichDriverCrossed.put("lat", location.getLatitude());
                    latLngOfStopWhichDriverCrossed.put("lng", location.getLongitude());
                    storeStopLatLngToFirestoreDB(routeName, stopName, stopPosition, latLngOfStopWhichDriverCrossed, noOfSeatsOccupied);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };



        LocationManager currentLocationLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocationLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, currentLocationListener, null);
    }

    public void storeStopLatLngToFirestoreDB(String routeName, String stopName, Integer stopPosition, Map<String, Double> latLngOfStopWhichDriverCrossed, int noOfSeatsOccupied)
    {
        /* In case routeName is space separated, make it underscore separated */
        /* In case stopName is separated by multiple spaces or whitelines, make it separated by a single
        space
         */
        final String functionTAG = "storeStopLatLngToFires";
        Log.d(functionTAG, "called");
        Map<String, Object> data = new HashMap<>();
        String key1 = "stop_"+Integer.toString(stopPosition);
        String key2 = "on_location_pick_up_time_"+Integer.toString(stopPosition);
        String key3 = "stop_" + Integer.toString(stopPosition)+"_lat_lng";
        String key4 = "stop_"+Integer.toString(stopPosition)+"_no_of_seats_occupied";
        String underscoreSeparatedRouteName = convertStringFromSpaceSeparatedWordsToUnderscoreSeparated(routeName);
        String spaceSeparatedStopName = convertStringFromWhitelineSeparatedWordsToSingleSpaceSeparatedWords(stopName);
        String dd_mm_yyyy = getDateInDDMMYYYYFormat();
        String time_in_am_pm_when_driver_crossed = getTimeInAMPMFormat();
        data.put(key1, spaceSeparatedStopName);
        data.put(key2, time_in_am_pm_when_driver_crossed); /* get value from getCurrentTime function in the appropriate format */
        data.put(key3, latLngOfStopWhichDriverCrossed);
        data.put(key4, noOfSeatsOccupied);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("driver_routes")
                .document(underscoreSeparatedRouteName)
                .collection(dd_mm_yyyy)
                .document(underscoreSeparatedRouteName)
                .set(data, SetOptions.merge()  )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document snapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing document " + e);
                    }
                });

    }

    public String getTimeInAMPMFormat()
    {
        Date currentTime = Calendar.getInstance().getTime();
        String dateAndtime = currentTime.toString();
        String []parts = dateAndtime.split(" ");
        String []time_parts = parts[3].split(":");
        String am_or_pm = "am";
        Integer hour = Integer.parseInt(time_parts[0]);
        if(hour > 12)
        {
            am_or_pm = "pm";
            time_parts[0] = Integer.toString(hour%12);
        }

        String timeWhenDriverCrossed = time_parts[0]+":"+time_parts[1]+" "+am_or_pm;
        return timeWhenDriverCrossed;
    }

    private String convertStringFromSpaceSeparatedWordsToUnderscoreSeparated(String anySpaceSeparatedString)
    {
        String[] routeNameWords = anySpaceSeparatedString.split("\\s+"); /* tabs and spaces and whitespaces */
        Log.d(TAG, "no of words in passed string: " + routeNameWords.length);
        if(routeNameWords.length>0) {
            String alteredRouteName = routeNameWords[0];
            int j = 0;
            for (j = 1; j < routeNameWords.length; ++j)
            {
                Log.d(TAG, "j: " + j + ", routeNameWords[j]: " + routeNameWords[j]);
                alteredRouteName += "_" + routeNameWords[j];
            }
            Log.d(TAG, "returning string: " + alteredRouteName);
            return alteredRouteName;
        }
        else
        {
            Log.d(TAG, "routeNameWords has length less than zero.");
            return anySpaceSeparatedString;
        }

    }



    private String convertStringFromWhitelineSeparatedWordsToSingleSpaceSeparatedWords(String anyWhitelineSeparatedString)
    {
        String[] routeNameWords = anyWhitelineSeparatedString.split("\\s+");
        if(routeNameWords.length>0) {
            String alteredRouteName = routeNameWords[0];
            int j = 0;
            for (j = 1; j < routeNameWords.length; ++j)
            {
                alteredRouteName += " " + routeNameWords[j];
            }
            return alteredRouteName;
        }else
        {
            Log.d(TAG, "routeNameWords has length less than zero.");
            return anyWhitelineSeparatedString;
        }
    }




    private void send(String stopName, String noOfSeatsOccupied)
    {

        String stopNameWithSingleSpace = convertStringFromWhitelineSeparatedWordsToSingleSpaceSeparatedWords(stopName);
        String stopNameWithUnderscore = convertStringFromSpaceSeparatedWordsToUnderscoreSeparated(stopNameWithSingleSpace);
        getCurrentTime();
        publish(stopNameWithUnderscore, noOfSeatsOccupied);

    }

    private String getDateInDDMMYYYYFormat()
    {
        String functionTAG = "getDateInDDMMYYYYFormat";
        String pattern = "dd_MM_yyyy";
        String dateInString = new SimpleDateFormat(pattern).format(new Date());
        Log.d(functionTAG, "dd_mm_yyyy: " + dateInString);
        return dateInString;

    }

    public void getCurrentTime()
    {

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


    public void publish(String stopNameWithUnderscore, String noOfSeatsOccupied)
    {
        /*It will be published to the topic named:
                    "/topics/"+selected_route_name+"/"+selected_stop_name

                 where selected_route_name variable will have the value selected by the driver.
        */
        Log.d(TAG, "PREFS_COUNT_TILL_NOW(1): "+ PREFS_COUNT_TILL_NOW);
        PREFS_COUNT_TILL_NOW += Integer.parseInt(noOfSeatsOccupied);
        Log.d(TAG, "PREFS_COUNT_TILL_NOW(2): "+ PREFS_COUNT_TILL_NOW);
        ((ListOfStops) context).getSharedPreferences(PREFS_NAME,MODE_PRIVATE).edit()
                .putInt(PREFS_COUNT_TILL_NOW_KEY,PREFS_COUNT_TILL_NOW)
                .apply();
        String numberOfSeatsOccupied = noOfSeatsOccupied;
        SharedPreferences pref = ((ListOfStops) context).getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        Integer countsTillNow = pref.getInt(PREFS_COUNT_TILL_NOW_KEY, 0);
        String countsTillNowString = Integer.toString(countsTillNow);
        Log.d(TAG, "no of seats occupied: " + numberOfSeatsOccupied + " at " + stopNameWithUnderscore + " in route " + routeName_+ ". Total number of seats occupied till now: " + countsTillNowString);
        if(stopNameWithUnderscore!=null) {
            requiredURL = URL + "routeName=" + routeName_ + "&&crossedStopName=" + stopNameWithUnderscore + "&&crossedStopAtDateAndTime=" + current_date_and_time + "&&noOfSeatsOccupied=" + noOfSeatsOccupied + "&&totalNoOfSeatsOccupiedTillNow=" + countsTillNowString;
        }
        else
        {
            requiredURL = URL + "routeName=" + routeName_ + "&&crossedStopName=" + "" + "&&crossedStopAtDateAndTime=" + current_date_and_time + "&&noOfSeatsOccupied=" + noOfSeatsOccupied + "&&totalNoOfSeatsOccupiedTillNow=" + countsTillNowString;
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


    public void restoreItem(StopDetailsFromFirestore stopDetailsFromFirestore, int position)
    {
        stopDetailsFromFirestoreArrayList_.add(position, stopDetailsFromFirestore);
        notifyItemInserted(position);
    }

}

