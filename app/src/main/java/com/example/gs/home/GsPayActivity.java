package com.example.gs.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gs.R;
import com.example.gs.login.CardInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class GsPayActivity extends AppCompatActivity {

    private TextView payTextView;
    private Button cardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gspay);

        payTextView = (TextView) findViewById(R.id.gsPayValue);
        Button chargeBtn = (Button) findViewById(R.id.chargeBtn);
        cardBtn = (Button) findViewById(R.id.cardBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String current = user.getUid();

        db.collection("users").whereEqualTo("uid", current)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                                        CardInfo cardInfo = new CardInfo(null, null, null, null, (Number) ds.getData().get("gsPay"), null);
                                                        payTextView.setText(cardInfo.getGsPay().toString());
                                                        String value = ds.getString("cardNum");
                                                        if (value != "") {
                                                            cardBtn.setText(value);
                                                        } else {
                                                            cardBtn.setText("failed");
                                                        }
                                                        //    break;
                                                    }
                                                }
                                            }
        );

        chargeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.collection("users").whereEqualTo("uid", current)

                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            CardInfo cardInfo = new CardInfo((String) ds.getData().get("cardNum"), (String) ds.getData().get("mmYy"), (String) ds.getData().get("cardPass"), (String) ds.getData().get("birDate"), (Number) ds.getData().get("gsPay"), current);

                            String value = cardInfo.getGsPay().toString();
                            int plus = 5000;
                            int result = plus + Integer.parseInt(value);

                            cardInfo.setGsPay(result);

                            payTextView.setText(Integer.toString(result));

                            db.collection("users").document(current).set(cardInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startToast("충전되었습니다.");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            startToast("실패");
                                        }
                                    });
                        }
                    }
                });


            }
        });


    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}