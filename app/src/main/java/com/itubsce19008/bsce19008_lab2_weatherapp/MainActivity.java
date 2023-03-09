package com.itubsce19008.bsce19008_lab2_weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    TextView cityName,cityViewName,gettextViewWindSpeed, gettextViewTemperature,gettextViewVisibility,gettextViewHumidity, gettextViewPressure,gettextViewMinTemperature,gettextViewMaxTemperature;
    Button cityBtn;
    double latitude,longitude;
    ImageButton userLocationBtn;
    double temperature,minTemperature,maxTemperature,pressure,humidity,speed;
    String placeName;
    int visibility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText) findViewById(R.id.editTextCityName);
        cityViewName = (TextView) findViewById(R.id.textViewCity);
        cityBtn = (Button) findViewById(R.id.btnCity);
        userLocationBtn = findViewById(R.id.userLocationBtn);
        gettextViewTemperature = (TextView) findViewById(R.id.textViewTemperature);
        gettextViewPressure = (TextView) findViewById(R.id.textViewPressure);
        gettextViewMinTemperature = (TextView) findViewById(R.id. textViewMinTemperature);
        gettextViewMaxTemperature = (TextView) findViewById(R.id. textViewMaxTemperature);
        gettextViewHumidity = (TextView) findViewById(R.id. textViewHumidity);
        gettextViewVisibility = (TextView) findViewById(R.id. textViewVisibility);
        gettextViewWindSpeed = (TextView) findViewById(R.id. textViewWindSpeed);


        int permissionCode = 1;
        int fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[] { Manifest.permission.ACCESS_FINE_LOCATION };
            ActivityCompat.requestPermissions(this, permissions, permissionCode);
        }
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER or GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Get the latitude and longitude from the location object
                 latitude = location.getLatitude();
                 longitude = location.getLongitude();

                // Use the latitude and longitude values as needed
                Log.d("***************************MyLocation", "******************Latitude: " + latitude + ", Longitude: " + longitude);
            }
        });
        Log.d("***************************MyLocation", "******************Latitude: " + latitude + ", Longitude: " + longitude);
        userLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString =  "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=9bf9823ba78966c7e23ca45773ea73b9";
                Runnable runnable2 = new Runnable() {

                    @Override
                    public void run() {
                        char[] data= new char[5000];
                        try {
                            URL u = new URL(urlString);
                            InputStream i = u.openStream();
                            BufferedReader b = new BufferedReader(new InputStreamReader(i));
                            int count = b.read(data);
                            String response = new String(data,0,count);
                            Log.d("+++++++++++++++++++", "USER LOCATION on: +++++++++++++"+response);
                            DecimalFormat df = new DecimalFormat("#.##");
                            JSONObject obj = new JSONObject(response);
                            JSONObject main = obj.getJSONObject("main");
                            JSONObject wind = obj.getJSONObject("wind");
                            JSONObject sys = obj.getJSONObject("sys");
                             temperature = main.getDouble("temp");
                            temperature = Double.parseDouble(df.format(temperature -275.15));
                             minTemperature = main.getDouble("temp_min");
                            minTemperature = Double.parseDouble(df.format(minTemperature -275.15));
                             maxTemperature = main.getDouble("temp_max");
                            maxTemperature = Double.parseDouble(df.format(maxTemperature -275.15));
                             pressure = Double.parseDouble(df.format(main.getDouble("pressure")));
                             humidity = Double.parseDouble(df.format(main.getDouble("humidity")));
                             visibility = obj.getInt("visibility");
                             speed = wind.getInt("speed");
                             placeName = obj.getString("name");
                            cityViewName.setText(placeName);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gettextViewTemperature.setText("Temperature: "+temperature+"°C");
                                    gettextViewPressure.setText("Pressure: "+pressure);
                                    gettextViewMinTemperature.setText("Min Temperature: "+minTemperature+"°C");
                                    gettextViewMaxTemperature.setText("Max Temperature: "+maxTemperature+"°C");
                                    gettextViewHumidity.setText("Humidity: "+humidity);
                                    gettextViewVisibility.setText("Vsibility: "+visibility);
                                    gettextViewWindSpeed.setText("Wind Speed: "+speed);
                                    cityViewName.setText(placeName);
                                }
                            });
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                Thread myThread = new Thread(runnable2);
                myThread.start();

            }
        });


        cityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputCityName = cityName.getText().toString();
//                https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=-94.04&appid= USE YOUR API KEY
//                String urlString =  "https://api.openweathermap.org/data/2.5/weather?lat="+userLat+"&lon="+userLon+"&appid= USE YOUR API KEY"
                String urlString = "https://api.openweathermap.org/data/2.5/weather?q="+inputCityName+"&appid=9bf9823ba78966c7e23ca45773ea73b9";
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        char[] data= new char[5000];
                        try {
                            URL u = new URL(urlString);
                            InputStream i = u.openStream();
                            BufferedReader b = new BufferedReader(new InputStreamReader(i));
                            int count = b.read(data);
                            String response = new String(data,0,count);

                                Log.d("+++++++++++++++++++", "run: +++++++++++++" + response);
                                DecimalFormat df = new DecimalFormat("#.##");
                                JSONObject obj = new JSONObject(response);
                                JSONObject main = obj.getJSONObject("main");
                                JSONObject wind = obj.getJSONObject("wind");
                                temperature = main.getDouble("temp");
                                temperature = Double.parseDouble(df.format(temperature - 275.15));
                                minTemperature = main.getDouble("temp_min");
                                minTemperature = Double.parseDouble(df.format(minTemperature - 275.15));
                                maxTemperature = main.getDouble("temp_max");
                                maxTemperature = Double.parseDouble(df.format(maxTemperature - 275.15));
                                pressure = Double.parseDouble(df.format(main.getDouble("pressure")));
                                humidity = Double.parseDouble(df.format(main.getDouble("humidity")));
                                visibility = obj.getInt("visibility");
                                speed = wind.getInt("speed");
                                placeName = obj.getString("name");


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        gettextViewTemperature.setText("Temperature: " + temperature + "°C");
                                        gettextViewPressure.setText("Pressure: " + pressure);
                                        gettextViewMinTemperature.setText("Min Temperature: " + minTemperature + "°C");
                                        gettextViewMaxTemperature.setText("Max Temperature: " + maxTemperature + "°C");
                                        gettextViewHumidity.setText("Humidity: " + humidity);
                                        gettextViewVisibility.setText("Vsibility: " + visibility);
                                        gettextViewWindSpeed.setText("Wind Speed: " + speed);
                                        cityViewName.setText(placeName);
                                    }
                                });

                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                if (inputCityName.isEmpty()) { // Check if the input is empty
                    // Show a dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Empty Input");
                    builder.setMessage("Please enter a valid place in the input field.");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Thread myThread = new Thread(runnable);
                    myThread.start();
                }
            }
        });

    }
}