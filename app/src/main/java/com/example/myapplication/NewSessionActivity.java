package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeListener;

public class NewSessionActivity extends AppCompatActivity implements PreferredMethodsFragment.SummaryPageTrigger, NewSessionActivityFragment.SummaryPageTrigger2 {

    //member variables
    String operationName;
    String[] prodTaskArray;
    String[] goalTimesArray;
    String[] prefMethodsArray;
    String[] responseArray;
    String[] prodTaskCount;
    String[]sliderValuesArrray;
    float[] earnedTimeContribution;
    String totalTimeString;
    String delayTimeString;
    String delayNotesString;


    float totalEarnedTime;
    float totTimeMinusDelay;
    long goalTimeHolder;
    long prodTaskCountHolder;
    float performanceCalc;
    float totSecondsDelay;


    NewSessionActivityFragment newSessionActivityFragment;
    SwipeAdapter swipeAdapter = new SwipeAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        //setting listener for fragment


        //read the name of the operation that was passed from the previous activity
        //to tell the asynctask what to query in the database
        operationName = getIntent().getStringExtra("Operation Name");

        //start Asynctask to extract information from database
        NewSessionActivity.ReadInfoFromDatabase2 readInfoFromDatabase = new NewSessionActivity.ReadInfoFromDatabase2();
        readInfoFromDatabase.execute();

    }

    //create a public method to allow the fragment to access data from this activity
    public String[] dataAccess(){
        return prodTaskArray;
    }

    @Override
    protected void onStart() {
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
        super.onStart();
    }

    @Override
    public void onSummaryPageTriggered(String[]sliderValues, String delayNotes) {
        //this is the preferred methods interface method.. it will be called
        //when the "end session" button is clicked
        //initiate calculations to get the performance etc
        sliderValuesArrray=sliderValues;
        delayNotesString=delayNotes;
        totalTimeCalculator();

    }

    @Override
    public void onSummaryPageTriggered2(String timeString,String timeString2, String[] prodTasks ) {
        //this is the receiver for the interface in the NewSessionActivityFragment
        Log.i("From Activity", timeString+""+timeString2+""+prodTasks[0]);
        totalTimeString=timeString;
        delayTimeString=timeString2;
        prodTaskCount=prodTasks;

    }

    public void totalTimeCalculator(){

        String holderString = totalTimeString;
        String holderString2 = holderString.replace(":","");
        //getting the first digit
        Character firstNum=holderString2.charAt(0);
        String firstNumString = firstNum.toString();
        int firstNumInt=Integer.parseInt(firstNumString);
        //getting the second digit
        Character secondNum=holderString2.charAt(1);
        String secondNumString = secondNum.toString();
        int secondNumInt=Integer.parseInt(secondNumString);
        //getting the third digit
        Character thirdNum=holderString2.charAt(2);
        String thirdNumString = thirdNum.toString();
        //getting the fourth digit
        Character fourthNum=holderString2.charAt(3);
        String fourthNumString = fourthNum.toString();
        //concatenate last 2 digits
        String thirdAndFourthNumString= thirdNumString+fourthNumString;
        int thirdAndFourthNumint=Integer.parseInt(thirdAndFourthNumString);
        //manipulate digits to get total seconds
        int totSeconds= (firstNumInt*600)+(secondNumInt*60)+thirdAndFourthNumint;
        System.out.println("THIS IS THE TOTAL TIME IN THE SESSION CHRONOMETER :"+totSeconds+" SECONDS" );

        //Do the same for the delayChronometer
        String holderStringDelay = delayTimeString;
        String holderString2Delay = holderStringDelay.replace(":","");
        //getting the first digit
        Character firstNumDelay=holderString2Delay.charAt(0);
        String firstNumStringDelay = firstNumDelay.toString();
        int firstNumIntDelay=Integer.parseInt(firstNumStringDelay);
        //getting the second digit
        Character secondNumDelay=holderString2Delay.charAt(1);
        String secondNumStringDelay = secondNumDelay.toString();
        int secondNumIntDelay=Integer.parseInt(secondNumStringDelay);
        //getting the third digit
        Character thirdNumDelay=holderString2Delay.charAt(2);
        String thirdNumStringDelay = thirdNumDelay.toString();
        //getting the fourth digit
        Character fourthNumDelay=holderString2Delay.charAt(3);
        String fourthNumStringDelay = fourthNumDelay.toString();
        //concatenate last 2 digits
        String thirdAndFourthNumStringDelay= thirdNumStringDelay+fourthNumStringDelay;
        int thirdAndFourthNumintDelay=Integer.parseInt(thirdAndFourthNumStringDelay);
        //manipulate digits to get total seconds
        totSecondsDelay= (firstNumIntDelay*600)+(secondNumIntDelay*60)+thirdAndFourthNumintDelay;
        System.out.println("THIS IS THE TOTAL TIME IN THE SESSION CHRONOMETER :"+totSecondsDelay+" SECONDS" );

        totTimeMinusDelay=(totSeconds-totSecondsDelay);

        earnedTimeCalculator();

    }

    public void earnedTimeCalculator(){
        totalEarnedTime=0;
        earnedTimeContribution=new float[prodTaskArray.length-1];
        for(int i =0;i<prodTaskArray.length-1;i++){
            goalTimeHolder = Long.parseLong(goalTimesArray[i]);
            prodTaskCountHolder = Long.parseLong(prodTaskCount[i]);
            //store the result of the multiplication in an array so that we can pass
            //it to the next activity and understand how each production task contributes
            //to the overall earned time
            earnedTimeContribution[i]=(goalTimeHolder*prodTaskCountHolder);
            totalEarnedTime = totalEarnedTime + (goalTimeHolder*prodTaskCountHolder);
            Log.i("GT vs COUNT: ", goalTimeHolder+" vs "+prodTaskCountHolder );
            Log.i("Total Earned Time:", ""+totalEarnedTime);


        }
        //calculate performance
        performanceCalc = (totalEarnedTime/totTimeMinusDelay)*100;
        Log.i("Earned Time/Tot Time =",totalEarnedTime+"/"+totTimeMinusDelay);
        Log.i("Performance", performanceCalc+"");

        //call the activity changer to change to the summary activity
        activityChanger();

    }

    public void activityChanger(){
        //change to the summary activity and pass the necessary values
        Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
        //pass the performance
        intent.putExtra("performance",performanceCalc);
        //pass the earned time
        intent.putExtra("earned time",totalEarnedTime);
        //pass the total time
        intent.putExtra("total time",totTimeMinusDelay);
        //pass the delay time
        intent.putExtra("delay time",totSecondsDelay);
        //pass the produciton tasks
        intent.putExtra("production tasks",prodTaskArray);
        //pass the count of each prod task
        intent.putExtra("count", prodTaskCount);
        //pass the contribution of each production task
        intent.putExtra("contribution", earnedTimeContribution);
        //pass the values of the sliders
        intent.putExtra("slider",sliderValuesArrray);
        //pass the preferred methods to associate with the slider values
        intent.putExtra("preferred methods",prefMethodsArray);
        //pass the delay notes
        intent.putExtra("notes", delayNotesString);
        Log.i("from activity:", delayNotesString);
        startActivity(intent);


    }



    //insert asynctask to run DB operations-----------------------------------------------------------------------------------------------------
    //create an asynctask to fetch goal times and production tasks for the operation selected
    //the asynctask needs to pass the operation name to the php script, and the php script needs
    //to fetch the information for the variable passed
    public class ReadInfoFromDatabase2 extends AsyncTask<Void,Void,Void> {

        String read_info_url2;
        String response;

        //implement methods for AsyncTask
        @Override
        protected void onPreExecute() {
            //specify the domain name and php script
            //still need to make php file
            read_info_url2 ="http://192.168.1.67:80/read_info2.php";
        }

        @Override
        protected Void doInBackground(Void... args) {

            try {
                URL url = new URL(read_info_url2);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //set parameters for url connection
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                //need to create output stream to pass operation name to the php
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data_string = URLEncoder.encode("operationName","UTF-8")+"="+URLEncoder.encode(operationName,"UTF-8");
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

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
            //get the production task string and split it
            Log.i("this is the response:", response);
            responseArray= response.split(";");
            Log.i("Response Array:",responseArray[0]+" "+responseArray[1]);
            //the last digit of the prod task list is the last character
            //of the 0th index in the responseArray
            String lastDigit = responseArray[0].substring(responseArray[0].length()-1);
            int numOfProdTasks = Integer.valueOf(lastDigit);
            prodTaskArray = new String[numOfProdTasks];
            String prodTaskStringjoined =responseArray[0];
            prodTaskArray=prodTaskStringjoined.split("/");

            //do the same for the preferred methods, which is held
            //in the the first index of the response array
            String lastDigit2=responseArray[1].substring(responseArray[1].length()-1);
            int numOfPrefMethods=Integer.valueOf(lastDigit2);
            prefMethodsArray=new String[numOfPrefMethods];
            String prefMethodsJoined=responseArray[1];
            prefMethodsArray=prefMethodsJoined.split("/");

            //do the same for the goal times, which is held
            //in the the second index of the response array1)
            goalTimesArray = new String[numOfProdTasks];
            String goalTimesStringJoined=responseArray[2];
            goalTimesArray=goalTimesStringJoined.split("/");
            Log.i("Goal times", goalTimesStringJoined);

            //initialize the viewpager
            ViewPager viewPager = findViewById(R.id.viewPager);
            SwipeAdapter swipeAdapter = new SwipeAdapter(getSupportFragmentManager());
            viewPager.setAdapter(swipeAdapter);
            //create bundle to pass info
            Bundle bundle = new Bundle();
            bundle.putStringArray("pt",prodTaskArray);
            bundle.putStringArray("pm",prefMethodsArray);

            NewSessionActivityFragment newSessionActivityFragment = new NewSessionActivityFragment();
            newSessionActivityFragment.setArguments(bundle);
            //getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, newSessionActivityFragment);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Ending Session")
                .setMessage("Are you sure you want to close this session?\n(All progress will be lost)")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


}
