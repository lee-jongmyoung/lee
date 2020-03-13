package com.project.food;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StoreStamp extends AppCompatActivity {

    Button button1, button2;
    Button stamp1,stamp2,stamp3,stamp4,stamp5,stamp6,stamp7,stamp8,stamp9,stamp10;
    Button stampbtn, stampno, stampuse;
    public static TextView stampnum;

    //전역변수 관련 코드
    GlobalValues globalValues = (GlobalValues) getApplication();

    private static String TAG = "food";
    private static String IP_ADDRESS = "web02.privsw.com";
    String mJsonString;

    String Jid;
    String Id = Login.idd;
    String Tid;

    //NFC관련 변수.
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Tag tag;

    //버튼 배열화 관련 변수. 배열로 안쓸거면 지울것!
    public static Button [] stamps;
    public static int stampcount = 0;
    public static AlertDialog tagad;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store_stamp);

        // 이미지
        ImageView i1 = (ImageView) findViewById(R.id.imageView14) ;
        i1.setImageResource(R.drawable.foodtruck);

        Intent data_receive = getIntent();
        Bundle bundle = data_receive.getBundleExtra("Bundle");

        Jid = bundle.getString("jid");



        stampno = (Button)findViewById(R.id.stampno);
        stampnum = (TextView) findViewById(R.id.stampnum);

        tagadshow();
        tagad.cancel();

        //NFC 태그 관련 코드
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //스탬프 버튼 배열화 하였음. 보기 불편하다면 원상복구 해도 큰 이상은 없음.
        stamps = new Button[10];
        int [] stampid = {R.id.stamp1,R.id.stamp2, R.id.stamp3,R.id.stamp4,R.id.stamp5, R.id.stamp6,R.id.stamp7,R.id.stamp8,R.id.stamp9,R.id.stamp10};

        for (int i = 0; i < stamps.length; i++) {
            stamps[i] = (Button) findViewById(stampid[i]);
        }

        GetStampfirst task = new GetStampfirst();
        task.execute( "http://" + IP_ADDRESS + "/getstampfirst.php");


        // 스탬프 쓰기 코드
        stampuse = (Button)findViewById(R.id.stampuse);
        stampuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }

            //            if (view == stampuse) { //view가 reviewwrite 이면 팝업실행 즉 버튼을 누르면 팝업창이 뜨는 조건
//                Context mContext = getApplicationContext();
//                LayoutInflater inflater = (LayoutInflater) ((Context) mContext).getSystemService(LAYOUT_INFLATER_SERVICE);
//
//                //R.layout.activity_reviewwrite는 xml 파일명이고  R.id.reviewwritepage는 보여줄 레이아웃 아이디
//                View layout = inflater.inflate(R.layout.activity_stampuse, (ViewGroup) findViewById(R.id.stampusepage));
//                final AlertDialog.Builder aDialog = new AlertDialog.Builder(tab_stamp.this);
            void show() {
                AlertDialog.Builder aDialog = new AlertDialog.Builder(StoreStamp.this);
                aDialog.setTitle("스탬프 사용"); //타이틀바 제목
                aDialog.setMessage("스탬프를 사용하시겠습니까?");

//                aDialog.setView(layout); //activity_reviewwrite.xml 파일을 뷰로 셋팅
                aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        tagadshow();

                        dialog.dismiss();
                    }
                });
                aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //팝업창 생성
                AlertDialog ad = aDialog.create();
                ad.show();//보여줌!
            }
        });
    }

    public static final String CHARS = "0123456789ABCDEF";

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; ++i) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F))
                    .append(CHARS.charAt(data[i] & 0x0F));
        }
        return sb.toString();
    }


    // 스탬프 - 디비 개수 가져
    private class GetStampfirst extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);


            if (result == null) {

            } else {
                mJsonString = result;
                showResult();
                stampnum.setText(""+stampcount);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String jid = Jid;
            String id = Id;

            String serverURL = params[0];
            String postParameters = "jid="+jid+"&id="+id;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){

        String TAG_JSON="webnautes";
        String TAG_NUM = "number";



        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String num = item.getString(TAG_NUM);

                if(num != null)
                {
                    stampcount = Integer.parseInt(num);
                }
                if (num == null)
                {
                    stampcount = 0;
                }
                stampsetting(stampcount);


            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
            stampcount = 0;

        }
    }


    public void tagadshow() {

        AlertDialog.Builder aDialog = new AlertDialog.Builder(StoreStamp.this);
        aDialog.setTitle("스탬프 사용"); //타이틀바 제목
        aDialog.setMessage("태그를 대주세요.");




        aDialog.setNegativeButton("사용 취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //팝업창 생성
        tagad = aDialog.create();
        tagad.show();//보여줌!
    }

    public void stampsetting(int stampcount)
    {
        if (stampcount > stamps.length)
        {stampcount = stamps.length; }

        for (int i = 0; i < stampcount; i++)
        {
            stamps[i].setSelected(true);
        }
    }





    public Boolean checktag(String compareId, String Tid)
    {
        if (compareId.equals(Tid))
        {return true;}
        else
            return false;

    }



    // 스탬프 - 스탬프가 0개일때 삽입
    private class StampInsert extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

        }

        @Override
        protected String doInBackground(String... params) {

            String jid = (String) Jid;
            String id = (String) Id;


            String serverURL = params[0];
            String postParameters = "jid=" + jid + "&id=" + id;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "StampUpdate : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }
}
