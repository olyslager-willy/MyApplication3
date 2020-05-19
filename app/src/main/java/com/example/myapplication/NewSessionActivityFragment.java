package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class NewSessionActivityFragment extends Fragment  {

    //define views
    TextView title;
    LinearLayout productionTaskLayout;
    LinearLayout counterPlusLayout;
    LinearLayout counterTextLayout;
    LinearLayout counterMinusLayout;
    Button delayStart, delayStop;
    TextView delayText;
    Chronometer delayChronometer;
    Button sessionStartBtn, sessionStopBtn;
    Chronometer sessionChronometer;

    //member variables
    String operationName;
    String[] prodTaskArray;
    String[] prodTaskCount;
    NewSessionActivity newSessionActivity;
    boolean running;
    boolean running2;
    long pauseOffset;
    long pauseOffset2;
    View v;

    //create instance of interface
    SummaryPageTrigger2 summaryPageTrigger2;

    public interface SummaryPageTrigger2{
        public void onSummaryPageTriggered2(String timeString, String timeString2, String[] prodTaskCount);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_activity_new_session,container,false);

        //assign the title of the page
        title = v.findViewById(R.id.operationNameText);
        title.setText(operationName);

        //set views
        productionTaskLayout=v.findViewById(R.id.productionTaskLayout);
        counterPlusLayout=v.findViewById(R.id.counterPlusLayout);
        counterTextLayout= v.findViewById(R.id.counterTextLayout);
        counterMinusLayout=v.findViewById(R.id.counterMinusLayout);
        sessionChronometer=v.findViewById(R.id.sessionChronometer);

        delayStart = v.findViewById(R.id.delayStartBtn);
        delayChronometer = v.findViewById(R.id.delayChronometer);
        sessionStartBtn = v.findViewById(R.id.sessionStart);
        //set delay elements invisible by default
        delayStart.setVisibility(View.INVISIBLE);
        delayChronometer.setVisibility(View.INVISIBLE);
        sessionChronometer.setVisibility(View.INVISIBLE);


        //create instance of newSession activity to access information
        newSessionActivity =(NewSessionActivity)getActivity();
        if(newSessionActivity.prodTaskArray!=null){
            prodTaskArray = newSessionActivity.prodTaskArray;
            Toast.makeText(newSessionActivity, prodTaskArray[0], Toast.LENGTH_SHORT).show();
        }

        //call the viewCreator
        viewCreator();

        //set click listener for the start session button
        sessionStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when this button is clicked we have to change the visibility of the delay
                //views so that the user can see them
                delayStart.setVisibility(View.VISIBLE);
                delayChronometer.setVisibility(View.VISIBLE);
                sessionChronometer.setVisibility(View.VISIBLE);
                //call the startChronometer method to start the timer
                startChronometer(sessionChronometer);
            }
        });


        //set a click listener for the start delay button
        delayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChronometerDelay(delayChronometer);
                delayChronometer.setVisibility(View.VISIBLE);
            }
        });


        return v;
    }
    //create chronomter methods for the Total Session Time
    public void startChronometer (View v){
        //check if time is running
        if(!running){
            //tell chronometer from which time we want to start it
            sessionChronometer.setBase(SystemClock.elapsedRealtime()-pauseOffset);
            sessionChronometer.start();
            sessionStartBtn.setBackgroundColor(Color.parseColor("#FF2400"));
            running=true;
            sessionStartBtn.setText("STOP SESSION");
            delayChronometer.setTextColor(Color.parseColor("#D5D0D0"));
        }else if (running){
            sessionChronometer.stop();
            pauseOffset=SystemClock.elapsedRealtime() - sessionChronometer.getBase();
            sessionStartBtn.setBackgroundColor(Color.parseColor("#6FDA10"));
            running=false;
            sessionStartBtn.setText("START SESSION");
            //pass values of sessionChronometer delayChronometer and prodTask Count to the activity through interface
            //populate the prodTaskCount array
            infoPasser();
        }

    }

    //create chronomter methods for the DELAY  Times
    public void startChronometerDelay (View v){
        //check if time is running
        if(!running2) {
            //tell chronometer from which time we want to start it
            delayChronometer.setBase(SystemClock.elapsedRealtime()-pauseOffset2);
            delayChronometer.start();
            running2 = true;;
            delayStart.setBackgroundColor(Color.parseColor("#FF2400"));
            delayStart.setText("STOP DELAY");
            sessionChronometer.setTextColor(Color.parseColor("#D5D0D0"));
            delayChronometer.setTextColor(Color.parseColor("#000000"));
        }else if (running2){
            delayChronometer.stop();
            running2=false;
            pauseOffset2=SystemClock.elapsedRealtime() - delayChronometer.getBase();
            sessionChronometer.setVisibility(View.VISIBLE);
            delayStart.setBackgroundColor(Color.parseColor("#6FDA10"));
            delayStart.setText("START DELAY");
            sessionChronometer.setTextColor(Color.parseColor("#000000"));
            delayChronometer.setTextColor(Color.parseColor("#D5D0D0"));
        }

    }

    //implement method to create views
    public void viewCreator(){
        //programmatically create views to display operations based on
        //what the server responded

        for(int i = 0;i<(prodTaskArray.length-1);i++){
            //create the production task textviews
            TextView textView = new TextView(getContext());
            textView.setText(prodTaskArray[i]);
            textView.setTextSize(22);
            textView.setPadding(0,20,0,0);
            textView.setGravity(1);
            textView.setId(i);
            productionTaskLayout.addView(textView);

            //now create the counter (-) buttons
            Button button = new Button(getContext());
            button.setText("-");
            button.setId(i+10);
            button.setTextSize(20);
            button.setHeight(1);
            button.setWidth(1);
            button.setOnClickListener(minusListener);
            counterPlusLayout.addView(button);


            //create the textview to display the count
            TextView textView1 = new TextView(getContext());
            textView1.setText("0");
            textView1.setId(i+20);
            textView1.setTextSize(30);
            textView1.setHeight(67);
            textView1.setWidth(67);
            counterTextLayout.addView(textView1);

            //create the counter(+) buttons
            Button button1 = new Button(getContext());
            button1.setText("+");
            button1.setId(i+30);
            button1.setTextSize(20);
            button1.setHeight(1);
            button1.setWidth(1);
            button1.setOnClickListener(plusListener);
            counterMinusLayout.addView(button1);

        }
    }

    //set onclick listener for the plus counters
    View.OnClickListener plusListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //change the textview corresponging to that button when the
            //button is pressed
            Button b = (Button)view;
            int identifier = b.getId();
            //get corresponding textview
            TextView textView1 = v.findViewById(identifier-10);
            //extract text from textview
            String count = textView1.getText().toString();
            //change text from string to int and add 1
            int intCount = Integer.parseInt(count)+1;
            //update the text
            textView1.setText(Integer.toString(intCount));

        }

    };

    //set onclick listener for the minus counters
    View.OnClickListener minusListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //change the textview corresponging to that button when the
            //button is pressed
            Button b = (Button)view;
            int identifier = b.getId();
            //get corresponding textview
            TextView textView1 = v.findViewById(identifier+10);
            //extract text from textview
            String count = textView1.getText().toString();
            //change text from string to int and add 1
            int intCount2 = Integer.parseInt(count)-1;
            //update the text
            textView1.setText(Integer.toString(intCount2));

        }

    };

    public void infoPasser(){
        prodTaskCount= new String[prodTaskArray.length];
        for(int i=0;i<prodTaskArray.length-1;i++){
            Log.i("number:", ""+i);
            TextView textView1 = v.findViewById(i+20);
            prodTaskCount[i] = textView1.getText().toString();
        }

        summaryPageTrigger2.onSummaryPageTriggered2(sessionChronometer.getText().toString(),delayChronometer.getText().toString(), prodTaskCount);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
         summaryPageTrigger2= (SummaryPageTrigger2) activity;

    }

}
