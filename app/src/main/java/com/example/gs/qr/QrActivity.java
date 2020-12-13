
package com.example.gs.qr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gs.home.GsBin;
import com.example.gs.R;
import com.example.gs.login.CardInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    private double tempCapacity = -1;
    private int tempId = -1;
    int socketWeight;
    double flag;
    int flag2;
    double receivedWeight;

    private Socket socket;
    String read;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<GsBin> gsBins = new ArrayList<>();
    ArrayList<String> results = new ArrayList<>();

    checkedBin targetGsbin = new checkedBin();
    ConnectThread th;
    TextView gramTextView, moneyTextView;

    private String qrUrl;
    private int port = 9999;
    private String ip = "192.168.1.5";

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
        setContentView(R.layout.activity_qr);
        Intent intent = getIntent();
        qrUrl = intent.getExtras().getString("code");

        gramTextView = (TextView) findViewById(R.id.gValue);
        moneyTextView = (TextView) findViewById(R.id.moneyValue);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case SEND_INFORMATION:
                        //핸들러 값 저장
                        receivedWeight = msg.arg1;

                        ////DATABASE GSBIN UPDATE
                        firebaseDatabase = FirebaseDatabase.getInstance();                      //파이어베이스 데이터베이스 연동
                        databaseReference = firebaseDatabase.getReference("gsbin");       //DB 테이블 연결
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                gsBins.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    GsBin gsBin = snapshot.getValue(GsBin.class); //만들어둔 GsBin 객체에 데이터 담기
                                    gsBins.add(gsBin);
                                }
                                targetGsbin = findGs(gsBins);
                                targetGsbin = CalculGs(targetGsbin, receivedWeight);

                                databaseReference.child(targetGsbin.key).child("capacity").setValue(targetGsbin.capacity);

                                //intent 정보
                                results.add(String.valueOf(receivedWeight));              //측정무게      - results(0)
                                results.add(String.valueOf(0.2 * receivedWeight));          //이용 요금     - results(1)
                                results.add(String.valueOf(targetGsbin.key));                  //gsbin number - results(2)
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //디비 가져오던 중 에러 발생
                            }
                        });

                        ////DATABASE USER GSPAY UPDATE
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final String current = user.getUid();

                        db.collection("users").whereEqualTo("uid", current)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                    CardInfo cardInfo = new CardInfo((String) ds.getData().get("cardNum"), (String) ds.getData().get("mmYy"), (String) ds.getData().get("cardPass"), (String) ds.getData().get("birDate"), (Number) ds.getData().get("gsPay"), current);

                                    String value = cardInfo.getGsPay().toString();
                                    Double minus = 5.0 + 2 * receivedWeight;

                                    int payResult = Integer.parseInt(value) - Integer.parseInt(String.valueOf(Math.round(minus)));

                                    cardInfo.setGsPay(payResult);

                                    //intent 정보
                                    results.add(String.valueOf(payResult));                 //GSPOINT    - results(3)

                                    db.collection("users").document(current).set(cardInfo);
                                }
                            }
                        });
                        break;
                    case SEND_STOP:
                        th.stopThread();
                        break;
                    default:
                        break;
                }
            }
        };

        //쓰레드 시작
        th = new ConnectThread(handler);
        th.start();

        //화면전환
        Button paymentBtn = (Button) findViewById(R.id.button1);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(QrActivity.this, ResultActivity.class);
                intent.putExtra("results", results);
                startActivity(intent);//화면전환
            }
        });
    }

    //쓰레기통 QR코드->Gsbin ID 얻기
    private checkedBin findGs(List<GsBin> gsBins) {
        checkedBin chBin = new checkedBin();
        if (tempCapacity < 0) {
            for (GsBin gsbin : this.gsBins) {
                if (qrUrl.equalsIgnoreCase(gsbin.url)) {
                    tempId = gsbin.getGsId();
                    tempCapacity = gsbin.getGsCapacity();
                    break;
                }
            }
            if (tempCapacity > 0) {
                flag = tempCapacity;             //현재 쓰레기통 적재량 찾았음.!
                flag2 = tempId;
            }
        }
        chBin.capacity = flag;
        chBin.key = String.valueOf(flag2);
        return chBin;
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

                read = input.readLine();                            //소켓 read
                socketWeight = Integer.parseInt(read);              //read 변환

                //소켓정보 핸들러 전달
                Message message = mHandler.obtainMessage();
                message.what = SEND_INFORMATION;
                message.arg1 = socketWeight;
                mHandler.sendMessage(message);

                //UI 전달
                mHandler.post(new msgUpdate(String.valueOf(0)));    //0g
                Thread.sleep(2000);
                mHandler.post(new msgUpdate(read));                 //150g

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
            gramTextView.setText(null);             //초기화
            moneyTextView.setText(null);

            gramTextView.setText(msg + " g");
            moneyTextView.setText(Math.round(5.0+0.2 * Integer.parseInt(msg)) + " 원");
        }
    }

    class checkedBin {
        public double capacity;
        public String key;
    }
}