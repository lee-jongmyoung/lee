package com.project.food;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Intent;

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

public class DeleteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        final EditText idText = (EditText)findViewById(R.id.id);
        final EditText passwordText = (EditText)findViewById(R.id.password);
        final EditText passwordText2 = (EditText)findViewById(R.id.password2);

        Intent intent = getIntent();

        final String idname = intent.getStringExtra("id");
        final String pass = intent.getStringExtra("pass");
        Button delbtn = (Button)findViewById(R.id.deleteButton);
        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idText.getText().toString();
                String password = passwordText.getText().toString();
                String password2 = passwordText2.getText().toString();
                if (id.equals(idname) && password.equals(pass) && password2.equals(pass)) {
                    if (id.length() != 0 && password.length() != 0 && password2.length() != 0) {
                        //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            //서버로부터 여기서 데이터를 받음
                            @Override
                            public void onResponse(String response) {
                                try {
                                    //서버로부터 받는 데이터는 JSON타입의 객체이다.

                                    JSONObject jsonResponse = new JSONObject(response);

                                    //그중 Key값이 "success"인 것을 가져온다.

                                    boolean success = jsonResponse.getBoolean("success");
                                    //회원 가입 성공시 success값이 true임

                                    if (success) {
                                        //알림상자를 만들어서 보여줌
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this);
                                        builder.setMessage("정상적으로 탈퇴 되었습니다.")
                                                .setPositiveButton("ok", null)
                                                .create()
                                                .show();
//                                        MainActivity.textid.setText("");

                                        Login.isLogin = false;
                                        Intent intent = new Intent(DeleteActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    //회원 가입 실패시 success값이 false임
                                    else {

                                        Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                        //알림상자를 만들어서 보여줌

                                        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this);
                                        builder.setMessage("register fail!!")
                                                .setNegativeButton("ok", null)
                                                .create()
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };//responseListener 끝

                        //volley 사용법

                        //1. RequestObject를 생성한다. 이때 서버로부터 데이터를 받을 responseListener를 반드시 넘겨준다.

                        DeleteRequest DeleteRequest = new DeleteRequest(id, responseListener);

                        //2. RequestQueue를 생성한다.

                        RequestQueue queue = Volley.newRequestQueue(DeleteActivity.this);

                        //3. RequestQueue에 RequestObject를 넘겨준다.

                        queue.add(DeleteRequest);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"빈칸 없이 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}