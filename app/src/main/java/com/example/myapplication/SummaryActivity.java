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
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    LinearLayout performanceCalcLayout, linearLayout, canvasLL;
    TextView performanceCalcTextview, delayNotesText, pumpText;
    signature mSignature;

    float earnedTime, performance, totalTime, delayTime, pumpScore;
    String [] prodTaskCount, gridDisplayText, sliderValues, preferredMethods, buckets;
    String goodMethods="";
    String badMethods="";
    String delayNotes, operation, uoms;
    String associateName, mentorName, associateID, date;
    String paceText, utilizationText, methodsText;
    float[] earnedTimeContribution;
    GridView gridView, gridView2;
    Button signButton, btnClear, submitSession;
    View view;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary2);

        //set views
        submitSession = findViewById(R.id.submitSession);
        linearLayout = findViewById(R.id.linearLayout);
        performanceCalcLayout = findViewById(R.id.performanceCalcLayout);
        performanceCalcTextview= findViewById(R.id.performanceCalcTextview);
        gridView = findViewById(R.id.gridView);
        gridView2=findViewById(R.id.gridView2);
        delayNotesText=findViewById(R.id.delayNotesText);
        pumpText=findViewById(R.id.pumpCalcTextview);

        //set views and methods for signature
        canvasLL = (LinearLayout) findViewById(R.id.canvasLL);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        canvasLL.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnClear = (Button) findViewById(R.id.clearBtn);


        view = canvasLL;

        //click listener for clear signature (clears signature space)
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.clear();
            }
        });


        //receive intent from NewSessionActivity
        operation = getIntent().getStringExtra("operation name");
        earnedTime= getIntent().getFloatExtra("earned time",0);
        totalTime= getIntent().getFloatExtra("total time",0);
        performance= getIntent().getFloatExtra("performance",0);
        delayTime= getIntent().getFloatExtra("delay time",0);
        prodTaskCount=getIntent().getStringArrayExtra("count");
        earnedTimeContribution=getIntent().getFloatArrayExtra("contribution");
        sliderValues = getIntent().getStringArrayExtra("slider");
        preferredMethods =getIntent().getStringArrayExtra("preferred methods");
        delayNotes =getIntent().getStringExtra("notes");
        paceText=getIntent().getStringExtra("pace");
        utilizationText=getIntent().getStringExtra("utilization");
        methodsText=getIntent().getStringExtra("methods");
        associateID = getIntent().getStringExtra("Associate ID");
        associateName = getIntent().getStringExtra("Associate Name");
        Log.i("Before the async:", associateName);
        mentorName = getIntent().getStringExtra("Mentor Name");
        date = getIntent().getStringExtra("Date");

        DecimalFormat df = new DecimalFormat("##.##");
        DecimalFormat df2 = new DecimalFormat("##.#");

        //set text for the performance formula
        performanceCalcTextview.setText(df.format(earnedTime/60)+" min"+" /("+df.format(totalTime/60)+" min -"+df.format(delayTime/60)+" min) ="+df.format(performance)+"%");
        //calculate pump and fill in the text
        pumpScore = (Float.parseFloat(paceText)/100)*(Float.parseFloat(utilizationText)/100)*(Float.parseFloat(methodsText)/100);
        pumpText.setText(paceText+"    X    "+utilizationText+"    X    "+methodsText+"    =    "+df2.format(pumpScore*100)+"%");
        //set text for the delay notes
        delayNotesText.setText(delayNotes);
        Log.i("from summary:", delayNotes);

        String[] prodTasks = getIntent().getStringArrayExtra("production tasks");
        String[] prodTasks2 = new String[prodTasks.length-1];
        prodTasks2= Arrays.copyOf(prodTasks, prodTasks.length-1);

        //create a new array with the prod task count, the prod task, and the contribution to the overall earned time
        gridDisplayText = new String[prodTaskCount.length-1];
        uoms = "";
        for(int i =0;i<prodTasks2.length;i++){
            gridDisplayText[i]=String.valueOf(prodTaskCount[i])+" "+prodTasks2[i]+"(s):"+" "+String.valueOf(df.format(earnedTimeContribution[i]))+" minutes";
            //creating uoms string so that we can store it in DB
            uoms = uoms+String.valueOf(prodTaskCount[i])+" "+prodTasks2[i]+"(s):"+" "+String.valueOf(df.format(earnedTimeContribution[i]))+" minutes"+"/";
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
                badMethods = "-"+badMethods+preferredMethods[i]+"/";
            }else{
                goodMethods = "-"+goodMethods+preferredMethods[i]+"/sou";
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

        //clicklistener for submit session
        submitSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundTask backgroundTask = new BackgroundTask();
                backgroundTask.execute(associateName, mentorName, date, operation, Float.toString(earnedTime), Float.toString(totalTime), Float.toString(delayTime), Float.toString(performance),uoms, goodMethods, badMethods, associateID, paceText,methodsText, utilizationText, Float.toString(pumpScore));
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

    //implement AsyncTask to run database operations---------------------------------------------------------------------------------------------------
    class BackgroundTask extends AsyncTask<String, Void, String> {

        //implement methods for AsyncTask

        String add_info_url;

        @Override
        protected void onPreExecute() {
            //specify the domain name and php script
            //still need to make php file
            add_info_url ="http://192.168.1.67:80/add_info_session.php";
        }

        @Override
        protected String doInBackground(String... args) {
            String associateName, mentorName, date, operation, earnedTime, totalTime, delayTime, performance, uoms, goodMethods,badMethods, associateID, pace, methods, utilization,pumpScore;
            //Blob signature;
            associateName=args[0];
            mentorName = args[1];
            date = args[2];
            operation = args[3];
            earnedTime = args[4];
            totalTime= args[5];
            delayTime = args[6];
            performance = args[7];
            uoms = args[8];
            goodMethods = args[9];
            badMethods = args[10];
            associateID = args[11];
            pace = args[12];
            methods = args[13];
            utilization = args[14];
            pumpScore=args[15];

            //make sure information is making it here
            Log.i("associateName", associateName);
            Log.i("date", date);
            Log.i("operation", operation);
            Log.i("earnedTime", earnedTime);
            Log.i("totalTime", totalTime);
            Log.i("delayTime", delayTime);
            Log.i("performance", performance);
            Log.i("uoms", uoms);
            Log.i("goodMethods", goodMethods);
            Log.i("badMethods", badMethods);
            Log.i("associateID", associateID);
            Log.i("pace", pace);
            Log.i("methods", methods);
            Log.i("utilization", utilization);
            Log.i("pumpScore", pumpScore);


            try {
                URL url = new URL(add_info_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //set parameters for url connection
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //create output stream object
                //to write to server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data_string = URLEncoder.encode("associateName","UTF-8")+"="+URLEncoder.encode(associateName,"UTF-8")+"&"+
                        URLEncoder.encode("mentorName","UTF-8")+"="+URLEncoder.encode(mentorName,"UTF-8")+"&"+
                        URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(date,"UTF-8")+"&"+
                        URLEncoder.encode("operation","UTF-8")+"="+URLEncoder.encode(operation,"UTF-8")+"&"+
                        URLEncoder.encode("earnedTime","UTF-8")+"="+URLEncoder.encode(earnedTime,"UTF-8")+"&"+
                        URLEncoder.encode("totalTime","UTF-8")+"="+URLEncoder.encode(totalTime,"UTF-8")+"&"+
                        URLEncoder.encode("delayTime","UTF-8")+"="+URLEncoder.encode(delayTime,"UTF-8")+"&"+
                        URLEncoder.encode("performance","UTF-8")+"="+URLEncoder.encode(performance,"UTF-8")+"&"+
                        URLEncoder.encode("uoms","UTF-8")+"="+URLEncoder.encode(uoms,"UTF-8")+"&"+
                        URLEncoder.encode("goodMethods","UTF-8")+"="+URLEncoder.encode(goodMethods,"UTF-8")+"&"+
                        URLEncoder.encode("badMethods","UTF-8")+"="+URLEncoder.encode(badMethods,"UTF-8")+"&"+
                        URLEncoder.encode("associateID","UTF-8")+"="+URLEncoder.encode(associateID,"UTF-8")+"&"+
                        URLEncoder.encode("pace","UTF-8")+"="+URLEncoder.encode(pace,"UTF-8")+"&"+
                        URLEncoder.encode("methods","UTF-8")+"="+URLEncoder.encode(methods,"UTF-8")+"&"+
                        URLEncoder.encode("utilization","UTF-8")+"="+URLEncoder.encode(utilization,"UTF-8")+"&"+
                        URLEncoder.encode("pumpScore","UTF-8")+"="+URLEncoder.encode(pumpScore,"UTF-8");
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //create input stream object to receive server response
                InputStream inputStream =httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                response="";
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    response+=line;
                }

                Log.i("Server response:", response);
                inputStream.close();
                httpURLConnection.disconnect();
                return "Session Added";

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
        protected void onPostExecute(String result) {
            Toast.makeText(SummaryActivity.this, result, Toast.LENGTH_LONG).show();

        }
    }
}

