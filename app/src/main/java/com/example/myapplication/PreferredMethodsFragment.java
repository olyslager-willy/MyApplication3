package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;



public class PreferredMethodsFragment extends Fragment {

    NewSessionActivity newSessionActivity;
    //views
    LinearLayout preferredMethodsLayout;
    LinearLayout sliderLayout;
    LinearLayout textLayout;
    String []prefMethodsArray;
    String[] sliderValues;
    Button endButton;
    EditText delayNotes, paceText, utilizationText, methodsText;
    View v;

    //create instance of interface
    SummaryPageTrigger summaryPageTrigger;

    public interface SummaryPageTrigger{
        public void onSummaryPageTriggered(String[]sliderValues, String delayNotes, String pace, String utilization, String methods);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v= inflater.inflate(R.layout.fragment_preferred_methods,container,false);

        //retrieve information from activity
        //create instance of newSession activity to access information
        newSessionActivity =(NewSessionActivity)getActivity();
        prefMethodsArray = newSessionActivity.prefMethodsArray;
        Log.i("Pref Methods in frag:",prefMethodsArray[0]);
        //set views
        preferredMethodsLayout=v.findViewById(R.id.preferredMethodsLayout);
        sliderLayout = v.findViewById(R.id.slidersLayout);
        textLayout=v.findViewById(R.id.textLayout);
        endButton=v.findViewById(R.id.endButton);
        delayNotes =v.findViewById(R.id.delayNotes);
        paceText=v.findViewById(R.id.paceText);
        utilizationText=v.findViewById(R.id.utilizationText);
        methodsText=v.findViewById(R.id.utilizationText);
        //set onclick listener for endButton...
        //it should call the interface which will trigger the activity to fetch the necessary data
        //and change to the summary page activity
        endButton.setOnClickListener(endButtonListener);
        //call viewCreator Method
        viewCreator();

        return v;
    }

    View.OnClickListener endButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sliderValues = new String[prefMethodsArray.length];
            //calling interface method
            //in this interface method we have to pass the slider values and
            //the user's comments
            for(int i=0;i<prefMethodsArray.length-1;i++){
                TextView textView1 = v.findViewById(i+10);
                sliderValues[i]=textView1.getText().toString();
            }
           String delayNotesString= delayNotes.getText().toString();
            String pace = paceText.getText().toString();
            String util= utilizationText.getText().toString();
            String methods = methodsText.getText().toString();
            Log.i("from pref meth:", delayNotesString);
            summaryPageTrigger.onSummaryPageTriggered(sliderValues,delayNotesString, pace, util, methods);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void viewCreator(){

        for(int i = 0;i<(prefMethodsArray.length-1);i++) {
            //create the production task textviews
            TextView textView = new TextView(getContext());
            textView.setText(prefMethodsArray[i]);
            textView.setTextSize(22);
            if (i==0){
                textView.setPadding(10, 20, 20, 0);
                textView.setId(i);
                preferredMethodsLayout.addView(textView);
            }else{
                textView.setPadding(10, 60, 20, 0);
                textView.setId(i);
                preferredMethodsLayout.addView(textView);
            }

            //create the sliders
            ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
            SeekBar seekBar = new SeekBar(getContext());
            thumb.setIntrinsicHeight(80);
            thumb.setIntrinsicWidth(30);
            seekBar.setThumb(thumb);
            seekBar.setProgress(1);
            seekBar.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(200, 100);
            seekBar.setLayoutParams(lp);
            seekBar.setId(i);
            sliderLayout.addView(seekBar);
            //assign a listener to each seekbar
            seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

            //create the textViews
            TextView textView1 = new TextView(getContext());
            textView1.setText("1");
            textView1.setId(i+10);
            textView1.setTextSize(18);
            if (i==0){
                textView1.setPadding(10, 20, 20, 0);
            }else{
                textView1.setPadding(10, 60, 20, 0);

            }
            textLayout.addView(textView1);

        }

    }

    OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
           int id= seekBar.getId();
           TextView textView1 = v.findViewById(id+10);
           //if statements to determine if its a 1, 2 or 3
            if(progress<33){
                textView1.setText("1");
            }else if (progress<66){
                textView1.setText("2");
            }else{
                textView1.setText("3");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        summaryPageTrigger = (SummaryPageTrigger) activity;

    }


}
