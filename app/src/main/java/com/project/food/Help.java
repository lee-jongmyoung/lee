package com.project.food;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setTitle("도움말");

        // 이미지
        ImageView i1 = (ImageView) findViewById(R.id.image21) ;
        i1.setImageResource(R.drawable.ic_action_grade_black);

        ImageView i2 = (ImageView) findViewById(R.id.image22) ;
        i2.setImageResource(R.drawable.gradex);

        ImageView i3 = (ImageView) findViewById(R.id.image23) ;
        i3.setImageResource(R.drawable.grade1);

        ImageView i4 = (ImageView) findViewById(R.id.image24) ;
        i4.setImageResource(R.drawable.grade2);

        ImageView i5 = (ImageView) findViewById(R.id.image25) ;
        i5.setImageResource(R.drawable.grade3);

        ImageView i6 = (ImageView) findViewById(R.id.image26) ;
        i6.setImageResource(R.drawable.grade4);

        ImageView i7 = (ImageView) findViewById(R.id.image27) ;
        i7.setImageResource(R.drawable.grade5);

        ImageView i8 = (ImageView) findViewById(R.id.image28) ;
        i8.setImageResource(R.drawable.ic_action_bookmark_border);

        ImageView i9 = (ImageView) findViewById(R.id.image29) ;
        i9.setImageResource(R.drawable.ic_action_content_paste);
    }
}