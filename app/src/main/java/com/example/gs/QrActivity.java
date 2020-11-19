package com.example.gs;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class QrActivity extends AppCompatActivity {

    private int num;
    private Button paymentBtn;
    private Button cancelBtn;

    private Handler mHandler;
    private Socket socket;

    private String code;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    TextView msgTV;
    TextView idid;
    private List<GsBin> gsBins = new ArrayList<>();

    private String ip = "192.168.237.1";
    //private String ip = "10.0.2.2";

    private int port = 9003;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_q_r);

        Intent intent = getIntent();
        code = intent.getExtras().getString("code");

        //파이어베이스 데이터베이스
        firebaseDatabase = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        databaseReference = firebaseDatabase.getReference("gsbin"); //DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                gsBins.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GsBin gsBin = snapshot.getValue(GsBin.class); //만들어둔 GsBin 객체에 데이터 담기
                    gsBins.add(gsBin);
                    findGs(gsBins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비 가져오던 중 에러 발생
            }
        });

        mHandler = new Handler();
        msgTV = (TextView) findViewById(R.id.gv);
        Toast.makeText(this, "wifi에 연결", Toast.LENGTH_SHORT).show();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (false == isConnected()) {
            Toast.makeText(this, "네트원크 x", Toast.LENGTH_SHORT).show();
        }
        switch (getNetworkType()) {
            case ConnectivityManager.TYPE_WIFI:

                ConnectThread th = new ConnectThread();
                th.start();
                break;

            case ConnectivityManager.TYPE_MOBILE:
                Toast.makeText(this, "모바일 네트워크", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }

        paymentBtn = (Button) findViewById(R.id.button1);
        cancelBtn = (Button) findViewById(R.id.button2);

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
        //  String str = Integer.toString(num);
        idid = (TextView) findViewById(R.id.idvalue);
        idid.setText(String.valueOf(num));
    }


    private void findGs(List<GsBin> gsBins) {
        for (GsBin gsbin : this.gsBins) {
            if (code.equalsIgnoreCase(gsbin.url)) {
                num = gsbin.getGsId();
                break;
            }
        }

    }


    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetWork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetWork != null && activeNetWork.isConnectedOrConnecting();

        return isConnected;

    }

    private int getNetworkType() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork.getType();


    }

    class ConnectThread extends Thread {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, port);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String read = input.readLine();
                mHandler.post(new msgUpdate("0"));

                Thread.sleep(2000);

                mHandler.post(new msgUpdate(read));

                socket.close();
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    class msgUpdate implements Runnable {
        private String msg;

        public msgUpdate(String str) {
            this.msg = str;
        }

        public void run() {
            msgTV.setText(null);
            msgTV.setText(msgTV.getText().toString() + msg + "    g" + "\n" + (2 * Integer.parseInt(msg)) + "   원");

        }
    }


}