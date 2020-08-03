package com.example.myapplication;

import android.content.Intent;

public class ExampleItem {
    private String mText;

    //create constructor
    public ExampleItem(String text){
        mText = text;
    }

    // in order to get this value we need to create a get method
    public String getText(){
        return mText;
    }


}