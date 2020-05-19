package com.example.myapplication;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

public class NOA_ptgt extends Fragment {

    Button backBtn;
    //defining Views
    LinearLayout productionTaskList;
    LinearLayout goalTimesList;
    LinearLayout prefMethodsList;
    Button nextButton;
    TextView networkStatus;

    //Variables
    String opName;
    String deptName;
    int i=0;
    int j=0;
    String numberOfPT, numOfPM;
    String productionTaskString ="";
    String goalTimesString="";
    String preferredMethodsString="";

    //create instance of interface
    MessageSender2 messageSender2;

    //create interface to communicate with Acivity
    public interface MessageSender2{
        public void onMessageSent2(String opName, String productionTasks,String goalTimes, String preferredMethodsString );

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_configuration_pt_gt,container,false);

        //set views
        backBtn = view.findViewById(R.id.backBtn);
        nextButton=view.findViewById(R.id.nextButton);
        productionTaskList=view.findViewById(R.id.productionTaskListLayout);
        goalTimesList=view.findViewById(R.id.goalTimeListLayout);
        prefMethodsList=view.findViewById(R.id.prefMethodsLayout);
        networkStatus =view.findViewById(R.id.networkStatus);

        //get String values from bundle
        Bundle bundle = getArguments();
        opName = bundle.getString("operation");
        numberOfPT=bundle.getString("pt");
        deptName = bundle.getString("depts");
        numOfPM=bundle.getString("pm");
        Log.i("# of PM", numOfPM);

        //create UI elements programmatically for the preferred methods entry fields
        for(j=1;j<=Integer.parseInt(numOfPM);j++){
            EditText editText3 = new EditText(getContext());
            editText3.setHint("Preferred Method "+j);
            editText3.setId(j+20);
            prefMethodsList.addView(editText3);
            Log.i("count", String.valueOf(j));
        }

        //create UI elements programmatically (inside of the productionTaskList Linear Layout)r
        //Then try to create those UI elements using a for loop to create several
        for (i = 1; i <= Integer.parseInt(numberOfPT); i++) {
            //create a Edit Texts for Production  tasks
            //id's for production Task edit texts start at 1,2,3... etc
            EditText editText = new EditText(getContext());
            editText.setHint("Production Task "+i);
            editText.setId(i);
            productionTaskList.addView(editText);

            //create edit texts for Goal times
            //id's for goal time edit texts start at 50, 51, 52, .. etc
            EditText editText2 = new EditText(getContext());
            editText2.setHint("Goal Time "+i);
            editText2.setId(i+50);
            goalTimesList.addView(editText2);

        }




        //create object of ConnectivityManager to show whether or not there is a
        //network available
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            //if there is an internet connection, then hide the textview
            networkStatus.setVisibility(View.INVISIBLE);
        }else{
            nextButton.setEnabled(false);
        }



        //set onclicklistener for the next button
       nextButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //for loop to assign production task texts and goal time texts to arrays
               //so that we can send the values to the database
               for (int j =1;j<=Integer.parseInt(numberOfPT);j++) {
                   EditText editText = view.findViewById(j);
                   EditText editText2=view.findViewById(j+50);
                   //add all prod tasks to the same string with a delimeter "/" between them
                   //for ease of storage in the database.. same for goal times]
                   productionTaskString = productionTaskString + editText.getText().toString() + "/";
                   goalTimesString = goalTimesString + editText2.getText().toString() + "/";
               }

               for(int i = 1;i<=Integer.parseInt(numOfPM);i++){
                   EditText editext3 = view.findViewById(i+20);
                   //add all pref methods to the same string with a delimeter "/"
                   preferredMethodsString = preferredMethodsString+editext3.getText().toString()+"/";

               }

               //pass information to the Activity through the interface
               messageSender2.onMessageSent2(opName, productionTaskString, goalTimesString,preferredMethodsString);

               //display a Toast to inform the user that the operation has been added
               Toast.makeText(getContext(),
                       "Operation Added Successfully", Toast.LENGTH_SHORT).show();
           }
       });

        //set on click listener for back button.. takes user back to previous fragment
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container2, new OperationNameFragment()).commit();
            }
        });

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        messageSender2 = (NOA_ptgt.MessageSender2) activity;

    }




}
