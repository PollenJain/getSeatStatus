package com.example.paragjai.firestore_recycler_view;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/* A complete post for calling a URL from Android */

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    String inputLine;
    String response;

    @Override
    protected String doInBackground(String... strings) {
        String requiredURL = strings[0];
        try{
            //Create a URL object holding our URL
            URL myURL = new URL(requiredURL);

            //Create a connection
            HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();

            //Set methods and timeout
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to the URL
            connection.connect();

            /* Getting the response */

            //Create a new InputStreamReader
             InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            response = stringBuilder.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            response = null;
        }
        return response;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
