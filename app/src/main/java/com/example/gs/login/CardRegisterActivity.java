package com.example.gs.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gs.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CardRegisterActivity extends AppCompatActivity {

    private static final String TAG = "CardRegisterActivity";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_register);

        findViewById(R.id.cardBtn).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cardBtn:
                    cardRegister();
                    break;
            }
        }
    };

    private void cardRegister() {
        final String cardNum = ((EditText) findViewById(R.id.cardNum)).getText().toString();
        final String mmYy = ((EditText) findViewById(R.id.mmyy)).getText().toString();
        final String cardPass = ((EditText) findViewById(R.id.cardPw)).getText().toString();
        final String birDate = ((EditText) findViewById(R.id.birth)).getText().toString();
        final Number gsPay = 10000;
        final String uId;

        if (cardNum.length() > 15 && mmYy.length() > 3 && cardPass.length() > 1 && birDate.length() > 7) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            uId=user.getUid();
            CardInfo cardInfo = new CardInfo(cardNum, mmYy, cardPass, birDate, gsPay,uId);

            if (user != null) {
                db.collection("users").document(user.getUid()).set(cardInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(CardRegisterActivity.this, LoginActivity.class);
                                startToast("카드 등록에 성공하였습니다.");
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startToast("카드 등록 실패");
                            }
                        });
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CardRegisterActivity.this);
            dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
            dialog.show();
        }

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
