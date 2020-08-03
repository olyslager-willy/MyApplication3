package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewSessionFragment extends Fragment {
    private String operations;
    String[] operationArray;
    LinearLayout operationList;
    Button refreshButton;

    //create instance of interface
    RefreshMethod refreshMethod;

    //create interface to communicate with Acivity
    public interface RefreshMethod{
        public void refreshRequest();

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new,container,false);
        //get arguments from bundle
        if(getArguments()!=null){
            operationArray=getArguments().getStringArray("operations");
        }

        if(operationArray==null){
            Toast.makeText(getContext(), "Server is not active", Toast.LENGTH_LONG).show();
        }

        //set views
        operationList=view.findViewById(R.id.operationList);
        refreshButton=view.findViewById(R.id.refreshBtn);

        //create an onclick listener for the refresh button
        //the MainActivity must be called again to trigger the asynctask
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send request to the activity to refresh the database information
                // by using the interface
                refreshMethod.refreshRequest();
            }
        });

        //call viewCreater to create UI elements for operations
        viewCreator();

        return view;
    }

    //implement method to create views
    public void viewCreator(){

        //programatically create views to display operations based on
        //what the server responded
        if (operationArray!=null){
            for(int i = 0;i<(operationArray.length-1);i++){
                Button button = new Button(getContext());
                button.setText(operationArray[i]);
                button.setId(i);
                button.setOnClickListener(clickListener);
                operationList.addView(button);
            }
        }

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button)v;
            String buttonText = b.getText().toString();
            //create intent to open session sheet activity
            Intent intent = new Intent(getContext(), MentorMenteeActivity.class);
            //pass the information about the operation selected
            intent.putExtra("Operation Name", buttonText);
            //open the session sheet activity
            startActivity(intent);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        refreshMethod = (NewSessionFragment.RefreshMethod) activity;

    }
}
