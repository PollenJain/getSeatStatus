package com.example.paragjai.firestore_recycler_view;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.Date;

public class StartButtonActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bundle bundle;
    private String URL = "https://us-central1-firestore-recycler-view.cloudfunctions.net/DriverStartedJourney?";
    private String response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_button);
        imageView = (ImageView) findViewById(R.id.imageView);
        bundle = getIntent().getExtras();

        imageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                String selected_route_name = bundle.getString("selected_route_name");
                Log.d("StartButtonActivity", selected_route_name);

                /* 1. Get current time.
                      Send the selected_route_name and time to all those subscribed to this route.
                      <Optional for now: I will also be great if we can send the current location of the driver as well as part of URL>
                   2. Pass this selected_route_name to next activity and load all the stops from firestore in recycler view in next activity.
                 */

                /*1. Start : Get current time. */
                Date currentTime = Calendar.getInstance().getTime();
                String current_date_and_time = currentTime.toString();
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

                /*It will be published to the topic named:
                    "/topics/"+selected_route_name

                 where selected_route_name variable will have the value selected by the driver.
                 */
                String requiredURL = URL+"stopName="+selected_route_name+"&&startTime="+current_date_and_time;

                //Instantiate new instance of our class
                HttpGetRequest getRequest = new HttpGetRequest();

                //Perform the doInBackground method, passing in our  URL
                try {
                    response = getRequest.execute(requiredURL).get();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                /*1. End : Get current time. */

                /*2. Begin : Pass the selected route to next activity. */
                Intent intent = new Intent(StartButtonActivity.this, ListOfStops.class);
                intent.putExtra("selected_route_name", selected_route_name);
                startActivity(intent);
                /*2. End : Pass the selected route to next activity */



            }
        });
    }


}
