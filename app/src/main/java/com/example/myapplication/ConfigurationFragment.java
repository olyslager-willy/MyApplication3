package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ConfigurationFragment extends Fragment {

    LinearLayout editOperationLayout;
    LinearLayout createNewOperationLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuration,container,false);

        //set layouts
        editOperationLayout =view.findViewById(R.id.editOperationLayout);
        createNewOperationLayout= view.findViewById(R.id.createNewOperationLayout);

        //set click listener for edit operations layout
        editOperationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "This feature is not available at this time", Toast.LENGTH_SHORT).show();
            }
        });

        //set click listener for create new operation layout

        createNewOperationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfigCreateNewOperationName();

            }
        });



        return view;
    }

    public void openConfigCreateNewOperationName(){
        Intent intent = new Intent(getContext(), NewOperationActivity.class);
        startActivity(intent);
    }
}
