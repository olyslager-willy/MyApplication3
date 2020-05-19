package com.example.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class SwipeAdapter extends FragmentStatePagerAdapter {
    NewSessionActivityFragment newSessionActivityFragment;
    PreferredMethodsFragment preferredMethodsFragment;

    public SwipeAdapter(FragmentManager fm){
        super (fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                 newSessionActivityFragment = new NewSessionActivityFragment();
                return newSessionActivityFragment;
            case 1:
                 preferredMethodsFragment = new PreferredMethodsFragment();
                return  preferredMethodsFragment;
            default:
                break;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
