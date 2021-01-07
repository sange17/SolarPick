package com.App.SolarPing;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;

import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;

public class RankRecommendSub extends AppCompatActivity {
    City[] cityArr = new City[154];
    citiesDatabase cityDatabase;

    private void QuicKSort(City[] arr, int left, int right) {
        if (left < right) {
            int q = Partition(arr, left, right);
            QuicKSort(arr, left, q - 1);
            QuicKSort(arr, q + 1, right);
        }
    }

    private int Partition(City[] arr, int left, int right) {
        int low = left;
        int high = right + 1;
        City pivot = arr[left];
        do {
            do {
                low++;
            } while (low <= right && pivot.grade > arr[low].grade);
            do {
                high--;
            } while (high >= left && pivot.grade < arr[high].grade);
            if (low < high) {
                City t = arr[low];
                arr[low] = arr[high];
                arr[high] = t;

            }
        } while (low < high);
        arr[left] = arr[high];
        arr[high] = pivot;
        return high;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_recommend_sub);
        cityDatabase = new citiesDatabase(getApplicationContext());
        cityDatabase.open();


        //인텐트에서 도 광역시 정보를 받음
        final Intent intent = getIntent();
        String province = intent.getExtras().getString("province");
        TextView t = findViewById(R.id.tbx);
        t.setText(province);


        Cursor values = cityDatabase.selectColumns();
        for (int i = 0; i < values.getCount(); i++) {
            values.moveToPosition(i);
            cityArr[i] = new City(values.getString(1), values.getString(2), values.getString(3));
        }

        for (int i = 0; i < values.getCount(); i++) {
            System.out.println(cityArr[i].province + " " + cityArr[i].name + " " + cityArr[i].grade);
        }

        QuicKSort(cityArr, 0, cityArr.length - 1);
        final GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.maingrad);
        LinearLayout l = findViewById(R.id.list);
        int rank = 1;

        //도 광역시 이름이 province 인 지역들만 가저와서 버튼으로 동적생성
            for (int i = 0; i < 154; i++) {
                if (cityArr[i].province.contentEquals(t.getText())) {
                    LinearLayout.LayoutParams r = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    r.setMargins(0, 10, 0, 20);
                    final Button b = new Button(this);
                    String text = rank + "위 " + t.getText() + " " + cityArr[i].name;
                    b.setText(text);
                    b.setBackground(drawable);
                    b.setTextColor(Color.BLACK);
                    b.setHeight(160);
                    b.setTextSize(18);
                    b.setLayoutParams(r);
                    Typeface font = getResources().getFont(R.font.font);
                    b.setTypeface(font);
                    l.addView(b);
                    rank++;
                    b.setOnClickListener(v -> {
                        Intent graphIntent = new Intent(RankRecommendSub.this,GraphActivity.class);
                        String buttonText = b.getText().toString();
                        String[] strArr = buttonText.split(" ");
                        String reComposition = strArr[1] + " " + strArr[2];
                        graphIntent.putExtra("region",reComposition);
                        graphIntent.putExtra("response","0");
                        startActivity(graphIntent);
                    });
                    b.setBackground(drawable);
                }
            }


        //뒤로가기 버튼 함수구현
        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }




}