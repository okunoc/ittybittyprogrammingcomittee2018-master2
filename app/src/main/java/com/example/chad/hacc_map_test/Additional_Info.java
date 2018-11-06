package com.example.chad.hacc_map_test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/* Cynthia's Imports */
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

public class Additional_Info extends AppCompatActivity implements LocationListener{

    TextView windField, currentTemperatureField, locationText;
    String zipcode;
    String OPEN_WEATHER_MAP_API = "3d58a04d89afa4a0dab92c4e6490991c";
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional__info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationText = (TextView)findViewById(R.id.locationText);

        /* Permission for fine access location*/


        //Get and store gps location
        getLocation();


        Button nextButton = findViewById(R.id.Next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //taskLoadUp actually sends the zipcode query to the OpenWeatherMap API
                taskLoadUp(zipcode);

            }
        });

    }




    /* Begin Location getlocation and overrides shenanigans */

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);

        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Test prints yea!

        //locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        //latitude = location.getLatitude();
        //longitude = location.getLongitude();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            zipcode = addresses.get(0).getPostalCode();


            // Gets the address for later.
             locationText.setText(addresses.get(0).getAddressLine(0));
            //locationText.setText(locationText.getText()+ "\n Zipcode:" + zipcode);
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(Additional_Info.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }


    /* Begin WeatherAPI loading shenanigans */

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }



    class DownloadWeather extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String...args) {

            //Does query using zipcode
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?zip=" + args[0] +
                    ",us&units=imperial&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    //Grab xml and depending on which fields you want, pull from either
                    //The "main" group of fields. For us we also need "wind" group
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    JSONObject wind = json.getJSONObject("wind");

                    currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "°");
                    windField.setText("Windspeed: " + wind.getString("speed") + " mph");

                }
            } catch (JSONException e) {
                // This error will never happen since we don't allow user input anymore
                // Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
            }

        }

    }


}
