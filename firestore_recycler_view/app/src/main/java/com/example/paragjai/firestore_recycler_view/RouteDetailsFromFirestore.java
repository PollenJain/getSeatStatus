package com.example.paragjai.firestore_recycler_view;

public class RouteDetailsFromFirestore {

    private String route_name_;
    private String morn_start_time_;

    public RouteDetailsFromFirestore()
    {

    }

    public RouteDetailsFromFirestore(String route_name, String morn_start_time)
    {
        this.route_name_ = route_name;
        this.morn_start_time_ = morn_start_time;
    }

    public String getRouteName()
    {
        return route_name_;
    }

    public void setRouteName(String route_name)
    {
        this.route_name_ = route_name;
    }

    public String getMornStartTime()
    {
        return morn_start_time_;
    }

    public void setMornStartTime(String morn_start_time)
    {
        this.morn_start_time_ = morn_start_time;
    }
}
