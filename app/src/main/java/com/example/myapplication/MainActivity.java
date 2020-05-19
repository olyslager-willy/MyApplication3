package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements NewSessionFragment.RefreshMethod{



    String response;
    String[] operationArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        //call the asynctask as soon as the mainActivity is created so that we have the values
        //that have to be passed to the newSession fragment when it is created
        //Make object of ReadInfoFromDatabase to populate operation fields
        //when user opens new activity page
        ReadInfoFromDatabase readInfoFromDatabase = new ReadInfoFromDatabase();
        readInfoFromDatabase.execute();

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_new:
                            selectedFragment = new NewSessionFragment();
                            Bundle args = new Bundle();
                            args.putStringArray("operations",operationArray);
                            selectedFragment.setArguments(args);
                            break;
                        case R.id.nav_reporting:
                            selectedFragment = new ReportingFragment();
                            break;
                        case R.id.nav_configuration:
                            selectedFragment = new ConfigurationFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public void refreshRequest() {
        //this code will execute when the refreshrequest is sent from the newSessionFragment
        //we have to call the AsyncTask
        //call asynctask to refreshDatabase
        ReadInfoFromDatabase readInfoFromDatabase = new ReadInfoFromDatabase();
        readInfoFromDatabase.execute();

        //pass the refreshed information to the fragment via Bundle with the refreshed info
        NewSessionFragment newSessionFragment = new NewSessionFragment();
        Bundle args = new Bundle();
        args.putStringArray("operations",operationArray);
        newSessionFragment.setArguments(args);

        //open the new session fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSessionFragment).commit();

    }

    //Implement Asynctask to run database operations--------------------------------------------------------------------------------------------
    public  class ReadInfoFromDatabase extends AsyncTask<Void,Void,Void> {

        String read_info_url;
        //implement methods for AsyncTask
        @Override
        protected void onPreExecute() {
            //specify the domain name and php script
            //still need to make php file
            read_info_url ="http://192.168.1.67:80/read_info.php";
        }

        @Override
        protected Void doInBackground(Void... args) {

            try {
                URL url = new URL(read_info_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //set parameters for url connection
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //create input stream object to receive server response
                InputStream inputStream =httpURLConnection.getInputStream();
                //we implement a bufferedReader to read the text from the
                //input stream of characters
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                response="";
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    response+=line;
                }

                Log.i("Server response:", response);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            //get the last 2 digits of the response to get the number of operations in the database
            //so that we can initialize the operations array to the proper size
            //I NEED TO ADD A FAILSAFE TO THIS.. WHAT HAPPENS IF THE # OR OPERATIONS IS >9?
            //WE WILL HAVE TO READ 2 DIGITS INSTEAD OF 1.. CHANGE IN PHP CODE
            if(response!=null) {
                String lastDigit = response.substring(response.length() - 1);
                int numOfOperations = Integer.valueOf(lastDigit);
                operationArray = new String[numOfOperations];
                operationArray = response.split("/");
                Log.i("Main Activity", "Response is not null");
            }else{
                Log.i("Main Activity", "Response is null");
            }
        }
    }
}