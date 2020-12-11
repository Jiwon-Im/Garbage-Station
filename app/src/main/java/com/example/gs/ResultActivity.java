package com.example.gs;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ResultActivity extends AppCompatActivity {

    private TextView nowDate;
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy년 M월 d일");

    private TextView nowTime;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("a hh시 mm분");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        long time = System.currentTimeMillis();
        Date now = new Date(time);

        nowDate = (TextView)findViewById(R.id.dateValue);
        nowTime = (TextView)findViewById(R.id.timeValue);

        String day = dayFormat.format(now);
        String times = timeFormat.format(now);
        nowDate.setText(day);
        nowTime.setText(times);
    }
}