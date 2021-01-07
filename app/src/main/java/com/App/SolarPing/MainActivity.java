package com.App.SolarPing;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListPopupWindow;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;         // 구글맵 뷰에 필요한 변수
    SupportMapFragment mapFragment;         // 구글맵 뷰에 필요한 변수
    private SearchView searchView;      // 구글맵 뷰 위의 검색창에 필요한 변수
    List<Marker> AllMarkers = new ArrayList<Marker>();      // 구글맵 마커 저장을 위한 리스트 변수

    citiesDatabase cities_db;
    GraphDatabase graph_db;


    // 플로팅 버튼에 필요한 변수
    private Context mContext;
    private FloatingActionButton add_btn, ranking_btn, feedback_btn;
    private TextView ranking_text, feedback_text;
    private Animation rotate_open_anim, rotate_close_anim, from_bottom_anim, to_bottom_anim;
    private boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.sv_location);

        /* 플로팅 버튼 관련 */
        mContext = getApplicationContext();
        rotate_open_anim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_open_anim);
        rotate_close_anim = AnimationUtils.loadAnimation(mContext, R.anim.roatate_close_anim);

        add_btn = (FloatingActionButton) findViewById(R.id.add_btn);
        ranking_btn = (FloatingActionButton) findViewById(R.id.ranking_btn);
        feedback_btn = (FloatingActionButton) findViewById(R.id.feedback_btn);
        ranking_text = (TextView) findViewById(R.id.ranking_text);
        feedback_text = (TextView) findViewById(R.id.feedback_text);
        /* 플로팅 버튼 관련 */

        cities_db = new citiesDatabase(getApplicationContext());
        graph_db = new GraphDatabase(getApplicationContext());
        cities_db.open();
        graph_db.open();

        Button btnallremove = (Button) findViewById(R.id.btn_allremove);

        try {
            // 맵을 가져옵니다
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    String location = searchView.getQuery().toString();
//                    searchView.setIconified(false);     // 실행 시 자동으로 검색창 켜지는 것 방지
                    List<Address> addressList = null;

                    if (location != null || !location.equals("")) {
                        Geocoder geocoder = new Geocoder(MainActivity.this);
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                            searchView.clearFocus();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng((address.getLatitude()), address.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "지역이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            mapFragment.getMapAsync(this);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception ignored) {
        }

        /* 플로팅 버튼 관련 */
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFab();
            }
        });

        ranking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RankRecommend.class);
                startActivity(intent);
                toggleFab();
            }
        });

        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondScreen.class);
                startActivity(intent);
                toggleFab();
            }
        });

        /* 플로팅 버튼 관련 */

        // Spinner 등금 콤보박스 관련 코드
        Spinner gradeSpinner = (Spinner) findViewById(R.id.spinner_grade);
        ArrayAdapter gradeAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_grade, android.R.layout.simple_spinner_item);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);

        // Spinner 지역 콤보박스 관련 코드
        Spinner locationSpinner = (Spinner) findViewById(R.id.spinner_location);
        ArrayAdapter locationAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_location, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String grade;
                grade = adapterView.getItemAtPosition(position).toString();     // 콤보박스 문자열 가져오기
                removeAllMarkers();
                createMarker(grade);
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String location;
                location = adapterView.getItemAtPosition(position).toString();
                try {
                    Field popup = Spinner.class.getDeclaredField("mPopup");
                    popup.setAccessible(true);

                    ListPopupWindow window = (ListPopupWindow) popup.get(locationSpinner);
                    window.setHeight(1150);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                removeAllMarkers();
                createMarker(location);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnallremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createMarker("지도 초기화");
                gradeSpinner.setSelection(0);
                locationSpinner.setSelection(0);
            }
        });
    }

    GoogleMap.OnInfoWindowClickListener markerClickListener = marker -> {
        String markerId = marker.getTitle();
        //선택한 타겟위치
        LatLng location = marker.getPosition();
        Toast.makeText(MainActivity.this, "마커 클릭 Marker ID : " + markerId + "(" + location.latitude + " " + location.longitude + ")", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, GraphActivity.class);
        intent.putExtra("region", markerId);
        intent.putExtra("response", "0");
        startActivity(intent);
    };

    // 맵에 대한 작업을 세우는 곳(마커같은 기능)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setPadding(0, 400, 0, 0);            // 나침반 위치를 패딩으로 옮깁니다.
        mMap.getUiSettings().setMapToolbarEnabled(false);       // 구글맵 툴바를 없앱니다.
        this.mMap.setOnInfoWindowClickListener(markerClickListener);
//        Button btnall = (Button) findViewById(R.id.btn_allcreate);
//        Button btnseoul = (Button) findViewById(R.id.btn_seoul);
//        Button btndaejeon = (Button) findViewById(R.id.btn_deajeon);
//        Button btngwangju = (Button) findViewById(R.id.btn_gwangju);
//        Button btnbusan = (Button) findViewById(R.id.btn_busan);
//        Button btnincheon = (Button) findViewById(R.id.btn_incheon);
//        Button btngyeonggi = (Button) findViewById(R.id.btn_gyeonggi);
//        Button btngangwon = (Button) findViewById(R.id.btn_gangwon);
//        Button btnchungnam = (Button) findViewById(R.id.btn_chungnam);
//        Button btnchungbuk = (Button) findViewById(R.id.btn_chungbuk);
//        Button btnjeonnam = (Button) findViewById(R.id.btn_jeonnam);
//        Button btnjeonbuk = (Button) findViewById(R.id.btn_jeonbuk);
//        Button btngyeongnam = (Button) findViewById(R.id.btn_gyeongnam);
//        Button btngyeongbuk = (Button) findViewById(R.id.btn_gyeongbuk);

        // 지도상 대한민국 중심, 지도 위 경도 기반 위치 가져오기(무주군 위치)
        LatLng center = new LatLng(35.965500, 127.885751);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동

        Thread loading = new Thread() {
            @Override
            public void run() {
                super.run();
                Intent intent = new Intent(MainActivity.this, Loading.class);
                intent.putExtra("activity", "main");
                startActivity(intent);
            }
        };

        // 마커 모두 보기 버튼입니다. 지금은 없지요.
        //
        /*// 마커 모두 보기
        btnall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loading.getState() != Thread.State.NEW) {
                    loading.interrupt();
                }
                loading.start();
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("모두 보기");
                // 지도상 대한민국 중심, 지도 위 경도 기반 위치 가져오기(괴산으로 초기화 되있는 상태)
                LatLng center = new LatLng(36.8367, 127.875);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.5));        // 해당 지역으로 카메라 이동
                loading.interrupt();

            }
        });*/

        // 검색창 및 지역 태그 버튼들 입니다. 지금은 없지요.
        /*// 서울특별시 마커 모두 보기
        btnseoul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("서울특별시");
                // 지도상 서울특별시 중심, 지도 위 경도 기반 위치 가져오기(서울특별시 마포구로 초기화 되있는 상태)
                LatLng center = new LatLng(37.5415, 126.9499);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동
            }
        });

        // 대전광역시 마커 모두 보기
        btndaejeon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("대전광역시");
                // 지도상 대전광역시 중심, 지도 위 경도 기반 위치 가져오기(대전광역시 중구로 초기화 되있는 상태)
                LatLng center = new LatLng(36.315, 127.3953);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동
            }
        });

        // 광주광역시 마커 모두 보기
        btngwangju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("광주광역시");
                // 지도상 광주광역시 중심, 지도 위 경도 기반 위치 가져오기(광주광역시 서구로 초기화 되있는 상태)
                LatLng center = new LatLng(35.152, 126.8903);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동
            }
        });

        // 부산광역시 마커 모두 보기
        btnbusan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("부산광역시");
                // 지도상 부산광역시 중심, 지도 위 경도 기반 위치 가져오기(부산광역시 연제구로 초기화 되있는 상태)
                LatLng center = new LatLng(35.1845, 129.0761);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동
            }
        });

        // 인천광역시 마커 모두 보기
        btnincheon.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("인천광역시");
                // 지도상 인천광역시 중심, 지도 위 경도 기반 위치 가져오기(인천광역시 중구로 초기화 되있는 상태)
                LatLng center = new LatLng(37.4404, 126.4613);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동
            }
        });

        // 경기도 마커 모두 보기
        btngyeonggi.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("경기도");
                // 지도상 경기도 중심, 지도 위 경도 기반 위치 가져오기(경기도 구리시로 초기화 되있는 상태)
                LatLng center = new LatLng(37.5964, 127.1492);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동
            }
        });

        // 강원도 마커 모두 보기
        btngangwon.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("강원도");
                // 지도상 강원도 중심, 지도 위 경도 기반 위치 가져오기(강원도 평창군으로 초기화 되있는 상태)
                LatLng center = new LatLng(37.6736, 128.7061);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동
            }
        });

        // 충청남도 마커 모두 보기
        btnchungnam.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("충청남도");
                // 지도상 충청남도 중심, 지도 위 경도 기반 위치 가져오기(충청남도 아산시로 초기화 되있는 상태)
                LatLng center = new LatLng(36.7575, 126.8774);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동
            }
        });

        // 충청북도 마커 모두 보기
        btnchungbuk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("충청북도");
                // 지도상 충청북도 중심, 지도 위 경도 기반 위치 가져오기(충청북도 청주시로 초기화 되있는 상태)
                LatLng center = new LatLng(36.5536, 127.5488);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동
            }
        });

        // 전라남도 마커 모두 보기
        btnjeonnam.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("전라남도");
                // 지도상 전라남도 중심, 지도 위 경도 기반 위치 가져오기(전라남도 보성군으로 초기화 되있는 상태)
                LatLng center = new LatLng(34.8295, 127.1518);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동
            }
        });

        // 전라북도 마커 모두 보기
        btnjeonbuk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("전라북도");
                // 지도상 전라북도 중심, 지도 위 경도 기반 위치 가져오기(전라북도 전주시로 초기화 되있는 상태)
                LatLng center = new LatLng(35.8386, 127.1431);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동
            }
        });

        // 경상남도 마커 모두 보기
        btngyeongnam.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("경상남도");
                // 지도상 경상남도 중심, 지도 위 경도 기반 위치 가져오기(경상남도 창원시로 초기화 되있는 상태)
                LatLng center = new LatLng(35.1904, 128.5624);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동
            }
        });

        // 경상북도 마커 모두 보기
        btngyeongbuk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllMarkers();     // 맵 위의 모든 마커를 삭제합니다.
                createMarker("경상북도");
                // 지도상 경상북도 중심, 지도 위 경도 기반 위치 가져오기(경상북도 청송군으로 초기화 되있는 상태)
                LatLng center = new LatLng(36.3446, 129.0634);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동
            }
        });*/
    }

    // 검색창 밑 도,광역시 버튼 클릭 시 String location 매개변수로 구분하여 마커를 찍는 메소드입니다.
    private void createMarker(String location) {
        Cursor selecttable = cities_db.selectColumns();
        for (int i = 0; i < selecttable.getCount(); i++) {
            selecttable.moveToPosition(i);
            String colums_do = selecttable.getString(1);
            String colums_sigungu = selecttable.getString(2);
            String colums_locationinfo = selecttable.getString(3);

            double D_latitude = Double.parseDouble(Objects.requireNonNull(colums_locationinfo.split(" ")[0]));
            double D_longitude = Double.parseDouble(Objects.requireNonNull(colums_locationinfo.split(" ")[1]));
            double D_sunshineduration = Double.parseDouble(Objects.requireNonNull(colums_locationinfo.split(" ")[2]));
            double D_stationNum = Double.parseDouble(Objects.requireNonNull(colums_locationinfo.split(" ")[3]));
            double D_hazard = Double.parseDouble(colums_locationinfo.split(" ")[4]);
            double D_architecture = Double.parseDouble(Objects.requireNonNull(colums_locationinfo.split(" ")[5]));
            String city_do = colums_do;
            String city_sigungu = colums_sigungu;

            // 등급 수식입니다. 계산된 값이 높을수록
            double grade = 55 - (((((10 - D_sunshineduration) * Math.log10(D_stationNum) + 1) / 100)
                    * (1 + Math.log10(1 + Math.pow(D_hazard, 2))) * Math.log10(D_architecture)) * 10);

            // 지도 위.경도 기반 위치 가져오기
            LatLng latLng = new LatLng(D_latitude, D_longitude);

            // 지역 마커 객체 생성, 위치, 제목, 더보기 내용 설정
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(city_do + " " + city_sigungu);
            markerOptions.snippet(D_latitude + ", " + D_longitude);


            // 등급마다 마커 표시를 하는 등급 콤보박스 번호들
            switch (location) {
                case "1등급":
                    // 대한민국 지도상 중심(무주군 위치)
                    LatLng center = new LatLng(35.965500, 127.885751);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동
                    for (int j = 0; j < 154; j++) {
                        // 초록색3 1등급
                        if (grade >= 0 && grade < 20.0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(120f));
                            // 구글맵에 설정된 마커 추가
                            Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                            AllMarkers.add(locationMarker);     // 배열에 마커 추가
                        } else break;
                    }
                    break;
                case "2등급":
                    // 대한민국 지도상 중심(무주군 위치)
                    center = new LatLng(35.965500, 127.885751);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동
                    for (int j = 0; j < 154; j++) {
                        // 초록색2 2등급
                        if (grade >= 20.0 && grade < 30.0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(100f));
                            // 구글맵에 설정된 마커 추가
                            Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                            AllMarkers.add(locationMarker);     // 배열에 마커 추가
                        } else break;
                    }
                    break;
                case "3등급":
                    // 대한민국 지도상 중심(무주군 위치)
                    center = new LatLng(35.965500, 127.885751);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동
                    for (int j = 0; j < 154; j++) {
                        // 초록색1 3등급
                        if (grade >= 30.0 && grade < 40.0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(80f));
                            // 구글맵에 설정된 마커 추가
                            Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                            AllMarkers.add(locationMarker);     // 배열에 마커 추가
                        } else break;
                    }
                    break;
                case "4등급":
                    // 대한민국 지도상 중심(무주군 위치)
                    center = new LatLng(35.965500, 127.885751);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동
                    for (int j = 0; j < 154; j++) {
                        // 주황색2 4등급
                        if (grade >= 40.0 && grade < 45.0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(50f));
                            // 구글맵에 설정된 마커 추가
                            Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                            AllMarkers.add(locationMarker);     // 배열에 마커 추가
                        } else break;
                    }
                    break;
                case "5등급":
                    // 대한민국 지도상 중심(무주군 위치)
                    center = new LatLng(35.965500, 127.885751);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동
                    for (int j = 0; j < 154; j++) {
                        // 주황색1 5등급
                        if (grade >= 45.0 && grade < 50.0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(20f));
                            // 구글맵에 설정된 마커 추가
                            Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                            AllMarkers.add(locationMarker);     // 배열에 마커 추가
                        } else break;
                    }
                    break;
                case "6등급":
                    // 대한민국 지도상 중심(무주군 위치)
                    center = new LatLng(35.965500, 127.885751);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동
                    for (int j = 0; j < 154; j++) {
                        // 빨간색3 6등급
                        if (grade >= 50.0 && grade < 52.0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(10f));
                            // 구글맵에 설정된 마커 추가
                            Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                            AllMarkers.add(locationMarker);     // 배열에 마커 추가
                        } else break;
                    }
                    break;
                case "7등급":
                    // 대한민국 지도상 중심(무주군 위치)
                    center = new LatLng(35.965500, 127.885751);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동
                    for (int j = 0; j < 154; j++) {
                        // 빨간색2 7등급
                        if (grade >= 52.0 && grade < 54.0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(5f));
                            // 구글맵에 설정된 마커 추가
                            Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                            AllMarkers.add(locationMarker);     // 배열에 마커 추가
                        } else break;
                    }
                    break;
                case "8등급":
                    // 대한민국 지도상 중심(무주군 위치)
                    center = new LatLng(35.965500, 127.885751);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동
                    for (int j = 0; j < 154; j++) {
                        // 빨간색1 8등급
                        if (grade >= 54.0 && grade < 56.0) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(0f));
                            // 구글맵에 설정된 마커 추가
                            Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                            AllMarkers.add(locationMarker);     // 배열에 마커 추가
                        } else break;
                    }
                    break;
            }


            // 마커 등급 색상 입히기~!(위에서부터 1~8등급)
            // 마커 모두 보기 버튼입니다. 지금은 없어요.
            /*if (location.equals("모두 보기")) {
                for (int j = 0; j < 154; j++) {
                    // 초록색3 1등급
                    if (grade >= 0 && grade < 20.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(120f));
                        // 초록색2 2등급
                    else if (grade >= 20.0 && grade < 30.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(100f));
                        // 초록색1 3등급
                    else if (grade >= 30.0 && grade < 40.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(80f));
                        // 주황색2 4등급
                    else if (grade >= 40.0 && grade < 45.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(50f));
                        // 주황색1 5등급
                    else if (grade >= 45.0 && grade < 50.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(20f));
                        // 빨간색3 6등급
                    else if (grade >= 50.0 && grade < 52.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(10f));
                        // 빨간색2 7등급
                    else if (grade >= 52.0 && grade < 54.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(5f));
                        // 빨간색1 8등급
                    else
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(0f));

                    // 구글맵에 설정된 마커 추가
                    Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                    AllMarkers.add(locationMarker);     // 배열에 마커 추가
                }
            }*/


            if (city_do.equals(location)) {
                switch (location) {
                    case "서울특별시": {
                        // 지도상 서울특별시 중심, 지도 위 경도 기반 위치 가져오기(서울특별시 마포구로 초기화 되있는 상태)
                        LatLng center = new LatLng(37.5415, 126.9499);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동

                        break;
                    }
                    case "대전광역시": {
                        // 지도상 대전광역시 중심, 지도 위 경도 기반 위치 가져오기(대전광역시 중구로 초기화 되있는 상태)
                        LatLng center = new LatLng(36.315, 127.3953);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동

                        break;
                    }
                    case "광주광역시": {
                        // 지도상 광주광역시 중심, 지도 위 경도 기반 위치 가져오기(광주광역시 서구로 초기화 되있는 상태)
                        LatLng center = new LatLng(35.152, 126.8903);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "부산광역시": {
                        // 지도상 부산광역시 중심, 지도 위 경도 기반 위치 가져오기(부산광역시 연제구로 초기화 되있는 상태)
                        LatLng center = new LatLng(35.1845, 129.0761);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "인천광역시": {
                        // 지도상 인천광역시 중심, 지도 위 경도 기반 위치 가져오기(인천광역시 중구로 초기화 되있는 상태)
                        LatLng center = new LatLng(37.4404, 126.4613);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 9));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "경기도": {
                        // 지도상 경기도 중심, 지도 위 경도 기반 위치 가져오기(경기도 구리시로 초기화 되있는 상태)
                        LatLng center = new LatLng(37.5964, 127.1492);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "강원도": {
                        // 지도상 강원도 중심, 지도 위 경도 기반 위치 가져오기(강원도 평창군으로 초기화 되있는 상태)
                        LatLng center = new LatLng(37.6736, 128.7061);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "충청남도": {
                        // 지도상 충청남도 중심, 지도 위 경도 기반 위치 가져오기(충청남도 아산시로 초기화 되있는 상태)
                        LatLng center = new LatLng(36.7575, 126.8774);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "중청북도": {
                        // 지도상 충청북도 중심, 지도 위 경도 기반 위치 가져오기(충청북도 청주시로 초기화 되있는 상태)
                        LatLng center = new LatLng(36.5536, 127.5488);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "전라남도": {
                        // 지도상 전라남도 중심, 지도 위 경도 기반 위치 가져오기(전라남도 보성군으로 초기화 되있는 상태)
                        LatLng center = new LatLng(34.8295, 127.1518);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "전라북도": {
                        // 지도상 전라북도 중심, 지도 위 경도 기반 위치 가져오기(전라북도 전주시로 초기화 되있는 상태)
                        LatLng center = new LatLng(35.8386, 127.1431);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "경상남도": {
                        // 지도상 경상남도 중심, 지도 위 경도 기반 위치 가져오기(경상남도 창원시로 초기화 되있는 상태)
                        LatLng center = new LatLng(35.1904, 128.5624);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동


                        break;
                    }
                    case "경상북도": {
                        // 지도상 경상북도 중심, 지도 위 경도 기반 위치 가져오기(경상북도 청송군으로 초기화 되있는 상태)
                        LatLng center = new LatLng(36.3446, 129.0634);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 8.0));        // 해당 지역으로 카메라 이동


                        break;
                    }
                }
                for (int j = 0; j < 154; j++) {
                    // 초록색3 1등급
                    if (grade >= 0 && grade < 20.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(120f));
                        // 초록색2 2등급
                    else if (grade >= 20.0 && grade < 30.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(100f));
                        // 초록색1 3등급
                    else if (grade >= 30.0 && grade < 40.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(80f));
                        // 주황색2 4등급
                    else if (grade >= 40.0 && grade < 45.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(50f));
                        // 주황색1 5등급
                    else if (grade >= 45.0 && grade < 50.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(20f));
                        // 빨간색3 6등급
                    else if (grade >= 50.0 && grade < 52.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(10f));
                        // 빨간색2 7등급
                    else if (grade >= 52.0 && grade < 54.0)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(5f));
                        // 빨간색1 8등급
                    else
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(0f));

                    // 구글맵에 설정된 마커 추가
                    Marker locationMarker = mMap.addMarker(markerOptions);      // 구글맵에 설정된 마커 추가
                    AllMarkers.add(locationMarker);     // 배열에 마커 추가
                }
            }

            if (location.equals("지도 초기화")) {
                removeAllMarkers();
                // 지도상 서울특별시 중심, 지도 위 경도 기반 위치 가져오기(영동군으로 초기화 되있는 상태)
                LatLng center = new LatLng(35.965500, 127.885751);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, (float) 6.9));        // 해당 지역으로 카메라 이동

            }
        }
    }

    private void toggleFab() {

        if (isFabOpen) {
            ranking_btn.startAnimation(rotate_close_anim);
            feedback_btn.startAnimation(rotate_close_anim);
            ranking_btn.setClickable(false);
            feedback_btn.setClickable(false);
            ranking_text.setVisibility(View.INVISIBLE);
            feedback_text.setVisibility(View.INVISIBLE);
            isFabOpen = false;
        } else {
            ranking_btn.startAnimation(rotate_open_anim);
            feedback_btn.startAnimation(rotate_open_anim);
            ranking_btn.setClickable(true);
            feedback_btn.setClickable(true);
            ranking_text.setVisibility(View.VISIBLE);
            feedback_text.setVisibility(View.VISIBLE);
            isFabOpen = true;
        }
    }

    // 지도 상의 마커를 모두 지웁니다.
    private void removeAllMarkers() {
        for (Marker locationMarker : AllMarkers) {
            locationMarker.remove();
        }
        AllMarkers.clear();
    }
}