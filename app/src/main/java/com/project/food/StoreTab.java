package com.project.food;

import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TabHost;
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

import static com.project.food.StoreStamp.stampcount;
import static com.project.food.StoreStamp.stamps;

public class StoreTab extends TabActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Tag tag;

    private static String TAG = "food";
    private static String IP_ADDRESS = "web02.privsw.com";
    String mJsonString;
    String Jid;
    Double Lat;
    Double Lon;
    String Tid;
    String Num;

    private Boolean isCurrect;
    StoreStamp storeStamp = new StoreStamp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView(R.layout.activity_store_tab);

        //NFC 태그 관련 코드
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent data_receive = getIntent();
        Bundle bundle = data_receive.getBundleExtra("Bundle");

        String id = bundle.getString("jid");

        String lat = bundle.getString("lat");

        String lon = bundle.getString("lon");

//        Intent data = getIntent();
//        String id = data.getStringExtra("jid");

        Jid = id;

        GetCheckTag task2 = new GetCheckTag();
        task2.execute("http://" + IP_ADDRESS + "/getchecktag.php");

        Intent storePrint = new Intent(StoreTab.this, StorePrint.class);
        Bundle sendbundle = new Bundle();
        sendbundle.putString("jid", id);
        sendbundle.putString("lat", lat);
        sendbundle.putString("lon",lon);
        storePrint.putExtra("Bundle",sendbundle);
        setResult(1, intent);

        Intent storeReview = new Intent(StoreTab.this, StoreReview.class);
        storeReview.putExtra("Bundle",sendbundle);
        setResult(1, intent);

        Intent storeStamp = new Intent(StoreTab.this, StoreStamp.class);
        storeStamp.putExtra("Bundle",sendbundle);
        setResult(1, intent);

        final TabHost tabHost = getTabHost();
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if(tabHost.getCurrentTab() == 2){
                    if(Login.isLogin == false){
                        show2();
                    }
                }
            }
        });

        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("정보").setContent(storePrint));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("리뷰").setContent(storeReview));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("스탬프").setContent(storeStamp));
    }

    void show2(){
        AlertDialog.Builder stamp = new AlertDialog.Builder(this);
        stamp.setTitle("로그인 확인");
        stamp.setMessage("스탬프는 로그인하셔야 이용가능합니다.\n로그인 하시겠습니까? ");
        stamp.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 디비에서 회원 정보 삭제는 코드 작성해야함
                        finish();
                        Intent intent = new Intent(StoreTab.this,Login.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
        stamp.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent data_receive = getIntent();
                        Bundle bundle = data_receive.getBundleExtra("Bundle");

                        String id = bundle.getString("jid");

                        String lat = bundle.getString("lat");

                        String lon = bundle.getString("lon");
                        Intent storePrint = new Intent(StoreTab.this, StoreTab.class);
                        Bundle sendbundle = new Bundle();
                        sendbundle.putString("jid", id);
                        sendbundle.putString("lat", lat);
                        sendbundle.putString("lon",lon);
                        storePrint.putExtra("Bundle",sendbundle);
                        startActivity(storePrint);
                        finish();
                        dialog.dismiss();
                    }
                });
        stamp.setCancelable(false);
        stamp.show();
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }


    //태그 인식되면 고고
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            String compareId = toHexString(tagId);
            isCurrect = storeStamp.checktag(compareId, Tid);
            //타이머 변수. sharedPrefference 로 끌어온다. 만약 두시간이 안됬다면 false, 두시간 이상 지났다면 true
        }
        if (tag != null && getTabHost().getTabWidget().getChildAt(2).isSelected() && !storeStamp.tagad.isShowing() && (stampcount < stamps.length) && isCurrect) {

            Num = Integer.toString(stampcount);


            if (Num.equals("0"))
            {
                StampInsert task5 = new StampInsert();
                task5.execute("http://" + IP_ADDRESS + "/stampInsert2.php");
            }

            else {
                Toast.makeText(this, "스탬프 적립됨", Toast.LENGTH_LONG).show();
                stamps[stampcount].setSelected(true);
                stampcount++;
                storeStamp.stampnum.setText("" + stampcount);
                Num = Integer.toString(stampcount);

                if (Integer.parseInt(Num) > 0 && Integer.parseInt(Num) < 11) {
                    StampUpdate task4 = new StampUpdate();
                    task4.execute("http://" + IP_ADDRESS + "/StampUpdate2.php");
                }

            }
//            //시간 구해서 적립
//            TimeSetting();

        } else if (tag != null && getTabHost().getTabWidget().getChildAt(2).isSelected() && !storeStamp.tagad.isShowing() && (stampcount == stamps.length)) {
            byte[] tagId = tag.getId();
            Toast.makeText(this, "스탬프가 가득 찼습니다. \n버튼을 눌러 스탬프를 사용해주세요!", Toast.LENGTH_LONG).show();

        } else if (tag != null && getTabHost().getTabWidget().getChildAt(2).isSelected() && storeStamp.tagad.isShowing() && stampcount == stamps.length && isCurrect) {
            storeStamp.tagad.cancel();
            Toast.makeText(this, "스탬프 사용됨", Toast.LENGTH_LONG).show();

            for (int i = 0; i < stamps.length; i++) {
                stamps[i].setSelected(false);
            }
            stampcount = 0;
            storeStamp.stampnum.setText(""+stampcount);

            Num = Integer.toString(stampcount);
            StampUpdate task4 = new StampUpdate();
            task4.execute("http://" + IP_ADDRESS + "/StampUse.php");




        } else if (tag != null && getTabHost().getTabWidget().getChildAt(2).isSelected() && storeStamp.tagad.isShowing() && stampcount < stamps.length) {
            storeStamp.tagad.cancel();
            Toast.makeText(this, "스탬프의 개수가 부족합니다", Toast.LENGTH_LONG).show();

        }

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

    // 스탬프 - 디비에서 스탬프 ID가져와 비교
    private class GetCheckTag extends AsyncTask<String, Void, String> {

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
                showResultTag();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String jid = Jid;


            String serverURL = params[0];
            String postParameters = "jid=" + jid;

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
                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResultTag() {

        String TAG_JSON = "webnautes";
        String TAG_STAMP = "stamp";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String stamp = item.getString(TAG_STAMP);

                if (stamp != null) {
                    Tid = stamp;
                }
                if (stamp == null) {
                    Tid = "null";
                }

            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }


    // 스탬프 - 디비에서 스탬프 ID가져와 비교
    private class StampUpdate extends AsyncTask<String, Void, String> {

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
            String id = (String) storeStamp.Id;
            String number = Num;

            String serverURL = params[0];
            String postParameters = "jid=" + jid + "&id=" + id + "&number=" + number;

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


    // 스탬프 - 디비에서 스탬프 ID가져와 비교
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

            Toast.makeText(StoreTab.this, "스탬프 적립됨", Toast.LENGTH_LONG).show();
            stamps[stampcount].setSelected(true);
            stampcount++;
            storeStamp.stampnum.setText(""+stampcount);
            Num = Integer.toString(stampcount);

            if (Integer.parseInt(Num) > 0 && Integer.parseInt(Num) < 11) {
                StampUpdate task4 = new StampUpdate();
                task4.execute("http://" + IP_ADDRESS + "/StampUpdate2.php");
            }


        }

        @Override
        protected String doInBackground(String... params) {

            String jid = (String) Jid;
            String id = (String) Login.idd;


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


//    public void TimeSetting() {
//        long now = System.currentTimeMillis();
//        Date date = new Date(now);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
//        String getTime = sdf.format(date);
//        String addedTime = addTwoHour(getTime);

//        SharedPreferences pref = getSharedPreferences("stampend_" + Jid, MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("stampEndTime", getTime);
//        editor.commit();


//        Toast.makeText(StoreTab.this, "현재 시각" + getTime +"\n두시간 뒤 :" + addedTime, Toast.LENGTH_SHORT).show();
//    }
//
//    public String GetStampTime() {
//        SharedPreferences pref = getSharedPreferences("stampend_" + Jid, MODE_PRIVATE);
//        String stamptime = pref.getString("stampEndTime", "");
//        return stamptime;
//    }

//    public boolean TimeDiffCheck(String Time1, String Time2) //프레퍼 타임과 현재 시간이 두시간 이상 차이난다면, true
//    {
//
//    }
//
//    public String addTwoHour(String dt) {
//
//            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
//
//            Calendar cal = Calendar.getInstance();
//
//            try{
//            Date date = format.parse(dt);
//            cal.setTime(date);
//            cal.add(Calendar.HOUR, 2);      //두시간 더하기
//
//            } catch (Exception e) {
//
//             // TODO: handle exception
//                return e.toString();
//
//            }
//
//
//            return format.format(cal.getTime());
//
//    }
}
