package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MentorMenteeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String strDate;
    String operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_mentee);
        operation = getIntent().getStringExtra("Operation Name");

        Button button = findViewById(R.id.dateButton);
        Button submitBtn = findViewById(R.id.submitBtn);
        final EditText associateName= findViewById(R.id.associateName);
        final EditText mentorName = findViewById(R.id.mentorName);
        final EditText associateID = findViewById(R.id.associateName);
        //set click listener for the date picker
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        //set click listener for the submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open the New Session Activity and pass the operation name, Date, associate name, and mentor name to it
                Intent intent  = new Intent(getApplicationContext(), NewSessionActivity.class);
                intent.putExtra("Date", strDate);
                intent.putExtra("Associate Name", associateName.getText().toString());
                intent.putExtra("Mentor Name", mentorName.getText().toString());
                intent.putExtra("Operation Name",operation );
                intent.putExtra("Associate ID", associateID.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
    c.set(Calendar.YEAR, year);
    c.set(Calendar.MONTH, month);
    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        strDate = format.format(c.getTime());
        TextView textView = findViewById(R.id.dateText);
        textView.setText(currentDateString);
    }
}
