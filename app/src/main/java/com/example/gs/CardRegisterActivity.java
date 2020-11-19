package com.example.gs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardRegisterActivity extends AppCompatActivity {

    private static final String TAG = "CardRegisterActivity";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_register);

        findViewById(R.id.CardRegister_button).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.CardRegister_button:
                    cardRegister();
                    break;
            }
        }
    };

    private void cardRegister() {
        String CardNum = ((EditText) findViewById(R.id.Card_Num)).getText().toString();
        String MmYy = ((EditText) findViewById(R.id.Mm_Yy)).getText().toString();
        String CardPass = ((EditText) findViewById(R.id.Card_Pass)).getText().toString();
        String BirDate = ((EditText) findViewById(R.id.Bir_Date)).getText().toString();

        if (CardNum.length() > 15 && MmYy.length() > 3 && CardPass.length() > 1 && BirDate.length() > 7) {

            final CardInfo cardInfo = new CardInfo(CardNum, MmYy, CardPass, BirDate);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference userbaseReference = FirebaseDatabase.getInstance().getReference();
            String userId = user.getUid();
            DatabaseReference adduserinfoReference = userbaseReference.child("userinfo").child(userId);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String CardNum = snapshot.child("cardNum").getValue(String.class);
                    String MmYy = snapshot.child("mmyy").getValue(String.class);
                    String CardPass = snapshot.child("cardPass").getValue(String.class);
                    String BirDate = snapshot.child("birDate").getValue(String.class);

                    Intent intent = new Intent(CardRegisterActivity.this, LoginActivity.class);
                    startToast("카드 등록에 성공하였습니다.");
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    startToast("카드 등록 실패");
                }
            };
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

