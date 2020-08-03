package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ReportingFragment extends Fragment {
    String associate_name;
    String[] associatesArray;
    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ExampleItem> exampleList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reporting, container, false);

        setHasOptionsMenu(true);

        //receive the associatesArray from the main activity
        associatesArray = getArguments().getStringArray("associates");
        //create an arrayList
        exampleList = new ArrayList<>();

        //need a for loop that will iterate through an array(composed of associate names) and add each element
        //of the array to the ArrayList
        if (associatesArray==null){
            Toast.makeText(getContext(), "Server is not active", Toast.LENGTH_LONG).show();
        }else{
            for (int i = 0; i < associatesArray.length - 1; i++) {
                exampleList.add(new ExampleItem(associatesArray[i]));
            }
        }
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ExampleAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                associate_name= exampleList.get(position).getText();
                //open the reporting activity and pass the associate name
                Toast.makeText(getContext(), "position"+position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ReportingActivity.class);
                intent.putExtra("associate name", associate_name);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.example_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }


        });

    }



}
