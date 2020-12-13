package com.example.gs.qr;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

import com.example.gs.R;
import com.journeyapps.barcodescanner.CaptureActivity;

public class CaptureFormActivity extends CaptureActivity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                100,260*5);


        //TextView를 설정하고 this.addContentView
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.parseColor("#ABCDEF"));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        //imageView를 설정하고 this.addContentView
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);

        imageView.setImageDrawable(getResources().getDrawable(R.drawable.trashcan));

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        float width = (float)(size.x)/2;
        float height = (float)(size.y)/8;

        Matrix matrix=new Matrix();
        matrix.set(imageView.getImageMatrix());
        matrix.postScale(0.25f,0.25f,(float)((float)width-(float)576*0.09),(float)((float)height-(float)720*0.09));//화면크기 받아와서 조절
        imageView.setImageMatrix(matrix);


        //this.addContentView
        this.addContentView(textView, params);
        this.addContentView(imageView,params);
    }
}