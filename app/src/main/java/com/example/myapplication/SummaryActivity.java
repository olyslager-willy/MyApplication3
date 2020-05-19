package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    LinearLayout performanceCalcLayout, linearLayout, canvasLL;
    TextView performanceCalcTextview, delayNotesText;
    signature mSignature;

    float earnedTime, performance, totalTime, delayTime;
    String [] prodTaskCount, gridDisplayText, sliderValues, preferredMethods, buckets;
    String goodMethods="";
    String badMethods="";
    String delayNotes;
    float[] earnedTimeContribution;
    GridView gridView, gridView2;
    Button signButton, btnClear;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary2);

        //set views
        linearLayout = findViewById(R.id.linearLayout);
        performanceCalcLayout = findViewById(R.id.performanceCalcLayout);
        performanceCalcTextview= findViewById(R.id.performanceCalcTextview);
        gridView = findViewById(R.id.gridView);
        gridView2=findViewById(R.id.gridView2);
        delayNotesText=findViewById(R.id.delayNotesText);

        //set views and methods for signature
        canvasLL = (LinearLayout) findViewById(R.id.canvasLL);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        canvasLL.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnClear = (Button) findViewById(R.id.clearBtn);


        view = canvasLL;

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.clear();
            }
        });


        //receive intent from NewSessionActivity
        earnedTime= getIntent().getFloatExtra("earned time",0);
        performance= getIntent().getFloatExtra("performance",0);
        totalTime= getIntent().getFloatExtra("total time",0);
        delayTime= getIntent().getFloatExtra("delay time",0);
        prodTaskCount=getIntent().getStringArrayExtra("count");
        earnedTimeContribution=getIntent().getFloatArrayExtra("contribution");
        sliderValues = getIntent().getStringArrayExtra("slider");
        preferredMethods =getIntent().getStringArrayExtra("preferred methods");
        delayNotes =getIntent().getStringExtra("notes");

        DecimalFormat df = new DecimalFormat("##.##");

        //set text for the performance formula
        performanceCalcTextview.setText(df.format(earnedTime/60)+" min"+" /("+df.format(totalTime/60)+" min -"+df.format(delayTime/60)+" min) ="+df.format(performance)+"%");
        //set text for the delay notes
        delayNotesText.setText(delayNotes);
        Log.i("from summary:", delayNotes);

        String[] prodTasks = getIntent().getStringArrayExtra("production tasks");
        String[] prodTasks2 = new String[prodTasks.length-1];
        prodTasks2= Arrays.copyOf(prodTasks, prodTasks.length-1);

        //create a new array with the prod task count, the prod task, and the contribution to the overall earned time
        gridDisplayText = new String[prodTaskCount.length-1];
        for(int i =0;i<prodTasks2.length;i++){
            gridDisplayText[i]=String.valueOf(prodTaskCount[i])+" "+prodTasks2[i]+"(s):"+" "+String.valueOf(df.format(earnedTimeContribution[i]))+" minutes";
            Log.i("gridText: ", gridDisplayText[i]);
        }




        // Populate a List from Array elements
        final List<String> prodTaskList = new ArrayList<String>(Arrays.asList(gridDisplayText));

        // Data bind GridView with ArrayAdapter (String Array elements)
        gridView.setNumColumns(3);
        gridView.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, prodTaskList){
            public View getView(int position, View convertView, ViewGroup parent) {

                // Return the GridView current item as a View
                View view = super.getView(position,convertView,parent);

                // Convert the view as a TextView widget
                TextView tv = (TextView) view;
                tv.setTextSize(30);

                //tv.setTextColor(Color.DKGRAY);

                // Set the layout parameters for TextView widget
                RelativeLayout.LayoutParams lp =  new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                );

                tv.setLayoutParams(lp);

                // Get the TextView LayoutParams
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tv.getLayoutParams();

                // Set the width of TextView widget (item of GridView)
                /*
                    IMPORTANT
                        Adjust the TextView widget width depending
                        on GridView width and number of columns.

                        GridView width / Number of columns = TextView width.

                        Also calculate the GridView padding, margins, vertical spacing
                        and horizontal spacing.
                 */
                params.width = getPixelsFromDPs(SummaryActivity.this,168);

                // Set the TextView layout parameters
                tv.setLayoutParams(params);

                // Display TextView text in center position
                tv.setGravity(Gravity.CENTER);

                // Set the TextView text font family and text size
                tv.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                // Set the TextView text (GridView item text)
                tv.setText(prodTaskList.get(position));

                // Set the TextView background color
                //tv.setBackgroundColor(Color.parseColor("#d9d5dc"));


                // Return the TextView widget as GridView item
                return tv;
            }
        });


        //for the second gridView first determine which methods were done well and which weren't
        for(int i =0; i<preferredMethods.length-1;i++){
            if(Integer.parseInt(sliderValues[i])<3){
                badMethods = "-"+badMethods+preferredMethods[i]+" ";
            }else{
                goodMethods = "-"+goodMethods+preferredMethods[i]+" ";
            }
        }
        //now we have to change the strings into an array so that we can pass them to the gridView with the arrayAdapter
        buckets=new String[2];
        buckets[0]=goodMethods;
        buckets[1]=badMethods;

        // Populate a List from Array elements
        final List<String> buckets2 = new ArrayList<String>(Arrays.asList(buckets));

        //create another gridView to show the good and bad methods
        // Data bind GridView with ArrayAdapter (String Array elements)
        gridView2.setNumColumns(2);
        gridView2.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, buckets2){
            public View getView(int position, View convertView, ViewGroup parent) {

                // Return the GridView current item as a View
                 View view2 = super.getView(position,convertView,parent);

                // Convert the view as a TextView widget
                TextView tv = (TextView) view2;
                tv.setTextSize(30);

                //tv.setTextColor(Color.DKGRAY);

                // Set the layout parameters for TextView widget
                RelativeLayout.LayoutParams lp =  new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                );

                tv.setLayoutParams(lp);

                // Get the TextView LayoutParams
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tv.getLayoutParams();

                // Set the width of TextView widget (item of GridView)
                /*
                    IMPORTANT
                        Adjust the TextView widget width depending
                        on GridView width and number of columns.

                        GridView width / Number of columns = TextView width.

                        Also calculate the GridView padding, margins, vertical spacing
                        and horizontal spacing.
                 */
                params.width = getPixelsFromDPs(SummaryActivity.this,168);

                // Set the TextView layout parameters
                tv.setLayoutParams(params);
                // Display TextView text in center position
                tv.setGravity(Gravity.CENTER);

                // Set the TextView text font family and text size
                tv.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                // Set the TextView text (GridView item text)
                tv.setText(buckets2.get(position));

                // Set the TextView background color
                //tv.setBackgroundColor(Color.parseColor("#d9d5dc"));


                // Return the TextView widget as GridView item
                return tv;
            }
        });


    }

    // Method for converting DP value to pixels
    public static int getPixelsFromDPs(Activity activity, int dps){
        Resources r = activity.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }

    //create signature class

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }



        public void clear() {
            path.reset();
            invalidate();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}

