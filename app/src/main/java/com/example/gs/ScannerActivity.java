
package com.example.gs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);//XML 디자인 파일 불러오기

        qrScan = new IntentIntegrator(this);
        qrScan.setCaptureActivity(CaptureFormActivity.class);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.setPrompt("QR코드에 묻은 먼지 닦고 사용해주세요");
        qrScan.initiateScan();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(ScannerActivity.this, QrActivity.class);//여기서 scanQR로 이동
                intent.putExtra("qrcode",result.getContents());//name은 받기위한 tag정도일 뿐 중요치x, 전달데이터

                startActivity(intent);//이제 이동
                // todo
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
