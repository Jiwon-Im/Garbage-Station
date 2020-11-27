
package com.example.gs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    public static final int SEND_INFORMATION = 0;
    public static final int SEND_STOP = 1;
    private double num = -1;
    private int num2 = -1;

    int socketWeight;
    double flag;
    int flag2;

    double receivedWieght;

    String read;

    private Button paymentBtn;

    private Socket socket;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<GsBin> gsBins = new ArrayList<>();

    private String qrurl;
    private String ip = "192.168.1.5";  //samsung
    // private String ip = "10.0.2.2";  //pixel
    // private String ip = "192.168.222.1";  //hj
    // private String ip = "192.168.1.1";   //nr

    checkedBin chBin1 = new checkedBin();
    ConnectThread th;
    TextView msgTV, trashbinid;

    private int port = 9994;

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
          qrurl = intent.getExtras().getString("code");

      //  qrurl = "http://m.site.naver.com/0HELu";


        msgTV = (TextView) findViewById(R.id.gv);
        trashbinid = (TextView) findViewById(R.id.idvalue);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case SEND_INFORMATION:

                        receivedWieght = msg.arg1;
                        Log.d("flag 내부 ww", String.valueOf(receivedWieght));               //쓰레기무게

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

                                }
                                chBin1 = findGs(gsBins);
                                Log.d("flag 계산전 적재량 출력", String.valueOf(chBin1.capacity));//적재량
                                Log.d("flag 계산전 id 출력", String.valueOf(chBin1.key));//적재량
                                chBin1 = CalculGs(chBin1, receivedWieght);

                                Log.d("flag 계산후 적재량 출력", String.valueOf(chBin1.capacity));//적재량
                                Log.d("flag 계산후 id 출력", String.valueOf(chBin1.key));//적재량
                                databaseReference.child(chBin1.key).child("capacity").setValue(chBin1.capacity);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //디비 가져오던 중 에러 발생
                            }
                        });

                        break;
                    case SEND_STOP:
                        th.stopThread();
                        //  textView.setText("Thread가 중지됨.");
                        break;
                    default:
                        break;
                }
            }
        };


        //쓰레드 시작
        th = new ConnectThread(handler);

        th.start();


        //버튼->화면전환
        paymentBtn = (Button) findViewById(R.id.button1);

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(QrActivity.this, ResultActivity.class);
                startActivity(intent);//화면전환
            }
        });


    }

    //쓰레기통 QR코드 값으로 Gsbin 정보 얻기 ID
    private checkedBin findGs(List<GsBin> gsBins) {
        checkedBin chBin2 = new checkedBin();
        if (num < 0) {
            for (GsBin gsbin : this.gsBins) {
                if (qrurl.equalsIgnoreCase(gsbin.url)) {
                    num2 = gsbin.getGsId();
                    num = gsbin.getGsCapacity();
                    break;
                }
            }
            if (num > 0) {
                flag = num;             //현재 쓰레기통 적재량 찾았음.!
                flag2 = num2;
            }
        }

        chBin2.capacity = flag;
        chBin2.key = String.valueOf(flag2);
        return chBin2;
    }

    private checkedBin CalculGs(checkedBin checkBin, double weight) {
        checkBin.capacity = checkBin.capacity - weight;

        return checkBin;
    }

    //쓰레드
    class ConnectThread extends Thread {

        public static final int SEND_INFORMATION = 0;
        public static final int SEND_STOP = 1;
        boolean stopped = false;
        int i = 0;
        Handler mHandler;

        public ConnectThread(Handler handler) {
            stopped = false;
            mHandler = handler;
        }

        public void stopThread() {
            stopped = true;
        }

        public void run() {
            try {
                //소켓통신
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, port);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                read = input.readLine();

                socketWeight = Integer.parseInt(read);
                Log.d("flag 웨이틍ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ", String.valueOf(socketWeight));

                //소켓으로 받은 무게값 oncreate 핸들러로 전달
                Message message = mHandler.obtainMessage();
                message.what = SEND_INFORMATION;
                message.arg1 = socketWeight;
                message.obj = socketWeight;

                mHandler.sendMessage(message);

                //UI 전달
                mHandler.post(new msgUpdate(read));

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

    //UI 처리
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

    class checkedBin {
        public double capacity;
        public String key;
    }
}

