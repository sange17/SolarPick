package com.App.SolarPing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;


public class intro extends AppCompatActivity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    citiesDatabase cities_db;
    GraphDatabase graph_db;
    Thread DataThread = new Thread(){
        @Override
        public void run() {
            super.run();
            cities_db = new citiesDatabase(getApplicationContext());
            graph_db = new GraphDatabase(getApplicationContext());
            cities_db.open();
            graph_db.open();
            cities_db.create();
            graph_db.create();
            getDataFromFirebase();
            getValuesOfRegions();

        }
    };
    Thread IntroThread = new Thread(){
        @Override
        public void run() {
            super.run();

            try {
                Thread.sleep(5000);
                Intent intent = new Intent(intro.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
            catch (Exception ignored){}
        }
    };

    Thread Loading = new Thread(){
        @Override
        public void run() {
            super.run();
            Intent intent = new Intent(intro.this,Loading.class);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //글씨 fading
        setContentView(R.layout.activity_intro);
        TextView t1 = findViewById(R.id.title);
        TextView t2 = findViewById(R.id.t2);
        TextView t3= findViewById(R.id.t3);
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.alpha);
        t1.startAnimation(translate);
        t2.startAnimation(translate);
        t3.startAnimation(translate);


        // 인트로 로딩 회전
        TextView introLoader= findViewById(R.id.introload);
        Drawable introloader = introLoader.getBackground();
        introloader.setAlpha(80);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        introLoader.startAnimation(rotate);

        DataThread.start();
        IntroThread.start();

//        Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 1) {
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        };
//        IntroThread t = new IntroThread(handler);
//        t.start();

    }



    public void getDataFromFirebase(){
        for (int i = 1; i <= 154; i++) {
            String count = String.format("%s", i);
            DatabaseReference myRef = database.getReference(count);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Object value = snapshot.getValue(Object.class);
                    // 문장 양끝 중괄호 제거하는 코드
                    String local_string = String.valueOf(value).substring(1, String.valueOf(value).length() - 1);

                    // 지역 정보 전체 가져오기
                    assert value != null;
                    String[] locations_information = local_string.split(", ");

                    // 해쉬맵 객체 생성
                    HashMap<String, String> hashMap = new HashMap<>();
                    // 해쉬맵에 값 저장
                    for (String sentence : locations_information) {
                        String[] stringArr = sentence.split("=");
                        hashMap.put(stringArr[0], stringArr[1]);
                    }

                    double D_latitude = Double.parseDouble(Objects.requireNonNull(hashMap.get("위도")));
                    double D_longitude = Double.parseDouble(Objects.requireNonNull(hashMap.get("경도")));
                    double D_sunshineduration = Double.parseDouble(Objects.requireNonNull(hashMap.get("시군구일사량평균")));
                    double D_stationNum = Double.parseDouble(Objects.requireNonNull(hashMap.get("발전소수량")));
                    double D_hazard = Double.parseDouble(hashMap.get("산사태취약지역(개수)") + hashMap.get("산사태취약지역(고속도로 근처)"));
                    double D_architecture = Double.parseDouble(Objects.requireNonNull(hashMap.get("시군구별 건축허가현황")));
                    String city_do = hashMap.get("도*광역시");
                    String city_sigungu = hashMap.get("시군구");
                    String anotherInfo = D_latitude + " " + D_longitude + " " + D_sunshineduration + " " + D_stationNum + " " + D_hazard + " " + D_architecture;

                    Cursor cities_cursor = cities_db.selectWhatIWant(city_do, city_sigungu);
                    if (cities_cursor.getCount() == 0) {
                        cities_db.insertColumn(city_do, city_sigungu,anotherInfo);
                    } else {
                        cities_db.UpdateAll(city_do, city_sigungu,anotherInfo);
                    }


                    // 년도와 값이있는 데이터 들만 해쉬에 저장
                    HashMap<String, String> tmpHash = new HashMap<>();
                    for (String key : hashMap.keySet()) {
                        String val = hashMap.get(key);
                        if (!key.equals("시군구") && !key.equals("도*광역시") && !key.equals("시군구일사량평균") && !key.equals("경도") && !key.equals("위도")
                                && !key.equals("grade") && !key.equals("시군구별 건축허가현황") && !key.equals("발전소수량") && !key.equals("산사태취약지역(고속도로 근처)")
                                && !key.equals("산사태취약지역(개수)")) {
                            tmpHash.put(key, val);
                        }

                    }


                    //그래프에 관한 데이터 저장

                    Cursor graph_cursor = graph_db.selectedWhatIWant(city_do + " " + city_sigungu);
                    if (graph_cursor.getCount() == 0) {
                        for (int j = 2015; j < 2015 + tmpHash.size() / 2; j++) {
                            graph_db.insertColumn(city_do + " " + city_sigungu, String.valueOf(j), tmpHash.get(String.valueOf(j)), String.valueOf(j), tmpHash.get(j + "01(전년누계대비 증감률)"));
                        }
                    } else {
                        for (int j = 2015; j < 2015 + tmpHash.size() / 2; j++) {
                            graph_db.UpdateAll(city_do + " " + city_sigungu, String.valueOf(j), tmpHash.get(String.valueOf(j)), String.valueOf(j), tmpHash.get(j + "01(전년누계대비 증감률)"));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }
    }

    public void getValuesOfRegions() {
        generatingDatabase generatingDatabase = new generatingDatabase(getApplicationContext());
        generatingDatabase.open();
        generatingDatabase.create();
        final FirebaseDatabase[] database = {FirebaseDatabase.getInstance()};

        for(int i=1;i<=44;i++) {
            DatabaseReference myRef = database[0].getReference("발전량-"+String.valueOf(i));

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Object value = snapshot.getValue();
                    System.out.println("generating amount = ");
                    String a = String.valueOf(value).substring(1, String.valueOf(value).length() - 1);
                    String[] splitedByEqual = a.split("=");


                    for(int i=0;i<splitedByEqual.length;i++){
                        if(i == 1){
                            splitedByEqual[i] = splitedByEqual[i].substring(1,splitedByEqual[i].length() - 5);
                        }
                    }

                    Cursor cursor = generatingDatabase.selectWhatIWant(splitedByEqual[2]);
                    if (cursor.getCount() == 0) {
                        generatingDatabase.insertColumn(splitedByEqual[2],splitedByEqual[1]);
                    } else {
                        generatingDatabase.UpdateColumn(splitedByEqual[2],splitedByEqual[1]);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}