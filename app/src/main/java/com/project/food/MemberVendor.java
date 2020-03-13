package com.project.food;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

public class MemberVendor extends Activity {

    private static String IP_ADDRESS = "web02.privsw.com";
    private static String TAG = "food";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_JID = "jid";
    String mJsonString;

    private EditText eJname, eMname, emPhone;
    private Button eTimeStart, eTimeEnd;
    String saved_t;
    final Calendar cal = Calendar.getInstance();

    String id = "1";
    String jid, jname, mname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_vendor);

        eJname = (EditText)findViewById(R.id.vendorname_text);
        eMname = (EditText)findViewById(R.id.ownername_text);
        emPhone = (EditText)findViewById(R.id.ownberphonenumber_text);

        eTimeStart = (Button)findViewById(R.id.timebe);
        eTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(MemberVendor.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {

                        saved_t = hour + ":" + min;
//                Toast.makeText(Sch_list_save.this, saved_t , Toast.LENGTH_SHORT).show();
                        eTimeStart.setText(saved_t);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true);
                dialog.show();
            }
        });
        eTimeEnd = (Button)findViewById(R.id.timeaft);
        eTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(MemberVendor.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {

                        saved_t = hour + ":" + min;
//                Toast.makeText(Sch_list_save.this, saved_t , Toast.LENGTH_SHORT).show();
                        eTimeEnd.setText(saved_t);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true);
                dialog.show();
            }
        });

        // 색깔
        int color = Color.parseColor("#ffbb34");
        eJname.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        eMname.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        emPhone.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        // eTime.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);


        Button btn = (Button) findViewById(R.id.join1);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jname= eJname.getText().toString();
                mname = eMname.getText().toString();
                String mphone = emPhone.getText().toString();
                String tstart = eTimeStart.getText().toString();
                String tend = eTimeEnd.getText().toString();

                if(jname.length() != 0 && mname.length() != 0 && mphone.length() != 0 && !tstart.equals("START") && !tend.equals("END")) {
                    InsertData1 task1 = new InsertData1();
                    task1.execute("http://" + IP_ADDRESS + "/vendorregist1.php", jname, mname, mphone, tstart, tend);

                    eJname.setText("");
                    eMname.setText("");
                    emPhone.setText("");
                    eTimeStart.setText("START");
                    eTimeEnd.setText("END");

                    Intent intent = new Intent(MemberVendor.this, MemberScreen.class);
                    intent.putExtra("jname", jname);
                    intent.putExtra("mname", mname);
                    startActivity(intent);

                }else{
                    AlertDialog.Builder aDialog = new AlertDialog.Builder(MemberVendor.this);
                    aDialog.setTitle("상인등록 오류"); //타이틀바 제목
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

    class InsertData1 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MemberVendor.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String jname=(String)params[1];
            String mname = (String)params[2];
            String mphone = (String)params[3];
            String tstart = (String)params[4];
            String tend = (String)params[5];


            String serverURL = (String)params[0];
            String postParameters = "jname=" + jname + "&mname=" + mname + "&mphone=" + mphone + "&tstart=" + tstart + "&tend=" + tend;


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

