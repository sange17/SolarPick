package com.App.SolarPing;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.FileSystemLoopException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GraphActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager viewPager;
    TextView province;
    ImageButton restart;
    ImageButton back;
    String getValueFromOtherClass;
    String response;
    GraphDatabase graphDatabase;
    ArrayList<ArrayList<String>> arrayLists;
    ArrayList<String> arrayList;
    ArrayList<String> rising_point_of_landslide_comparingTo_last_year;
    ArrayList<String> number_of_generator_for_each_years;
    ArrayList<String> generating_amount;

    Thread loading = new Thread() {
        @Override
        public void run() {
            try {
                Intent loading = new Intent(GraphActivity.this, Loading.class);
                loading.putExtra("activity", "graph");
                startActivity(loading);
            } catch (Exception ignored) {
            }
        }
    };
    generatingDatabase generatingDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //데이터 베이스 열고 생성
        generatingDatas = new generatingDatabase(getApplicationContext());
        generatingDatas.open();
        generatingDatas.create();

        //데이터를 fragment로 보낼 ArrayList 생성
        rising_point_of_landslide_comparingTo_last_year = new ArrayList<>();
        number_of_generator_for_each_years = new ArrayList<>();
        generating_amount = new ArrayList<>();

        //메인화면에서 Intent를 통해서 지역 값 받아오기
        Intent intent1 = getIntent();
        getValueFromOtherClass = intent1.getStringExtra("region");

        //선택된 지역의 시군구 전력 데이터 찾기
        String generatingValues;
        String[] splitGeneratingValues;

        Cursor generatingDatasCursor = generatingDatas.selectWhatIWant(getValueFromOtherClass);
        if(generatingDatasCursor.getCount() > 0){
            generatingDatasCursor.moveToPosition(0);
            generatingValues = generatingDatasCursor.getString(2);
            splitGeneratingValues = generatingValues.split(", ");
            generating_amount.addAll(Arrays.asList(splitGeneratingValues));
        }


        //컴포넌트들 엑티비티에서 받기
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        province = findViewById(R.id.province);
        restart = findViewById(R.id.restart);
        back = findViewById(R.id.back);

        //큰타이틀 설정
        province.setText(getValueFromOtherClass);

        //그래프 데이터베이스 열기
        graphDatabase = new GraphDatabase(getApplicationContext());
        graphDatabase.open();





        //그래프에 관한 데이터 베이스 열기
        Cursor GraphCursor = graphDatabase.selectedWhatIWant(getValueFromOtherClass);
        //그래프에 관한 데이터 베이스를 찾고 위에서 생성한 Arraylist에 저장
        if (GraphCursor.getCount() != 0) {
            for (int i = 0; i < GraphCursor.getCount(); i++) {
                GraphCursor.moveToPosition(i);
                number_of_generator_for_each_years.add(GraphCursor.getString(3));
                rising_point_of_landslide_comparingTo_last_year.add(GraphCursor.getString(5));
            }
        }


        //슬라이드 탭의 이름을 설정하기 위한 Arraylist생성후 탭 이름 넣기
        arrayList = new ArrayList<>();
        arrayList.add("발전소 수량");
        arrayList.add("산사태 증감");
        arrayList.add("발전량");


        System.out.println("잘지냈어요 여기봐요 = ");




        //ArrayList에 ArrayList<String>들 넣기
        arrayLists = new ArrayList<>();
        arrayLists.add(number_of_generator_for_each_years);
        arrayLists.add(rising_point_of_landslide_comparingTo_last_year);
        arrayLists.add(generating_amount);


        //fragment들로 데이터 넘겨주기
        prepareViewPager(viewPager, arrayList, arrayLists);


        //fragment 완전 set
        tabLayout.setupWithViewPager(viewPager);

        //뒤로가기 버튼 리스너
        back.setOnClickListener(view -> {
            if (loading.getState() != Thread.State.NEW) {
                loading.interrupt();
            }
            loading.start();
            finish();
        });

        //다시시작 리스너
        restart.setOnClickListener(view -> {
            if (loading.getState() != Thread.State.NEW) {
                loading.interrupt();
            }
            loading.start();
            Intent intent = new Intent(GraphActivity.this, GraphActivity.class);
            intent.putExtra("region", getValueFromOtherClass);
            startActivity(intent);
            finish();
        });


        //전화면으로 데이터 response 0를 받았을 경우 재시작
        prepareViewPager(viewPager, arrayList, arrayLists);


    }


    // 다음 fragment로 데이터 넘겨주기
    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList, ArrayList<ArrayList<String>> arrayLists) {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        MainFragment fragment = new MainFragment();


        String[] names_of_charts = new String[3];
        names_of_charts[0] = "number_of_generator_for_each_years";
        names_of_charts[1] = "rising_point_of_landslide_comparingTo_last_year";
        names_of_charts[2] = "generating_amount";

        for (int i = 0; i < arrayList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(names_of_charts[i], arrayLists.get(i));
            fragment.setArguments(bundle);

            adapter.addFragment(fragment, arrayList.get(i));

            fragment = new MainFragment();
        }
        viewPager.setAdapter(adapter);


    }


    //fragment를 위한 어댑터
    private static class MainAdapter extends FragmentPagerAdapter {

        ArrayList<String> arrayList = new ArrayList<>();

        List<Fragment> fragmentList = new ArrayList<>();


        public void addFragment(Fragment fragment, String title) {
            arrayList.add(title);
            fragmentList.add(fragment);

        }

        public MainAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayList.get(position);
        }
    }


}









