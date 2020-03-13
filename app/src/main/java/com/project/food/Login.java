package com.project.food;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    public static boolean isLogin;
    public static boolean loginTrue = false;
    public static String idd;
    public static String nname;
    public static String name;
    public static String phone;
    public static String passs;
    public static String merchant;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("로그인");

        final EditText idText = (EditText)findViewById(R.id.id);
        final EditText passwordText = (EditText)findViewById(R.id.password);
        final Button loginbtn = (Button)findViewById(R.id.login);
        // final Button Nlogin = (Button) findViewById(R.id.Nlogin);
        final CheckBox chk_auto = (CheckBox) findViewById(R.id.Auto_Login);
        final Button member = (Button)findViewById(R.id.member);

        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();

        member.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(Login.this,Member.class);
                startActivity(intent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = idText.getText().toString();
                final String pass = passwordText.getText().toString();

                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            Toast.makeText(getApplicationContext(), "success"+success, Toast.LENGTH_SHORT).show();

                            //서버에서 보내준 값이 true이면?
                            if(success){

                                Toast.makeText(getApplicationContext(), "로그인성공", Toast.LENGTH_SHORT).show();

                                isLogin = true;

                                idd = jsonResponse.getString("id");
                                nname = jsonResponse.getString("nname");
                                name = jsonResponse.getString("name");
                                passs = jsonResponse.getString("pass");
                                phone = jsonResponse.getString("phone");
                                merchant = jsonResponse.getString("merchant");

                                //로그인에 성공했으므로 MainActivity로 넘어감
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("idd", idd);
                                intent.putExtra("pass", passs);
                                intent.putExtra("name", name);
                                intent.putExtra("nname", nname);
                                intent.putExtra("phone", phone);
                                intent.putExtra("isLogin",isLogin);
                                Login.this.startActivity(intent);

                                finish();

                            }else{//로그인 실패시
                                isLogin = false;
                                Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호 확인", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                builder.setMessage("Login failed")
                                        .setNegativeButton("retry", null)
                                        .create()
                                        .show();
                            }

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(id, pass, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(loginRequest);
            }
        });

        chk_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // TODO Auto-generated method stub

                if(isChecked){
                    String ID = idText.getText().toString();
                    String PW = passwordText.getText().toString();
                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("chk_auto_enabled", true);
                    editor.commit();
                }else{
                    editor.clear();
                    editor.commit();
                }
            }
        });
        if(setting.getBoolean("chk_auto_enabled", false)){
            idText.setText(setting.getString("ID", ""));
            passwordText.setText(setting.getString("PW", ""));
            chk_auto.setChecked(true);
        }
    }
}