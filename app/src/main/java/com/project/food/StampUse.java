package com.project.food;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class StampUse extends AppCompatActivity {

    Button stampyes, stampno;
    Button stamp1, stamp2, stamp3, stamp4, stamp5, stamp6, stamp7, stamp8, stamp9, stamp10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stamp_use);

        stampyes = (Button)findViewById(R.id.stampyes);
        stampno = (Button)findViewById(R.id.stampno);

        stamp1 = (Button)findViewById(R.id.stamp1);
        stamp2 = (Button)findViewById(R.id.stamp2);
        stamp3 = (Button)findViewById(R.id.stamp3);
        stamp4 = (Button)findViewById(R.id.stamp4);
        stamp5 = (Button)findViewById(R.id.stamp5);
        stamp6 = (Button)findViewById(R.id.stamp6);
        stamp7 = (Button)findViewById(R.id.stamp7);
        stamp8 = (Button)findViewById(R.id.stamp8);
        stamp9 = (Button)findViewById(R.id.stamp9);
        stamp10 = (Button)findViewById(R.id.stamp10);

        stampyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.stampyes ){
                    stamp1.setSelected(stamp1.isSelected());
                    stamp2.setSelected(stamp2.isSelected());
                    stamp3.setSelected(stamp3.isSelected());
                    stamp4.setSelected(stamp4.isSelected());
                    stamp5.setSelected(stamp5.isSelected());
                    stamp6.setSelected(stamp6.isSelected());
                    stamp7.setSelected(stamp7.isSelected());
                    stamp8.setSelected(stamp8.isSelected());
                    stamp9.setSelected(stamp9.isSelected());
                    stamp10.setSelected(stamp10.isSelected());
                }
                if (v.getId() == R.id.stampno) {
                    StampUse.this.finish();
                }
            }
        });
    }
}
