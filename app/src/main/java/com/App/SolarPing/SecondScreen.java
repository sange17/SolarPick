package com.App.SolarPing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SecondScreen extends AppCompatActivity {

    Button star_button;
    RatingBar ratingStars;
    static float myRating = 0;

    private static final String TAG = "SecondScreen";
    private DatabaseReference database;
    EditText titleEdit;
    EditText contentsEdit;
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String formatDate = sdfNow.format(date);
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        database = FirebaseDatabase.getInstance().getReference();
        findViewById(R.id.check).setOnClickListener(onClickListener);

        //뒤로가기버튼
        back = findViewById(R.id.back);

        //게시판 사용변수
        titleEdit = (EditText) findViewById(R.id.titleEditText);
        contentsEdit = (EditText) findViewById(R.id.contentsEditText);

        //별점 사용변수
        star_button = findViewById(R.id.star_check);
        ratingStars = findViewById(R.id.ratingBar);
        ratingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                int rating = (int) v;
                String message = null;

                myRating = (int)ratingBar.getRating();

                switch(rating) {
                    case 1:
                        message = "더 발전하겠습니다!";
                        break;
                    case 2:
                        message = "분발하겠습니다!";
                        break;
                    case 3:
                        message = "중간은 갔네요!";
                        break;
                    case 4:
                        message = "더더 열심히 하겠습니다!";
                        break;
                    case 5:
                        message = "감사합니다!";
                        break;
                }
                Toast.makeText(SecondScreen.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        star_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SecondScreen.this, String.valueOf(myRating), Toast.LENGTH_SHORT).show();
                star_uploader();
            }
        });

        ImageButton button = (ImageButton) findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.check) {
                uploader();
            }
        }
    };


    private void uploader(){
        String title = titleEdit.getText().toString();
        String contents = contentsEdit.getText().toString();
        FeedBack feedBack = new FeedBack(title,contents);
        database.child("feedback").child(formatDate).setValue(feedBack);
        Toast.makeText(getApplicationContext(),"전송완료",Toast.LENGTH_LONG).show();
    }

    private void star_uploader(){
        float rs_star = myRating;
        StarCount starcount = new StarCount(rs_star);
        database.child("StarNum").child(formatDate).setValue(starcount);
        Toast.makeText(getApplicationContext(), "전송완료", Toast.LENGTH_LONG).show();
    }


}
@IgnoreExtraProperties
class StarCount{
    public float starEdit;
    public StarCount(float starEdit){
        this.starEdit = starEdit;
    }
}

@IgnoreExtraProperties
class FeedBack{
    public String title;
    public String contents;
    public FeedBack(String title,String contents){
        this.title = title;
        this.contents = contents;
    }
}