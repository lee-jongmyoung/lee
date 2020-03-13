package com.project.food;

import android.app.Activity;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.Toast;



import com.android.volley.RequestQueue;

import com.android.volley.Response;

import com.android.volley.toolbox.Volley;



import org.json.JSONException;

import org.json.JSONObject;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setTitle("메뉴 등록");

        final EditText jidText1 = (EditText)findViewById(R.id.jidText1);
        final EditText menuText1 = (EditText)findViewById(R.id.menuText1);
        final EditText priceText1 = (EditText)findViewById(R.id.priceText1);
        final EditText menuText2 = (EditText)findViewById(R.id.menuText2);
        final EditText priceText2 = (EditText)findViewById(R.id.priceText2);
        final EditText menuText3 = (EditText)findViewById(R.id.menuText3);
        final EditText priceText3 = (EditText)findViewById(R.id.priceText3);
        final EditText menuText4 = (EditText)findViewById(R.id.menuText4);
        final EditText priceText4 = (EditText)findViewById(R.id.priceText4);
        final EditText menuText5 = (EditText)findViewById(R.id.menuText5);
        final EditText priceText5 = (EditText)findViewById(R.id.priceText5);

        // 색깔
        int color = Color.parseColor("#ffbb34");
        menuText1.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        priceText1.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        menuText2.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        priceText2.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        menuText3.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        priceText3.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        menuText4.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        priceText4.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        menuText5.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        priceText5.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        Intent i = getIntent();    //로그인 request 에서 인텐트 값을 받아옴
        final String idname = i.getStringExtra("id");
        jidText1.setText(String.valueOf(idname));
        jidText1.setVisibility(View.GONE);      //JID 를 받아온 edittext 를 레이아웃에서 안보여줌
        Button menubtn = (Button)findViewById(R.id.menubtn);
        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jid1 = Login.merchant;
                String menu1 = menuText1.getText().toString();
                String price1 = priceText1.getText().toString();
                String menu2 = menuText2.getText().toString();
                String price2 = priceText2.getText().toString();
                String menu3 = menuText3.getText().toString();
                String price3 = priceText3.getText().toString();
                String menu4 = menuText4.getText().toString();
                String price4 = priceText4.getText().toString();
                String menu5 = menuText5.getText().toString();
                String price5 = priceText5.getText().toString();

                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    //서버로부터 여기서 데이터를 받음
                    @Override
                    public void onResponse(String response) {
                        try {
                            //서버로부터 받는 데이터는 JSON타입의 객체이다.
                            JSONObject jsonResponse = new JSONObject(response);

                            //그중 Key값이 "success"인 것을 가져온다.
                            boolean success = jsonResponse.getBoolean("success");

                            //회원 가입 성공시 success값이 true임
                            if(success){
                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                                builder.setMessage("register success!!")
                                        .setPositiveButton("ok", null)
                                        .create()
                                        .show();
                                finish();
                            }
                            //회원 가입 실패시 success값이 false임
                            else{
                                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                                builder.setMessage("register fail!!")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };//responseListener 끝
                //volley 사용법
                //1. RequestObject를 생성한다. 이때 서버로부터 데이터를 받을 responseListener를 반드시 넘겨준다.
                MenuRequest menuRequest = new MenuRequest(jid1, menu1, price1,menu2,price2,menu3,price3,menu4,price4,menu5,price5 ,responseListener);
                //2. RequestQueue를 생성한다.
                RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
                //3. RequestQueue에 RequestObject를 넘겨준다.
                queue.add(menuRequest);
            }
        });
    }
}