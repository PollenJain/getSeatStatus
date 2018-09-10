package com.example.paragjai.firestore_recycler_view;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {

    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    ArrayList<RouteDetailsFromFirestore> routeDetailsList;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        routeDetailsList = new ArrayList<>();
        setUpRecyclerView();
        setUpFireBase();
        //addTestDataToFirebase();
        loadDataFromFirebase();
    }

    private void addTestDataToFirebase()
    {
        Map<String, String> dataMap = new HashMap<String, String>();
        Random random = new Random();
        final Integer for_name = random.nextInt(50);
        final Integer for_status = random.nextInt(50);
        dataMap.put("status", "try status : " + for_status);
        dataMap.put("name", "try name : " + for_name);

        db.collection("routes")
                .add(dataMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        Log.d("onSuccess called: ", "name: try name " + for_name + " status: try status " + for_status);
                        Toast.makeText(MainActivity.this, "Added Test Data", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadDataFromFirebase()
    {
        if(routeDetailsList.size()>0)
            routeDetailsList.clear();

        /*get ALL documents in the "routes" collection*/
        db.collection("routes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {

                        if(task.isSuccessful())
                        {
                            Log.d("onComplete called", "task is successful: " );
                            Integer i = 0;
                            QuerySnapshot querySnapshot = task.getResult();

                            if(querySnapshot.isEmpty())
                            {
                                Log.d("onComplete called", "no documents in collection");
                            }
                            else {
                                String routeNameFromDoc;
                                String mornStartTimeFromDoc;
                                Log.d("onComplete called", querySnapshot.size() + " documents present in collection");
                                for (DocumentSnapshot documentSnapshot : querySnapshot)
                                {
                                    i = i + 1;
                                    Log.d("onComplete : " + i, "i :" + i);
                                    Log.d("doc id: " + documentSnapshot.getId(), documentSnapshot.getData().toString());
                                    routeNameFromDoc = documentSnapshot.getString("route_name");
                                    mornStartTimeFromDoc = documentSnapshot.getString("morn_start_time");
                                    Log.d("route_name from doc: ", routeNameFromDoc);
                                    Log.d("mornStartTime from doc ", mornStartTimeFromDoc);
                                    /*RouteDetailsFromFirestore ctor takes name first and then status*/
                                    RouteDetailsFromFirestore routeDetailsFromFirestore = new RouteDetailsFromFirestore(routeNameFromDoc, mornStartTimeFromDoc);
                                    routeDetailsList.add(routeDetailsFromFirestore);
                                }

                                Log.d("size of userArrayList: ", "is: " + routeDetailsList.size());
                                /*As far as recycler view is concerned,
                                  once we get the data in a list, we pass it to the recycler adapter ctor
                                  and then set the adapter to the recycler view
                                 */
                                myRecyclerViewAdapter = new MyRecyclerViewAdapter(MainActivity.this, routeDetailsList);
                                mRecyclerView.setAdapter(myRecyclerViewAdapter);
                            }
                        }
                        else
                        {
                            Exception exception = task.getException();
                            Log.d("loadDataFromFirebase: ", "exception occured: " + exception);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Problem occured", Toast.LENGTH_SHORT).show();
                        Log.w("onFailure called : ", e.getMessage());
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
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setUpFireBase()
    {
        db = FirebaseFirestore.getInstance();
    }
//public  void uncheck(int i){
//        RadioButton rb = (RadioButton) linearLayoutManager.getChildAt(i).findViewById(R.id.rbRadioButton);
//        rb.setChecked(false);
//}

}
