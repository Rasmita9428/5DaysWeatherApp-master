package com.example.dell.a5daysweatherapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    GPSTracker gpsTracker;
    TextView txtCity, updated_time, txttemp, txtsunrise, txtsunset, txtwind, txtpressure, txthumidity, txtabout;
    String str_currentlatitude, str_currentlongitude;
    String API = "02e24deaa9fa3286feaeead84040b350";
    String City_ID;
    RelativeLayout rl_mainContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        checkPermission();
        new weatherTask().execute();

    }
    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat="+str_currentlatitude+"&lon="+str_currentlongitude + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                Log.e("daydata", String.valueOf(jsonObj));
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                City_ID=weather.getString("id");
                Log.e("City_id",City_ID);
                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
//                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
//                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");


                /* Populating extracted data into our views */
                txtCity.setText(address);
                updated_time.setText(updatedAtText);
                txttemp.setText(temp);

                txtsunrise.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                txtsunset.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));


                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.rl_mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }

    public void initialize() {
        txtCity = findViewById(R.id.txtCity);
        updated_time = findViewById(R.id.updated_time);
        txttemp = findViewById(R.id.txttemp);
        txtsunrise = findViewById(R.id.txtsunrise);
        txtsunset = findViewById(R.id.txtsunset);
        txtwind = findViewById(R.id.txtwind);
        txtpressure = findViewById(R.id.txtpressure);
        txthumidity = findViewById(R.id.txthumidity);
        txtabout = findViewById(R.id.txtabout);
        rl_mainContainer=findViewById(R.id.rl_mainContainer);
    }
    public void checkPermission() {
        gpsTracker = new GPSTracker(MainActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
            if (Comman_Method.isPermissionNotGranted(MainActivity.this, permissions)) {
                requestPermissions();
                return;
            } else {
                str_currentlatitude = String.valueOf(gpsTracker.getLatitude());
                str_currentlongitude = String.valueOf(gpsTracker.getLongitude());
                Toast.makeText(MainActivity.this, str_currentlatitude + str_currentlongitude, Toast.LENGTH_SHORT).show();
            }
        } else
            str_currentlatitude = String.valueOf(gpsTracker.getLatitude());
        str_currentlongitude = String.valueOf(gpsTracker.getLongitude());
        Log.e("Latlong", str_currentlatitude + str_currentlongitude);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CommonKeys.PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                CommonKeys.PERMISSION_CODE
        );
    }


    }

