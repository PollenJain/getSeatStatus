package com.example.paragjai.firestore_recycler_view;

public class StopDetailsFromFirestore {

    private String stop_name_;
    private String expected_pickup_time_;

    public StopDetailsFromFirestore()
    {
        
    }

    public StopDetailsFromFirestore(String stop_name, String expected_pickup_time)
    {
        this.stop_name_ = stop_name;
        this.expected_pickup_time_ = expected_pickup_time;
    }

    public String getStopName()
    {
        return stop_name_;
    }

    public void setStopName(String stop_name)
    {
        this.stop_name_ = stop_name;
    }

    public String getExpectedPickupTime()
    {
        return expected_pickup_time_;
    }

    public void setExpectedPickupTime(String expected_pickup_time)
    {
        this.expected_pickup_time_ = expected_pickup_time;
    }
}
