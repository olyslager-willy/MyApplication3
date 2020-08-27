package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class NewOperationActivity extends AppCompatActivity implements OperationNameFragment.MessageSender, NOA_ptgt.MessageSender2 {
    private NOA_ptgt noa_ptgt;
    private OperationNameFragment operationNameFragment;

    String operation_name;
    String deptName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_operation);

        //set the fragments
        noa_ptgt = new NOA_ptgt();
        operationNameFragment = new OperationNameFragment();
        //show the user form to enter operation name and production tasks
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, new OperationNameFragment()).commit();

    }

    @Override
    public void onMessageSent(String opName, String numOfPT, String departmentName, String numOfPM) {
        //get the operation name
        operation_name=opName;
        deptName=departmentName;
        //get information from first fragment and pass it
        //to the next
        NOA_ptgt noa_ptgt = new NOA_ptgt();
        Bundle bundle = new Bundle();
        bundle.putString("operation",opName);
        bundle.putString("pt", numOfPT);
        bundle.putString("pm", numOfPM);
        noa_ptgt.setArguments(bundle);

        //start next fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, noa_ptgt,null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onMessageSent2(String opName, String productionTasks, String goalTimes, String preferredMethodsString) {
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(operation_name, productionTasks,goalTimes, deptName, preferredMethodsString);
        Log.i("after execute", deptName);
        finish();
    }


    //implement AsyncTask to run database operations---------------------------------------------------------------------------------------------------
    class BackgroundTask extends AsyncTask<String, Void, String> {

        //implement methods for AsyncTask

        String add_info_url;

        @Override
        protected void onPreExecute() {
            //specify the domain name and php script
            //still need to make php file
            add_info_url ="http://10.32.32.68" +
                    ":80//add_info.php";
        }

        @Override
        protected String doInBackground(String... args) {
            String operationName, productionTaskString, goalTimesString, preferredMethodsString;
            operationName=args[0];
            productionTaskString=args[1];
            goalTimesString=args[2];
            deptName=args[3];
            preferredMethodsString=args[4];


            //testing to see if this code is executing
            Log.i("NewOpActivity:", "Code is executing this time");
            Log.i("Operation Name: ",operationName);
            Log.i("Production Tasks ",productionTaskString);
            Log.i("Goal Times",goalTimesString);
            Log.i("Department Name", deptName);
            Log.i("Preferred Methods:", preferredMethodsString);

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

                String data_string = URLEncoder.encode("operationName","UTF-8")+"="+URLEncoder.encode(operationName,"UTF-8")+"&"+
                        URLEncoder.encode("productionTaskString","UTF-8")+"="+URLEncoder.encode(productionTaskString,"UTF-8")+"&"+
                        URLEncoder.encode("goalTimesString","UTF-8")+"="+URLEncoder.encode(goalTimesString,"UTF-8")+"&"+
                        URLEncoder.encode("deptName","UTF-8")+"="+URLEncoder.encode(deptName,"UTF-8")+"&"+
                        URLEncoder.encode("preferredMethodsString","UTF-8")+"="+URLEncoder.encode(preferredMethodsString,"UTF-8");
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //create input stream object to receive server response
                InputStream inputStream =httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
                return "Operation Added";

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
            Toast.makeText(NewOperationActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Ending Session")
                .setMessage("Are you sure you want to go back?\n(All progress will be lost)")
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
