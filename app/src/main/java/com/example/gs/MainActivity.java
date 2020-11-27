package com.example.gs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Gravity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class MainActivity extends AppCompatActivity implements NaverMap.OnMapClickListener, OnMapReadyCallback, Overlay.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static  final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private Context mContext;
    private ViewGroup mParent;

    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    private ListView listView;
    private ListViewAdapter menuAdapter;

    //마커 관련
    private InfoWindow infoWindow;
    private List<GsBin> gsBins = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private boolean isCameraAnimated = false;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = getApplicationContext();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        //파이어베이스 데이터베이스
        firebaseDatabase = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        databaseReference = firebaseDatabase.getReference("gsbin"); //DB 테이블 연결

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GsBin gsBin = snapshot.getValue(GsBin.class); //만들어둔 GsBin 객체에 데이터 담기
                    gsBins.add(gsBin);
                    updateMapMarkers(gsBins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비 가져오던 중 에러 발생
            }
        });

        //QR화면 연결
        Button qrBtn = (Button) findViewById(R.id.qrBtn);
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),QrActivity.class);
                startActivity(intent);
            }
        });

        //로그아웃
        ImageButton mBtn = (ImageButton) findViewById(R.id.menuImg);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.menuImg:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                }

            }
        });

        //리스트뷰 연결, 메뉴 등록
        listView = (ListView) findViewById(R.id.listView);
        //리스트뷰 관련
        ArrayList<String> arrayMenu = new ArrayList<>();
        arrayMenu.add("마이페이지");
        arrayMenu.add("이용안내");

        //리스트뷰 어댑터 연결
        menuAdapter = new ListViewAdapter(mContext, arrayMenu);
        listView.setAdapter(menuAdapter);

        //리스트뷰 선택시 액티비티 전환
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent1 = new Intent(getApplicationContext(), GsPayActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        break;
                    default:
                }
            }
        });

        //메인 화면 지도
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.mapView, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


    }
    /*private void startSignUpActivity()
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }*/


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        naverMap.setOnMapClickListener((NaverMap.OnMapClickListener) this);

        //마커 정보창
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this) {
            @NonNull
            @Override
            protected View getContentView(@NonNull InfoWindow infoWindow) {
                Marker marker = infoWindow.getMarker();
                GsBin gsbin = (GsBin) marker.getTag();
                View view = View.inflate(MainActivity.this, R.layout.gs_point, null);
                ((TextView) view.findViewById(R.id.gsName)).setText("GS-" + gsbin.size);
                ((TextView) view.findViewById(R.id.gsAvailable)).setText(gsbin.capacity + "g");
                ((TextView) view.findViewById(R.id.gsId)).setText("#" + gsbin.id);
                return view;
            }
        });

        //지도상 줌, 나침반, 로고 등 설정
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false); //현위치 버튼
        uiSettings.setScrollGesturesEnabled(true); //스크롤
        uiSettings.setZoomGesturesEnabled(true); //줌 확대
        uiSettings.setLogoGravity(Gravity.RIGHT | Gravity.TOP);
        uiSettings.setLogoMargin(0, 20, 15, 0);

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

    private void updateMapMarkers(List<GsBin> gsBins) {
        resetMarkerList();
        for (GsBin gsbin : this.gsBins) {
            Marker marker = new Marker();
            marker.setTag(gsbin);
            marker.setPosition(new LatLng(gsbin.lat, gsbin.lng));
            if (30000 <= (gsbin.capacity)) {
                marker.setIcon(OverlayImage.fromResource(R.drawable.greengs));
                marker.setWidth(150);
                marker.setHeight(150);
            } else if (15000 <= (gsbin.capacity)) {
                marker.setIcon(OverlayImage.fromResource(R.drawable.yellowgs));
                marker.setWidth(150);
                marker.setHeight(150);
            } else {
                marker.setIcon(OverlayImage.fromResource(R.drawable.redgs));
            }
            marker.setAnchor(new PointF(0.5f, 1.0f));
            marker.setMap(naverMap);
            marker.setOnClickListener(this);
            markerList.add(marker);
        }
    }

    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        if (infoWindow.getMarker() != null) {
            infoWindow.close();
        }
    }

    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if (overlay instanceof Marker) {
            Marker marker = (Marker) overlay;
            if (marker.getInfoWindow() != null) {
                infoWindow.close();
            } else {
                infoWindow.open(marker);
            }
            return true;
        }
        return false;
    }

    private void resetMarkerList() {
        if (markerList != null && markerList.size() > 0) {
            for (Marker marker : markerList) {
                marker.setMap(null);
            }
            markerList.clear();
        }
    }

//    private void init() {
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (firebaseUser == null) {
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
//        } else {
//            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
//            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document != null) {
//                            if (document.exists()) {
//                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                            } else {
//                                Log.d(TAG, "No such document");
//                                myStartActivity(CardRegisterActivity.class);
//                            }
//                        }
//                    } else {
//                        Log.d(TAG, "get failed with ", task.getException());
//                    }
//                }
//            });
//        }
//    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}