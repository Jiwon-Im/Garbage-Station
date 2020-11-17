package com.example.gs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,Overlay.OnClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private Context mContext;
    private ViewGroup mParent;

    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    //리스트뷰 관련
    private ArrayList<String> arrayMenu;
    private ListView listView;
    private ListViewAdapter menuAdapter;

    //마커 관련
    private InfoWindow infoWindow;
    private List<Marker> markerList = new ArrayList<>();
    private boolean isCameraAnimated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = getApplicationContext();

        //QR화면 연결
        Button qrBtn = (Button) findViewById(R.id.qrBtn);
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),QrActivity.class);
                startActivity(intent);
            }
        });

        //로그인 화면 연결
        ImageButton mBtn = (ImageButton)findViewById(R.id.menuImg);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        //리스트뷰 연결, 메뉴 등록
        listView = (ListView) findViewById(R.id.listView);
        arrayMenu = new ArrayList<>();
        arrayMenu.add("마이페이지");
        arrayMenu.add("이용안내");

        //리스트뷰 어댑터 연결
        menuAdapter = new ListViewAdapter(mContext, arrayMenu);
        listView.setAdapter(menuAdapter);

        //리스트뷰 선택시 액티비티 전환
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent1 = new Intent(getApplicationContext(),GsPayActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        break;
                    default:
                }
            }
        });

        locationSource =
                new FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.mapView, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        //지도상 마커 표시
        Marker marker = new Marker();
        marker.setPosition(new LatLng(35.888213, 128.610913));
        marker.setMap(naverMap);

        marker.setWidth(100);
        marker.setHeight(100);
        marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
        marker.setOnClickListener(this);

        //마커 정보창
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this) {
            @NonNull
            @Override
            protected View getContentView(@NonNull InfoWindow infoWindow) {
                 Marker marker = infoWindow.getMarker();
                 GsBin gsbin = (GsBin) marker.getTag();
                View view = View.inflate(MainActivity.this, R.layout.gs_point, null);
//                ((TextView) view.findViewById(R.id.gsName)).setText(GsBin.name);
//                ((TextView) view.findViewById(R.id.gsAvailable)).setText(GsBin.capacity);
//                ((TextView) view.findViewById(R.id.gsId)).setText(GsBin.Id);

                return view;
            }
        });

        //지도상 줌, 나침반, 로고 등 설정
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false); //현위치 버튼
        uiSettings.setScrollGesturesEnabled(true); //스크롤
        uiSettings.setZoomGesturesEnabled(true); //줌 확대
        uiSettings.setLogoGravity(Gravity.RIGHT|Gravity.TOP);
        uiSettings.setLogoMargin(0,20,15,0);

        LocationButtonView locationButtonView = findViewById(R.id.location);
        locationButtonView.setMap(naverMap);

        ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // request code와 권한획득 여부 확인
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }
    }

//    @Override
//    public void onMapClick(){
//
//    }
    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if(overlay instanceof Marker){
            Marker marker = (Marker) overlay;
            if (marker.getInfoWindow() != null){
                infoWindow.close();
            } else {
                infoWindow.open(marker);
            }
            return true;
        }
        return false;
    }

//    private void resetMarkerList() {
//        if(markerList != null && markerList.size() >0){
//            for(Marker marker : markerList){
//                marker.setMap(null);
//            }
//            markerList.clear();
//        }
//    }

    private void updateMapMarkers(GsBinResult result){
//        resetMarkerList();
        if(result.gsbins != null && result.gsbins.size() > 0){
            for (GsBin gsbin : result.gsbins){
                Marker marker = new Marker();
                marker.setTag(gsbin );
                marker.setPosition(new LatLng(gsbin.lat, gsbin.lng));
                if(1 <=(gsbin.capacity)){
                    marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
                }else if(0.5 <= (gsbin.capacity)){
                    marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
                }else{
                    marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
                }
                marker.setAnchor(new PointF(0.5f,1.0f));
                marker.setMap(naverMap);
                marker.setOnClickListener(this);
                markerList.add(marker);
            }
        }
    }
}