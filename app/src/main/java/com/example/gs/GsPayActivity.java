package com.example.gs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class GsPayActivity extends AppCompatActivity {

    private TextView payview;
    private Button add;
    private Button card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gspay);

        payview = (TextView) findViewById(R.id.gspayvalue);
        add = (Button) findViewById(R.id.add);
        card = (Button) findViewById(R.id.card);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String current = user.getUid();

        db.collection("users").whereEqualTo("uid", current)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                                        CardInfo cardInfo2 = new CardInfo(null, null, null, null, (Number) ds.getData().get("gsPay"), null);
                                                        payview.setText(cardInfo2.getGsPay().toString());
                                                        String value = ds.getString("cardNum");
                                                        if (value != "") {
                                                            card.setText(value);
                                                        } else {
                                                            card.setText("failed");
                                                        }

                                                        //    break;
                                                    }
                                                }
                                            }
        );

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.collection("users").whereEqualTo("uid", current)

                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            CardInfo cardInfo2 = new CardInfo((String) ds.getData().get("cardNum"), (String) ds.getData().get("mmYy"), (String) ds.getData().get("cardPass"), (String) ds.getData().get("birDate"), (Number) ds.getData().get("gsPay"), current);

                            String value = cardInfo2.getGsPay().toString();
                            int plus = 5000;
                            int result = plus + Integer.parseInt(value);

                            cardInfo2.setGsPay(result);

                            payview.setText(Integer.toString(result));

                            db.collection("users").document(current).set(cardInfo2)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startToast("성공");
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