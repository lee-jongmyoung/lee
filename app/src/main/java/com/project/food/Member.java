package com.project.food;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Member extends Activity {

    private static String IP_ADDRESS = "web02.privsw.com";
    private static String TAG = "me";

    private EditText eID;
    private EditText ePass;
    private EditText ePassC;
    private EditText eName;
    private EditText eNm;
    private EditText ePho;
    private Button eConfirm;
    private boolean validate = false;
    private AlertDialog dialog;
//    private TextView mTextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        ImageView i1 = (ImageView) findViewById(R.id.imageView8) ;
        i1.setImageResource(R.drawable.ic_action_supervised_user_circle);

        eID = (EditText)findViewById(R.id.id);
        ePass = (EditText)findViewById(R.id.password);
        ePassC = (EditText)findViewById(R.id.password2);
        eName = (EditText)findViewById(R.id.name1);
        eNm = (EditText) findViewById(R.id.nm1);
        ePho = (EditText) findViewById(R.id.pho1);
        eConfirm = (Button) findViewById(R.id.confirm);
//        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
//
//        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());


        Button buttonInsert = (Button)findViewById(R.id.deleteButton);
//        buttonInsert.setOnClickListener(this);

        eConfirm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String id = eID.getText().toString();
                if(validate){
                    return;//검증 완료
                }
                //ID 값을 입력하지 않았다면
                if(id.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Member.this);
                    dialog = builder.setMessage("ID is empty")
                            .setPositiveButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }


                //검증시작
                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){//사용할 수 있는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(Member.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디 입니다.")
                                        .setPositiveButton("OK", null)
                                        .create();
                                dialog.show();
                                eID.setEnabled(false);//아이디값을 바꿀 수 없도록 함
                                validate = true;//검증완료
                            }else{//사용할 수 없는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(Member.this);
                                dialog = builder.setMessage("이미 사용중인 아이디 입니다.")
                                        .setNegativeButton("OK", null)
                                        .create();
                                dialog.show();
                            }

                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };//Response.Listener 완료

                //Volley 라이브러리를 이용해서 실제 서버와 통신을 구현하는 부분
                IdConfirmRequest idConfirmRequest = new IdConfirmRequest(id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Member.this);
                queue.add(idConfirmRequest);
            }

        });


        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = eID.getText().toString();
                String pass = ePass.getText().toString();
                String passC = ePassC.getText().toString();
                String name = eName.getText().toString();
                String nname = eNm.getText().toString();
                String phone = ePho.getText().toString();

                if (id.length() != 0 && pass.length() != 0 && passC.length() != 0 &&
                        name.length() != 0 && nname.length() != 0 && phone.length() != 0) {
                    if (!pass.equals(passC)) {
                        AlertDialog.Builder aDialog = new AlertDialog.Builder(Member.this);
                        aDialog.setTitle("회원가입 오류"); //타이틀바 제목
                        aDialog.setMessage("비밀번호가 일치하지 않습니다.");

                        aDialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        //팝업창 생성
                        AlertDialog ad = aDialog.create();
                        ad.show();//보여줌!

                    } else if(pass.equals(passC)){
                        InsertData task = new InsertData();
                        task.execute("http://" + IP_ADDRESS + "/member.php", id, pass, name, nname, phone);

                        eID.setText("");
                        ePass.setText("");
                        ePassC.setText("");
                        eName.setText("");
                        eNm.setText("");
                        ePho.setText("");

                        AlertDialog.Builder aDialog = new AlertDialog.Builder(Member.this);
                        aDialog.setTitle("회원가입 완료"); //타이틀바 제목
                        aDialog.setMessage("회원가입이 완료 되었습니다.");

                        aDialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Member.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                            }
                        });
                        //팝업창 생성
                        AlertDialog ad = aDialog.create();
                        ad.show();//보여줌!
                    }

                }else{
                    AlertDialog.Builder aDialog = new AlertDialog.Builder(Member.this);
                    aDialog.setTitle("회원가입 오류"); //타이틀바 제목
                    aDialog.setMessage("정보를 모두 입력해주세요.");

                    aDialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    //팝업창 생성
                    AlertDialog ad = aDialog.create();
                    ad.show();//보여줌!
                }
            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog = ProgressDialog.show(MainActivity.this,
//                    "Please Wait", null, true, true);
//        }


//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            progressDialog.dismiss();
//             mTextViewResult.setText(result);
//            Log.d(TAG, "POST response  - " + result);
//        }


        @Override
        protected String doInBackground(String... params) {

            String id=(String)params[1];
            String pass = (String)params[2];
            String name = (String)params[3];
            String nname = (String)params[4];
            String phone = (String)params[5];

            String serverURL = (String)params[0];
            String postParameters = "id=" + id + "&pass=" + pass + "&name=" + name + "&nname=" + nname + "&phone=" + phone;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }

    }
}
