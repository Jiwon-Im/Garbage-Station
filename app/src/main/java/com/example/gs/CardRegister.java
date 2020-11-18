package com.example.gs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CardRegister extends AppCompatActivity {

    private EditText Card_Num,Mm_Yy, Card_Pass,Bir_Date;
    private Button CardRegister_button;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_register);

        Card_Num = findViewById( R.id.Card_Num );
        Mm_Yy = findViewById( R.id.Mm_Yy );
        Card_Pass = findViewById( R.id.Card_Pass );
        Bir_Date = findViewById(R.id.Bir_Date);

        //등록하기 버튼 클릭 시 수행
        CardRegister_button = findViewById( R.id.CardRegister_button );
        CardRegister_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String CardNum = Card_Num.getText().toString();
                final String MmYy = Mm_Yy.getText().toString();
                final String CardPass = Card_Pass.getText().toString();
                final String BirDat = Bir_Date.getText().toString();

                //한 칸이라도 입력 안했을 경우
                if (CardNum.equals("") || MmYy.equals("") || CardPass.equals("") || BirDat.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardRegister.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            //카드등록 성공시
                            if (success) {
                                //Toast.makeText(getApplicationContext(), String.format("%s님 카드등록에 성공하였습니다.", UserName), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CardRegister.this, LoginActivity.class);
                                startActivity(intent);

                                //카드등록 실패시
                            } else {
                                Toast.makeText(getApplicationContext(), "카드등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                //서버로 Volley를 이용해서 요청
                CardRequest cardrequest = new CardRequest(CardNum, MmYy, CardPass,BirDat, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CardRegister.this);
                queue.add(cardrequest);

            }
        });

    }
}