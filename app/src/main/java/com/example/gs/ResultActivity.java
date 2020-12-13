package com.example.gs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    ArrayList<String> results = new ArrayList<String>();

    TextView weightTextView;
    TextView payTextView;
    TextView pointTextView;
    TextView idinfoTextView;

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
        payTextView.setText(results.get(1));
        idinfoTextView.setText("#0000" + results.get(2));
        pointTextView.setText(results.get(3));
    }
}