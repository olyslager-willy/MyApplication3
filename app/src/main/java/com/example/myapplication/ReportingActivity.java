package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
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

public class ReportingActivity extends AppCompatActivity {

    String response;
    String[] fullResponseArray;
    String[] summaryInfo;
    String associate_name;
    int arrSize;
    private ExampleAdapter mAdapter;
    private List<ExampleItem>exampleList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting);

        //create an arrayList
        exampleList = new ArrayList<>();

        associate_name=getIntent().getStringExtra("associate name");
        //start Asynctask to extract information from database
        ReportingActivity.ReadInfoFromDatabase readInfoFromDatabase = new ReportingActivity.ReadInfoFromDatabase();
        readInfoFromDatabase.execute();


        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new ExampleAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }




    private void fillExampleList() {
       //this is where our list will be populated
        for(int i=2; i<arrSize;i+=16){
            exampleList.add(new ExampleItem(summaryInfo[i]));
        }

        //call method that sets up recyclerView
        setUpRecyclerView();

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                associate_name= exampleList.get(position).getText();
                //get the index of the selected date
                int selectedDateIndex=2+(16*position);
                Toast.makeText(ReportingActivity.this, ""+summaryInfo[selectedDateIndex], Toast.LENGTH_SHORT).show();
                //pass info to summaryActivity with an intent and start the summary activity
                Intent intent = new Intent(getApplicationContext(), SummaryReportingActivity.class);
                intent.putExtra("operation name", summaryInfo[selectedDateIndex+1]);
                intent.putExtra("earned time", summaryInfo[selectedDateIndex+2]);
                intent.putExtra("total time", summaryInfo[selectedDateIndex+3]);
                intent.putExtra("delay time", summaryInfo[selectedDateIndex+4]);
                intent.putExtra("performance", summaryInfo[selectedDateIndex+5]);
                intent.putExtra("uoms", summaryInfo[selectedDateIndex+6]);
                intent.putExtra("good methods", summaryInfo[selectedDateIndex+7]);
                intent.putExtra("bad methods", summaryInfo[selectedDateIndex+8]);
                intent.putExtra("associate ID", summaryInfo[selectedDateIndex+9]);
                intent.putExtra("pace", summaryInfo[selectedDateIndex+10]);
                intent.putExtra("methods", summaryInfo[selectedDateIndex+11]);
                intent.putExtra("utilization", summaryInfo[selectedDateIndex+12]);
                intent.putExtra("pump score", summaryInfo[selectedDateIndex+13]);
                startActivity(intent);
            }
        });
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(exampleList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    //Implement Asynctask to run database operations--------------------------------------------------------------------------------------------
    public class ReadInfoFromDatabase extends AsyncTask<Void, Void, Void> {

        String read_info_url;

        //implement methods for AsyncTask
        @Override
        protected void onPreExecute() {
            //specify the domain name and php script
            //still need to make php file
            read_info_url = "http://192.168.1.67:80/read_info3.php";
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
                //need to create output stream to pass operation name to the php
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data_string = URLEncoder.encode("associate_name","UTF-8")+"="+URLEncoder.encode(associate_name,"UTF-8");
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
            //get the last 2 digits of the response to get the number of operations in the database
            //so that we can initialize the operations array to the proper size
            //I NEED TO ADD A FAILSAFE TO THIS.. WHAT HAPPENS IF THE # OR OPERATIONS IS >9?
            //WE WILL HAVE TO READ 2 DIGITS INSTEAD OF 1.. CHANGE IN PHP CODE
            if (response != null) {
                Log.i("ReportingAsync:", response);
                //if(Integer.parseInt(response.substring(response.length()-1))>1) {
                    fullResponseArray = new String[2];
                    fullResponseArray = response.split(",");
                    Log.i("Full response arr", fullResponseArray[0]+" "+fullResponseArray[1]);
                    //get the number of entries for the size of the array
                    arrSize=Integer.parseInt(fullResponseArray[1]);
                    summaryInfo = new String[arrSize];
                    summaryInfo=fullResponseArray[0].split("&");
                fillExampleList();

            } else {
                Log.i("ReportingAsync:", "null");
            }
        }
    }
}

