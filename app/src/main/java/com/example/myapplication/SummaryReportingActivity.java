package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SummaryReportingActivity extends AppCompatActivity {

    LinearLayout performanceCalcLayout, linearLayout;
    TextView performanceCalcTextview, delayNotesText, pumpText, uomsText, goodMethodsText, badMethodsText;
    GridView gridView, gridView2;
    String operationName, earnedTime, totalTime,delayTime, performance, uoms, goodMethods,
            badMethods, associateID, pace, methods, utilization, pumpScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_reporting);

        //set views
        linearLayout = findViewById(R.id.linearLayout);
        performanceCalcLayout = findViewById(R.id.performanceCalcLayout);
        performanceCalcTextview= findViewById(R.id.performanceCalcTextview);
        gridView = findViewById(R.id.gridView);
        gridView2=findViewById(R.id.gridView2);
        delayNotesText=findViewById(R.id.delayNotesText);
        pumpText=findViewById(R.id.pumpCalcTextview);
        uomsText=findViewById(R.id.uomsText);
        goodMethodsText=findViewById(R.id.goodMethods);
        badMethodsText=findViewById(R.id.badMethods);

        //receive intent
        operationName = getIntent().getStringExtra("operation name");
        earnedTime = getIntent().getStringExtra("earned time");
        totalTime = getIntent().getStringExtra("total time");
        delayTime = getIntent().getStringExtra("oelay time");
        performance = getIntent().getStringExtra("performance");
        uoms = getIntent().getStringExtra("uoms");
        goodMethods = getIntent().getStringExtra("good methods");
        badMethods = getIntent().getStringExtra("bad methods");
        associateID = getIntent().getStringExtra("associate ID");
        pace = getIntent().getStringExtra("pace");
        methods = getIntent().getStringExtra("methods");
        utilization = getIntent().getStringExtra("utilization");
        pumpScore = getIntent().getStringExtra("pump score");
        float pumpScoreInt=Float.parseFloat(pumpScore);
        String pumpScoreString=Float.toString(pumpScoreInt*100);

        performanceCalcTextview.setText("("+earnedTime+" min)/("+totalTime+" min - "+ delayTime+" min)="+performance+"%");
        pumpText.setText(pace+"    X    "+utilization+"    X    "+methods+"    =    "+pumpScoreString+"%");
        uomsText.setText(uoms);
        goodMethodsText.setText(goodMethods);
        badMethodsText.setText(badMethods);
    }
}
