package com.project.food;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StorePrint extends AppCompatActivity implements OnMapReadyCallback {

    ViewPager viewPager;
    ListView mlistView;
    ImageView imageView2;
    TextView textView3;
    TextView memo1;
//    String[] fileArray = {"Android", "iPhone", "WindowMobile","Blackberry", "WebOS", "Ubuntu", "Windows10", "Mac OS"};

    private static String TAG = "food";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_JID = "jid";
    private static final String TAG_MENU = "menu";
    private static final String TAG_PRICE = "price";
    private static final String TAG_JNAME = "jname";
    private static final String TAG_GRADE = "grade";
    //    private static final String TAG_TIME = "time";
    private static final String TAG_TSTART = "tstart";
    private static final String TAG_TEND = "tend";
    private static final String TAG_MPHONE = "mphone";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_TIMESTATE = "timestate";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_MEMO = "memo";
    private static final String TAG_LIKES = "likes";

    private static String IP_ADDRESS = "web02.privsw.com";

    ArrayList<HashMap<String, String>> mArrayList;
    ArrayList<String> urlList = new ArrayList<>();
    ArrayList<Bitmap> Bitmaps = new ArrayList<>();
    String mJsonString;

    FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());

    TextView eJname, eGrade, eTstart,eTend, ePhone;
    String id, mid;
    String lat;
    String lon;
    LatLng ShopLatLng;

    // 즐겨찾기
    Button likebtn, timebtn;
    public static String like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store_print);

        // 이미지
        ImageView i1 = (ImageView) findViewById(R.id.imageView3) ;
        i1.setImageResource(R.drawable.time);

        ImageView i2 = (ImageView) findViewById(R.id.imageView6) ;
        i2.setImageResource(R.drawable.phone);

        ImageView i3 = (ImageView) findViewById(R.id.imageView33) ;
        i3.setImageResource(R.drawable.ic_action_bookmark_border);

        ImageView i4 = (ImageView) findViewById(R.id.menuimage1) ;
        i4.setImageResource(R.drawable.ic_action_comment);

        textView3 = (TextView)findViewById(R.id.textView3);

        memo1 = (TextView)findViewById(R.id.memo);

        timebtn = (Button)findViewById(R.id.timebutton);

        // 즐겨찾기 버튼
        likebtn = (Button)findViewById(R.id.likebtn);

        // TextView 선언
        eJname = (TextView)findViewById(R.id.truckname);
        eGrade = (TextView)findViewById(R.id.truckgrade);
        eTstart = (TextView)findViewById(R.id.tstarte);
        eTend = (TextView)findViewById(R.id.tende);
        ePhone = (TextView)findViewById(R.id.phonenumber2);



        // Fragment로 넘길 Image Resource
//        ArrayList<Integer> listImage = new ArrayList<>();
//        listImage.add(R.drawable.one);
//        listImage.add(R.drawable.two);
//        listImage.add(R.drawable.three);

        ViewPager viewPager = findViewById(R.id.viewPager);

        // ViewPager와  FragmentAdapter 연결
        viewPager.setAdapter(fragmentAdapter);

        viewPager.setClipToPadding(false);
        int dpValue = 16;
        float d = getResources().getDisplayMetrics().density;
        int margin = (int) (dpValue * d);
        viewPager.setPadding(margin, 0, margin, 0);
        viewPager.setPageMargin(margin/2);


        //fragment(지도부분) 생성
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.googlemap);
        mapFragment.getMapAsync(StorePrint.this);

        // 점포 id값 불러오기
        Intent data_receive = getIntent();
        Bundle bundle = data_receive.getBundleExtra("Bundle");
//        id = data_receive.getStringExtra("jid");


        if( bundle != null ) {

            id = bundle.getString("jid");

            lat = bundle.getString("lat");

            lon = bundle.getString("lon");

        }
        ////

        GetURL taskURL = new GetURL();
        taskURL.execute("http://web02.privsw.com/GetPictureURL.php");

        TextView jjid = (TextView)findViewById(R.id.jid);
        jjid.setText(id);
        ShopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));

        // 메뉴 리스트뷰 코드
        mlistView = (ListView) findViewById(R.id.menulist);
        mArrayList = new ArrayList<>();

        TextView tvs = (TextView)findViewById(R.id.empty_menu);
        mlistView.setEmptyView(tvs);

        GetGrade task3 = new GetGrade();
        task3.execute(id);

        GetDataStore task = new GetDataStore();
        task.execute(id);

        GetDataMenu task1 = new GetDataMenu();
        task1.execute(id);

        mid = Login.idd;

        // 즐겨찾기 값 가져오기
        if(Login.isLogin == true){
            GetData2 task2 = new GetData2();
            task2.execute(id, mid);
        }
        else {
            likebtn.setSelected(false);
        }


        // 즐겨찾기 버튼 -> true
        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Login.isLogin == false) {
                    Toast.makeText(getApplicationContext(), "즐겨찾기는 로그인 후 이용가능합니다.",
                            Toast.LENGTH_SHORT).show();

                }else{
                    switch (like) {
                        case "false":
                            like = "true";
                            InsertData task = new InsertData();
                            task.execute("http://" + IP_ADDRESS + "/liketrue.php", id,mid,"true");
                            likebtn.setSelected(true);
                            break;
                        case "true":
                            like = "false";
                            InsertData2 task2 = new InsertData2();
                            task2.execute("http://" + IP_ADDRESS + "/likefalse.php", id,mid);
                            likebtn.setSelected(false);
                            break;
                    }
                }
            }
        });

    }//onCreate 끝

    class FragmentAdapter extends FragmentPagerAdapter {

        // ViewPager에 들어갈 Fragment들을 담을 리스트
        private ArrayList<Fragment> fragments = new ArrayList<>();

        // 필수 생성자
        FragmentAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        // List에 Fragment를 담을 함수
        void addItem(Fragment fragment) {
            fragments.add(fragment);
        }

        void clearItem() {fragments.clear();}

    }

    private class GetDataStore extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StorePrint.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

//                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = id;

            String serverURL = "http://web02.privsw.com/getjstore.php";
            String postParameters = "jid=" + searchKeyword;

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

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }

        private void showResult() {
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String jid = item.getString(TAG_JID);

                    String jname = item.getString(TAG_JNAME);
//                    String grade = item.getString(TAG_GRADE);
                    String tstart = item.getString(TAG_TSTART);
                    String tend = item.getString(TAG_TEND);
                    String mphone = item.getString(TAG_MPHONE);
//                    String picture = item.getString(TAG_PICTURE);
                    String lat = item.getString(TAG_LAT);
                    String lon = item.getString(TAG_LON);
                    String timestate = item.getString(TAG_TIMESTATE);
                    String memo = item.getString(TAG_MEMO);

                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(TAG_JID, jid);

                    mArrayList.add(hashMap);

                    eJname.setText(jname);
//                     eGrade.setText(grade);

                    eTend.setText(tend);

                    if(tstart.equals("0") || tstart.equals("null")){
                        eTstart.setText("start");
                    }
                    else{
                        eTstart.setText(tstart);
                    }

                    if(tend.equals("0") || tend.equals("null")){
                        eTend.setText("end");
                    }
                    else{
                        eTend.setText(tend);
                    }

                    if(mphone.equals("0") || mphone.equals("null")){
                        ePhone.setText("전화번호가 없습니다");
                    }
                    else{
                        ePhone.setText(mphone);
                    }

//                    eTime.setText(time);
//                    ePhone.setText(phone);
                    if(memo.equals("0") || memo.equals("null")){
                        memo1.setText("메모가 없습니다");
                    }
                    else{
                        memo1.setText(memo);
                    }


                    ShopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));

//                    if (timestate == "true") {
//                        likebtn.setSelected(true);
//                    }
//                    else if (timestate == "false") {
//                        likebtn.setSelected(!likebtn.isSelected());
//                    }

                    if (timestate.equals("true")) {
                        timebtn.setSelected(true);
                    }
                    else if (timestate.equals("false")) {
                        timebtn.setSelected(false);
                    }


                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    private class GetDataMenu extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StorePrint.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

//                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = id;

            String serverURL = "http://web02.privsw.com/getmenu.php";
            String postParameters = "jid=" + searchKeyword;

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

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }

        private void showResult() {
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                mArrayList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    // String jid = item.getString(TAG_JID);

                    String menu = item.getString(TAG_MENU);
                    String price = item.getString(TAG_PRICE);

                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(TAG_MENU, menu);
                    hashMap.put(TAG_PRICE, price);

                    mArrayList.add(hashMap);

                }

                ListAdapter adapter = new SimpleAdapter(
                        StorePrint.this, mArrayList, R.layout.menu_listview,
                        new String[]{TAG_MENU, TAG_PRICE},
                        new int[]{R.id.textView_list_menu, R.id.textView_list_price}
                );

                mlistView.setAdapter(adapter);

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    // 평점 평균 가져오기
    private class GetGrade extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StorePrint.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

//                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = id;

            String serverURL = "http://web02.privsw.com/grade.php";
            String postParameters = "jid=" + searchKeyword;

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

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }

        private void showResult() {
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String jid = item.getString(TAG_JID);

                    String grade = item.getString(TAG_GRADE);

                    if(grade.length() != 0 ){
                        eGrade.setText(grade);
                    }
                    else {
                        eGrade.setText("0.00");
                    }

                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

//    private class GetDatapicture extends AsyncTask<String, Void, String> {
//
//        ProgressDialog progressDialog;
//        String errorString = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog = ProgressDialog.show(StorePrint.this,
//                    "Please Wait", null, true, true);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            progressDialog.dismiss();
////            mTextViewResult.setText(result);
//            Log.d(TAG, "response  - " + result);
//
//            if (result == null){
//
////                mTextViewResult.setText(errorString);
//            }
//            else {
//
//                mJsonString = result;
//                showResult();
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            String searchKeyword = id;
//
//            String serverURL = "http://web02.privsw.com/picture.php";
//            String postParameters = "jid=" + searchKeyword;
//
//            try {
//
//                URL url = new URL(serverURL);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setReadTimeout(5000);
//                httpURLConnection.setConnectTimeout(5000);
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.connect();
//
//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                outputStream.write(postParameters.getBytes("UTF-8"));
//                outputStream.flush();
//                outputStream.close();
//
//                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.d(TAG, "response code - " + responseStatusCode);
//
//                InputStream inputStream;
//                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
//                    inputStream = httpURLConnection.getInputStream();
//                } else {
//                    inputStream = httpURLConnection.getErrorStream();
//                }
//
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                StringBuilder sb = new StringBuilder();
//                String line;
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    sb.append(line);
//                }
//                bufferedReader.close();
//
//                return sb.toString().trim();
//
//            } catch (Exception e) {
//
//                Log.d(TAG, "InsertData: Error ", e);
//                errorString = e.toString();
//
//                return null;
//            }
//        }
//
//        private void showResult() {
//            try {
//                JSONObject jsonObject = new JSONObject(mJsonString);
//                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//                    JSONObject item = jsonArray.getJSONObject(i);
//
//                    String jid = item.getString(TAG_JID);
//
//                    String picture = item.getString(TAG_PICTURE);
//
//                    HashMap<String, String> hashMap = new HashMap<>();
//
//                    hashMap.put(TAG_JID, jid);
//
//                    byte[] picturebytes = Base64.decode(picture, 0);
//                    ByteArrayInputStream inStream = new ByteArrayInputStream(picturebytes);
//                    Bitmap Bitmap1 = BitmapFactory.decodeStream(inStream);
//
//                    textView3.setText(picture);
//
//                    imageView2.setImageBitmap(Bitmap1);
//
//                }
//
//            } catch (JSONException e) {
//
//                Log.d(TAG, "showResult : ", e);
//            }
//        }
//    }

    // like 디비 값 가져오기
    private class GetData2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StorePrint.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                like = "false";
                likebtn.setSelected(false);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = id;
            String searchKeyword2 = mid;

            String serverURL = "http://web02.privsw.com/likeget.php";
            String postParameters = "jid=" + searchKeyword1 + "&id=" +  searchKeyword2;


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

                if(!sb.toString().trim().equals(""))
                {return sb.toString().trim();}
                else {return null;}


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }

        private void showResult() {
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    // String jid = id;
                    String jid = item.getString(TAG_JID);
                    String mmid = item.getString(TAG_ID);
                    String likes = item.getString(TAG_LIKES);

                    if(id.equals(jid) && mid.equals(mmid)) {
                        like = "true";
                        likebtn.setSelected(true);
                    }
                    else if(!id.equals(jid) && !mid.equals(mmid)){
                        like = "false";
                        likebtn.setSelected(false);
                    }


                }

            } catch (JSONException e) {

//                like = "true";
//                likebtn.setSelected(true);
                Log.d(TAG, "showResult : ", e);
            }
        }

    }


    // 즐겨찾기 디비에 값 보내기 -> true
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StorePrint.this,
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

            String jid=(String)params[1];
            String id=(String)params[2];
            String likes=(String)params[3];

            String serverURL = (String)params[0];
            String postParameters = "jid=" + jid + "&id=" + id + "&likes=" + likes;


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

    // 즐겨찾기 값 보내기 -> false
    class InsertData2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StorePrint.this,
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

            String jid=(String)params[1];
            String id=(String)params[2];

            String serverURL = (String)params[0];
            String postParameters = "jid=" + jid + "&id=" + id;


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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ShopLatLng != null) {

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ShopLatLng);
            googleMap.addMarker(markerOptions);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ShopLatLng, 15));
        }
    }


    public void ThreadStart()
    {

        //Thread t = new Thread(Runnable 객체를 만든다);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다
                // TODO Auto-generated method stub
                try{
                    // 걍 외우는게 좋다 -_-;
//                    final ImageView iv = (ImageView)findViewById(R.id.imageView1);
                    for(int i = 0 ; i < urlList.size(); i++) {
                        URL url = new URL("http://web02.privsw.com/" + urlList.get(i));
                        InputStream is = url.openStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        Bitmaps.add(bm);
                    }
//                    iv.setImageBitmap(bm); //비트맵 객체로 보여주기
                } catch(Exception e){
                    Log.d("error : ",e.toString());
                }

            }
        });

        t.start();
        try
        {
            t.join();
            SetViewPager();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }



    // DB에서 상점 사진 가져오기
    private class GetURL extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if (result == null) {

            } else {
                mJsonString = result;
                showResult();
                ThreadStart();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String jid = id;

            String serverURL = params[0];
            String postParameters = "jid="+jid;

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
        String TAG_URL = "url";



        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String url = item.getString(TAG_URL);

                urlList.add(url);

            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }


    public void SetViewPager()
    {
        for (int i = 0; i < Bitmaps.size(); i++) {

            Bitmap bmp = Bitmaps.get(i);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();


            ImageFragment imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putByteArray("imgBit", byteArray);
            imageFragment.setArguments(bundle);
            fragmentAdapter.addItem(imageFragment);
        }
        fragmentAdapter.notifyDataSetChanged();

    }
}