package com.example.gs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    ArrayList<String> results = new ArrayList<String>();

    TextView weightTextView;
    TextView payTextView;
    TextView pointTextView;
    TextView idinfoTextView;

    Button finishBtn;

    private TextView nowDate;
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy년 M월 d일");

    private TextView nowTime;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("a hh시 mm분");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        Intent intent = getIntent();
        results = (ArrayList<String>) intent.getExtras().getSerializable("results");

        weightTextView = findViewById(R.id.gv);
        payTextView = findViewById(R.id.wv);
        pointTextView = findViewById(R.id.pointValue);
        idinfoTextView = findViewById(R.id.GSIdInfo);

        weightTextView.setText(results.get(0));
        payTextView.setText(String.valueOf((int)Double.parseDouble(results.get(1))));
        idinfoTextView.setText("GSID #" + results.get(2));
        pointTextView.setText(results.get(3));

        long time = System.currentTimeMillis();
        Date now = new Date(time);

        nowDate = (TextView) findViewById(R.id.dateValue);
        nowTime = (TextView) findViewById(R.id.timeValue);

        String day = dayFormat.format(now);
        String times = timeFormat.format(now);
        nowDate.setText(day);
        nowTime.setText(times);

        finishBtn = (Button) findViewById(R.id.finish);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);//화면전환
            }
        });

    }
}