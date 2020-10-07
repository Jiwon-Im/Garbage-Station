package com.example.gs;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QrActivity extends AppCompatActivity {

    private Button paymentBtn;
    private Button cancelBtn;
/*
    private Button menuBtn;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_q_r);

        Intent intent = getIntent(); /*데이터 수신*/

        int getcode = intent.getExtras().getInt("qrcode", 1);//얘도 receiveqr로 받아야함.

        paymentBtn = (Button) findViewById(R.id.button1);
        cancelBtn = (Button) findViewById(R.id.button2);
/*
        menuBtn=(Button)findViewById(R.id.button3);
*/

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(QrActivity.this, ResultActivity.class);
                startActivity(intent);//화면전환
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(QrActivity.this, ScannerActivity.class);
                startActivity(intent2);//화면전환
            }
        });
/*        menuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent3 = new Intent(scanQR.this, Result.class);
                startActivity(intent3);//화면전환
            }
        });*/
    }

}
