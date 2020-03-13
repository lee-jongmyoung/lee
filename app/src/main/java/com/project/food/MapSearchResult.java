package com.project.food;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapSearchResult extends Activity {

    ArrayAdapter<CharSequence> adspin1, adspin2; //어댑터 선언

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public final String PREFERENCE = "com.studio572.samplesharepreference";
    ArrayList<String> latestArray = ((MainActivity)MainActivity.context).latestArray;

    // 홈 디비 변수 선언
    private static String IP_ADDRESS = "web02.privsw.com";
    private static String TAG = "food";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_JID ="jid";
    private static final String TAG_JNAME ="jname";
    private static final String TAG_GRADE ="grade";
    private static final String TAG_LAT ="lat";
    private static final String TAG_LON ="lon";

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mListViewList;
    EditText mEditTextSearchKeyword;
    String mJsonString;
    String squery, squery2;
    String jid;

    ListView mlistView;
    private SearchView searchView;
    ListAdapter adapter;

    String spinOneFlag = "전체";
    Double spinTwoFlag = -1.0;

    static LatLng myLatLng;
    LatLng SearchLatLng = myLatLng;


    ArrayList<ListVO> listVO = new ArrayList<ListVO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search_result);

//        // 이미지
//        ImageView i1 = (ImageView) findViewById(R.id.star2) ;
//        i1.setImageResource(R.drawable.ic_action_grade);

        Intent intent = getIntent();
        squery = intent.getExtras().getString("search");

        //search view
        searchView = (SearchView)findViewById(R.id.search22);
        searchView.setQuery(squery, false);
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                squery2 = searchView.getQuery().toString();
                GetDataTotal1 task123 = new GetDataTotal1();  //Total = searchresult3.php
                task123.execute(squery2);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // init();
        // 스피너
        final Spinner spin1 = (Spinner) findViewById(R.id.spinner0101);
        final Spinner spin2 = (Spinner) findViewById(R.id.spinner0202);

        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner03, android.R.layout.simple_spinner_dropdown_item);
        adspin2 = ArrayAdapter.createFromResource(this, R.array.spinner02, android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adspin1);
        spin2.setAdapter(adspin2);

        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = (String)spin1.getSelectedItem();

                SearchLatLng = myLatLng;  //평소 거리계산은 내 위치로 해야하니까..

                if(text.equals("전체")){
                    listVO.clear();
                    spinOneFlag = "전체";
                    GetDataTotal task1 = new GetDataTotal();  //Total = searchresult3.php
                    task1.execute(squery);
                }
                if(text.equals("메뉴")){
                    listVO.clear();
                    spinOneFlag = "메뉴";
                    GetDatamenu task1 = new GetDatamenu();  //Menu = searchresult2.php
                    task1.execute(squery);
                }
                if(text.equals("점포")){
                    listVO.clear();
                    spinOneFlag = "점포";
                    // adapter.notifyDataSetChanged();
                    GetData task = new GetData();       //Store = searchresult1.php
                    task.execute(squery);
                }
                if(text.equals("지역")){
                    listVO.clear();
                    spinOneFlag = "지역";
                    SearchLatLng = getCurrentLocation(squery);  //검색어를 주소로 삼아 LatLng로 바꾼다.
                    //LatLng과 각 shop들의 위치 관계로 거리계산을 하여 띄운다.
                    GetDataLocation task = new GetDataLocation();
                    task.execute();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = (String)spin2.getSelectedItem();
                listVO.clear();
                if (text.equals("1km"))
                {
                    spinTwoFlag = 1000.0;  //1km = 1000m
                }
                else if (text.equals("3km"))
                {
                    spinTwoFlag = 3000.0;  //3km = 3000m
                }
                else if (text.equals("5km"))
                {
                    spinTwoFlag = 5000.0;  //5km = 5000m
                }
                else if (text.equals("10km"))
                {
                    spinTwoFlag = 10000.0;  //10km = 10000m
                }
                else if (text.equals("필터off"))//필터off
                {
                    spinTwoFlag = -1.0;
                }


                if (spinOneFlag.equals("전체"))
                {
                    GetDataTotal task1 = new GetDataTotal();  //Total = searchresult3.php
                    task1.execute(squery);
                }
                else if (spinOneFlag.equals("메뉴"))
                {
                    GetDatamenu task1 = new GetDatamenu();  //Menu = searchresult2.php
                    task1.execute(squery);
                }
                else if (spinOneFlag.equals("점포"))
                {
                    GetData task = new GetData();       //Store = searchresult1.php
                    task.execute(squery);
                }
                else if (spinOneFlag.equals("지역"))
                {
                    SearchLatLng = getCurrentLocation(squery);
                    GetDataLocation task = new GetDataLocation();
                    task.execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        GetData task = new GetData();
//        task.execute(squery);

        mlistView = (ListView) findViewById(R.id.listview11);
        mArrayList = new ArrayList<>();

        ListViewAdapter customAdapter = new ListViewAdapter(getApplicationContext(), R.layout.search_listview, listVO);
        mlistView.setAdapter(customAdapter); //그리드뷰 cumtomAdapter 호출
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, ""+al.get(position).jid +"", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MapSearchResult.this, MainActivity.class);

                Bundle sendbundle = new Bundle();
                sendbundle.putString("jid", listVO.get(position).jid);
                sendbundle.putString("lat", ""+listVO.get(position).lat);
                sendbundle.putString("lon", ""+listVO.get(position).lon);
                intent.putExtra("Bundle",sendbundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(1, intent);
                startActivity(intent);

            }
        });

    } //onCreate 끝

    // 프리퍼런스에 배열 넣기
    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size() ; i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty() ) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    public class ListViewAdapter extends BaseAdapter {

        public ArrayList<ListVO> listVO = new ArrayList<ListVO>();
        Context context;
        int layout;
        LayoutInflater inf;
        public ListViewAdapter(Context context, int layout, ArrayList<ListVO> listVO){
            this.context = context;
            this.layout = layout;
            this.listVO = listVO;
            inf = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return listVO.size();
        }

        @Override
        public Object getItem(int position) {
            return listVO.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.search_listview, parent, false);
            }

//            TextView Jid = (TextView)convertView.findViewById(R.id.textView_list_jid);
            TextView Jname = (TextView)convertView.findViewById(R.id.textView_list_jname);
            TextView Grade = (TextView)convertView.findViewById(R.id.textView_list_grade);
            TextView Myaddress = (TextView)convertView.findViewById(R.id.SearchLocation);

            ListVO listViewItem = listVO.get(position);

//            Jid.setText(listViewItem.jid);
            Jname.setText(listViewItem.jname);
            Grade.setText(listViewItem.grade);
            Myaddress.setText(getCurrentAddress(listViewItem.lat,listViewItem.lon));

            return convertView;
        }
    }


    class ListVO {
        String jid;
        String jname;
        String grade;
        Double lat;
        Double lon;

        public void setJname(String jname){
            this.jname = jname;
        }
        public void setJid(String jid){
            this.jid = jid;
        }
        public void setJGrade(String grade){
            this.grade = grade;
        }
        public void setLat(Double lat){
            this.lat = lat;
        }
        public void setLon(Double lon){
            this.lon = lon;
        }
        public String getJname(){
            return jname;
        }
        public String getJid(){
            return jid;
        }
        public String getGrade(){
            return grade;
        }
        public Double getLat(){
            return lat;
        }
        public Double getLon(){
            return lon;
        }
        public ListVO(String jid, String jname, String grade, String lat, String lon){
            super();
            this.jid=jid;
            this.jname=jname;
            this.grade=grade;
            this.lat = Double.parseDouble(lat);
            this.lon = Double.parseDouble(lon);

        }
    }


    // 점포명을 찾는 디비 코드
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MapSearchResult.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                // mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = squery;

            String serverURL = "http://web02.privsw.com/searchresult1.php";
            String postParameters = "jname=" + searchKeyword;


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

                listVO.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String jid = item.getString(TAG_JID);
                    String jname = item.getString(TAG_JNAME);
                    String grade = item.getString(TAG_GRADE);
                    String lat = item.getString(TAG_LAT);
                    String lon = item.getString(TAG_LON);

//                    LatLng myLatLng;
//
//                    myLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));

                    //지오코딩 주소
                    String Myaddress = getCurrentAddress(Double.parseDouble(lat),Double.parseDouble(lon));


                    LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    Double shopDistance = getDistance(SearchLatLng, shopLatLng);



                    if (shopDistance < spinTwoFlag)  //spin2에서 고른 거리보다 작으면 데이터 저장
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else if (spinTwoFlag == -1.0) //필터 off
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else //아니라면 건너뜀
                    {continue;}

                    mlistView = findViewById(R.id.listview11);
                    ListViewAdapter customAdapter = new ListViewAdapter(getApplicationContext(), R.layout.search_listview, listVO);
                    mlistView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();

                }


            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }

    }

    //지도 - 위/경도 => 주소 변환 지오코딩
    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }

    // 메뉴 찾는 디비 코드
    private class GetDatamenu extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MapSearchResult.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {

                // mTextViewResult.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = squery;

            String serverURL = "http://web02.privsw.com/searchresult2.php";
            String postParameters = "menu=" + searchKeyword;


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

                listVO.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String jid = item.getString(TAG_JID);
                    String jname = item.getString(TAG_JNAME);
                    String grade = item.getString(TAG_GRADE);
                    String lat = item.getString(TAG_LAT);
                    String lon = item.getString(TAG_LON);

//                    HashMap<String, String> hashMap = new HashMap<>();
//
//                    hashMap.put(TAG_JID, jid);
//                    hashMap.put(TAG_JNAME, jname);
//                    hashMap.put(TAG_GRADE, grade);
//
//                    mArrayList.clear();
////                    mlistView.clearChoices();
////                    ((SimpleAdapter) adapter).notifyDataSetChanged();
//
//                    mArrayList.add(hashMap);

                    //지오코딩 주소
                    String Myaddress = getCurrentAddress(Double.parseDouble(lat),Double.parseDouble(lon));


                    LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    Double shopDistance = getDistance(SearchLatLng, shopLatLng);



                    if (shopDistance < spinTwoFlag)  //spin2에서 고른 거리보다 작으면 데이터 저장
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else if (spinTwoFlag == -1.0) //필터 off
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else //아니라면 건너뜀
                    {continue;}

                    mlistView = findViewById(R.id.listview11);
                    ListViewAdapter customAdapter = new ListViewAdapter(getApplicationContext(), R.layout.search_listview, listVO);
                    mlistView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();

                }

//                ListAdapter adapter = new SimpleAdapter(
//                        SearchResult.this, mArrayList, R.layout.search_listview,
//                        new String[]{TAG_JID, TAG_JNAME, TAG_GRADE},
//                        new int[]{R.id.textView_list_jid, R.id.textView_list_jname, R.id.textView_list_grade}
//                );
//
//                mlistView.setAdapter(adapter);

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    // 전체를 찾는 디비 코드
    private class GetDataTotal extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MapSearchResult.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                // mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = squery;

            String serverURL = "http://web02.privsw.com/searchresult3.php";
            String postParameters = "searchword=" + searchKeyword;


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


                listVO.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String jid = item.getString(TAG_JID);
                    String jname = item.getString(TAG_JNAME);
                    String grade = item.getString(TAG_GRADE);
                    String lat = item.getString(TAG_LAT);
                    String lon = item.getString(TAG_LON);

//                    HashMap<String, String> hashMap = new HashMap<>();
//
//                    hashMap.put(TAG_JID, jid);
//                    hashMap.put(TAG_JNAME, jname);
//                    hashMap.put(TAG_GRADE, grade);
//
////                    mArrayList.clear();
////                    mlistView.clearChoices();
////                    ((SimpleAdapter) adapter).notifyDataSetChanged();
//
//                    mArrayList.add(hashMap);

                    //지오코딩 주소
                    String Myaddress = getCurrentAddress(Double.parseDouble(lat),Double.parseDouble(lon));


                    LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    Double shopDistance = getDistance(SearchLatLng, shopLatLng);

                    if (shopDistance < spinTwoFlag)  //spin2에서 고른 거리보다 작으면 데이터 저장
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else if (spinTwoFlag == -1.0) //필터 off
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else //아니라면 건너뜀
                    {continue;}


                }
                mlistView = findViewById(R.id.listview11);
                ListViewAdapter customAdapter = new ListViewAdapter(getApplicationContext(), R.layout.search_listview, listVO);
                mlistView.setAdapter(customAdapter);

                customAdapter.notifyDataSetChanged();


            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }

    }


    // 전체를 찾는 디비 코드
    private class GetDataTotal1 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MapSearchResult.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                // mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = squery2;

            String serverURL = "http://web02.privsw.com/searchresult3.php";
            String postParameters = "searchword=" + searchKeyword;


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


                listVO.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String jid = item.getString(TAG_JID);
                    String jname = item.getString(TAG_JNAME);
                    String grade = item.getString(TAG_GRADE);
                    String lat = item.getString(TAG_LAT);
                    String lon = item.getString(TAG_LON);

//                    HashMap<String, String> hashMap = new HashMap<>();
//
//                    hashMap.put(TAG_JID, jid);
//                    hashMap.put(TAG_JNAME, jname);
//                    hashMap.put(TAG_GRADE, grade);
//
////                    mArrayList.clear();
////                    mlistView.clearChoices();
////                    ((SimpleAdapter) adapter).notifyDataSetChanged();
//
//                    mArrayList.add(hashMap);

                    //지오코딩 주소
                    String Myaddress = getCurrentAddress(Double.parseDouble(lat),Double.parseDouble(lon));


                    LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    Double shopDistance = getDistance(SearchLatLng, shopLatLng);

                    if (shopDistance < spinTwoFlag)  //spin2에서 고른 거리보다 작으면 데이터 저장
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else if (spinTwoFlag == -1.0) //필터 off
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else //아니라면 건너뜀
                    {continue;}

                }
                mlistView = findViewById(R.id.listview11);
                ListViewAdapter customAdapter = new ListViewAdapter(getApplicationContext(), R.layout.search_listview, listVO);
                mlistView.setAdapter(customAdapter);
                mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Toast.makeText(MainActivity.this, ""+al.get(position).jid +"", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MapSearchResult.this, MainActivity.class);

                        Bundle sendbundle = new Bundle();
                        sendbundle.putString("jid", listVO.get(position).jid);
                        sendbundle.putString("lat", ""+listVO.get(position).lat);
                        sendbundle.putString("lon", ""+listVO.get(position).lon);
                        intent.putExtra("Bundle",sendbundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        setResult(1, intent);
                        startActivity(intent);

                    }
                });
                customAdapter.notifyDataSetChanged();

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }

    }

    // 점포명을 찾는 디비 코드
    private class GetDataLocation extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MapSearchResult.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                // mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {



            String serverURL = "http://web02.privsw.com/store1.php";



            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

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

                listVO.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    jid = item.getString(TAG_JID);
                    String jname = item.getString(TAG_JNAME);
                    String grade = item.getString(TAG_GRADE);
                    String lat = item.getString(TAG_LAT);
                    String lon = item.getString(TAG_LON);

                    //지오코딩 주소
                    String Myaddress = getCurrentAddress(Double.parseDouble(lat),Double.parseDouble(lon));


                    LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    Double shopDistance = getDistance(SearchLatLng, shopLatLng);


                    if (shopDistance < spinTwoFlag)  //spin2에서 고른 거리보다 작으면 데이터 저장
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else if (spinTwoFlag == -1.0) //필터 off
                    {
                        listVO.add(new ListVO(""+jid+"",""+jname+"",""+grade+"",""+lat+"", ""+lon+""));
                    }
                    else //아니라면 건너뜀
                    {continue;}



                }
                mlistView = findViewById(R.id.listview11);
                ListViewAdapter customAdapter = new ListViewAdapter(getApplicationContext(), R.layout.search_listview, listVO);
                mlistView.setAdapter(customAdapter);
                mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Toast.makeText(MainActivity.this, ""+al.get(position).jid +"", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MapSearchResult.this, MainActivity.class);

                        Bundle sendbundle = new Bundle();
                        sendbundle.putString("jid", listVO.get(position).jid);
                        sendbundle.putString("lat", ""+listVO.get(position).lat);
                        sendbundle.putString("lon", ""+listVO.get(position).lon);
                        intent.putExtra("Bundle",sendbundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        setResult(1, intent);
                        startActivity(intent);

                    }
                });
                customAdapter.notifyDataSetChanged();


            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }

    }



    //지도 - 위/경도 => 주소 변환 지오코딩
    public LatLng getCurrentLocation(String Address) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try{
            List<android.location.Address> mResultLocation = geocoder.getFromLocationName(Address,1);
            double mLat = mResultLocation.get(0).getLatitude();
            double mLon = mResultLocation.get(0).getLongitude();

            return new LatLng(mLat,mLon);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }


    }



    public double getDistance(LatLng LatLng1, LatLng LatLng2) {

        if (LatLng1 != null && LatLng2 != null) {
            double mapdistance = 0;
            Location locationA = new Location("A");
            locationA.setLatitude(LatLng1.latitude);
            locationA.setLongitude(LatLng1.longitude);
            Location locationB = new Location("B");
            locationB.setLatitude(LatLng2.latitude);
            locationB.setLongitude(LatLng2.longitude);
            mapdistance = locationA.distanceTo(locationB);

            return mapdistance;
        }
        else
            return 0;
    }

}


