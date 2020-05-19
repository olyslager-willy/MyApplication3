package com.example.myapplication;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OperationNameFragment extends Fragment {

    EditText operationNameEditText, departmentNameEditText;
    EditText numberOfProductionTasksEditText, numOfPrefMethdodsEditText;
    Button nextButton;

    //Define strings to store editText values
    String operationName, departmentName;
    String numberOfPT, numberOfPM;

    //create instance of interface
    MessageSender messageSender;

    //create interface to communicate with Acivity
    public interface MessageSender{
        public void onMessageSent(String opName, String numOfPT, String departmentName, String numOfPM);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_configuration_new_operation_name,container,false);

        //set views
        nextButton=view.findViewById(R.id.nextButton);
        operationNameEditText=view.findViewById(R.id.operationNameEditText);
        departmentNameEditText = view.findViewById(R.id.departmentNameEditText);
        numberOfProductionTasksEditText=view.findViewById(R.id.numberOfProductionTasksEditText);
        numOfPrefMethdodsEditText = view.findViewById(R.id.numberOfPrefMethodsEditText);

        //set clicklistener for nxtButton to take user to pt_gt fragment after clicking the next button
        //in this click listener we also pass the information entered into the edit texts to the next
        //fragment
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass information to the next fragment
                operationName= operationNameEditText.getText().toString();
                departmentName = departmentNameEditText.getText().toString();
                numberOfPT = numberOfProductionTasksEditText.getText().toString();
                numberOfPM=numOfPrefMethdodsEditText.getText().toString();
                Log.i("from first frag",numberOfPM);
                messageSender.onMessageSent(operationName, numberOfPT, departmentName, numberOfPM);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        messageSender = (MessageSender) activity;

    }

}
