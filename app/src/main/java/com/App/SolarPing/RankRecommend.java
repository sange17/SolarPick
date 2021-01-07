package com.App.SolarPing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class RankRecommend extends AppCompatActivity {
    public void BtnConnect(Button[] CityTOPArr) {
        CityTOPArr[0] = (Button) findViewById(R.id.bt1);
        CityTOPArr[1] = (Button) findViewById(R.id.bt2);
        CityTOPArr[2] = (Button) findViewById(R.id.bt3);
        CityTOPArr[3] = (Button) findViewById(R.id.bt4);
        CityTOPArr[4] = (Button) findViewById(R.id.bt5);
        CityTOPArr[5] = (Button) findViewById(R.id.bt6);
        CityTOPArr[6] = (Button) findViewById(R.id.bt7);
        CityTOPArr[7] = (Button) findViewById(R.id.bt8);
        CityTOPArr[8] = (Button) findViewById(R.id.bt9);
        CityTOPArr[9] = (Button) findViewById(R.id.bt10);
        CityTOPArr[10] = (Button) findViewById(R.id.bt11);
        CityTOPArr[11] = (Button) findViewById(R.id.bt12);
        CityTOPArr[12] = (Button) findViewById(R.id.bt13);
    }
    public void CityBtnConnect(final Button[] CityTOPArr, String[] cities)
    {
        for(int i = 0;i<13;i++)
        {
            CityTOPArr[i].setTextSize(15);
            CityTOPArr[i].setGravity(50);
            CityTOPArr[i].setText(cities[i]);
            CityTOPArr[i].setGravity(Gravity.CENTER);


        }
    }

    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), RankRecommendSub.class);
        intent.putExtra("province", "서울특별시");
        if (v.getId() == R.id.bt1) {
            intent.putExtra("province", "서울특별시");
        }
        else if (v.getId() == R.id.bt2) {
            intent.putExtra("province", "대전광역시");
        }
        else if (v.getId() == R.id.bt3){
            intent.putExtra("province", "광주광역시");
        }
        else if (v.getId() == R.id.bt4) {
            intent.putExtra("province", "부산광역시");
        }
        else if (v.getId() == R.id.bt5) {
            intent.putExtra("province", "인천광역시");
        }
        else if (v.getId() == R.id.bt6) {
            intent.putExtra("province", "전라북도");
        }
        else if (v.getId() == R.id.bt7) {
            intent.putExtra("province", "전라남도");
        }
        else if (v.getId() == R.id.bt8) {
            intent.putExtra("province", "충청북도");
        }
        else if (v.getId() == R.id.bt9) {
            intent.putExtra("province", "충청남도");
        }
        else if (v.getId() == R.id.bt10) {
            intent.putExtra("province", "강원도");
        }
        else if (v.getId() == R.id.bt11) {
            intent.putExtra("province", "경상남도");
        }
        else if (v.getId() == R.id.bt12) {
            intent.putExtra("province", "경상북도");
        }
        else if (v.getId() == R.id.bt13) {
            intent.putExtra("province", "경기도");
        }
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_recommend);
        Button[] CityTOPArr = new Button[13];
        BtnConnect(CityTOPArr);
        String[] cities = {"서울특별시", "대전광역시", "광주광역시", "부산광역시", "인천광역시", "전라북도", "전라남도", "충청북도",
                 "충청남도", "강원도", "경상남도", "경상북도", "경기도"};
        CityBtnConnect(CityTOPArr, cities);

        ImageButton back = findViewById(R.id.back2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}