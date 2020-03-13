package com.project.food;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class LatestStore extends AppCompatActivity {

    ListView list;
    ListviewAdapter adapter;
    ArrayList<listviewData> arrData;

    private final String latestjumpo = "latestjumpo";
    SharedPreferences pref ;
    public final String PREFERENCE = "com.studio572.samplesharepreference";
    ArrayList<String> latestArray = ((MainActivity)MainActivity.context).latestArray;
    String[] ids;

    private static String IP_ADDRESS = "web02.privsw.com";
    private static String TAG = "food";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_JID = "jid";
    private static final String TAG_JNAME = "jname";
    private static final String TAG_GRADE = "grade";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_MYADDRESS = "Myaddress";


    private GPSInfo gps;
    private boolean isPermission = false;

    String mJsonString;
    ArrayList<HashMap<String, String>> mArrayList;

    String id, Myaddress;

//    Button timestate23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_store);

//        timestate23 = findViewById(R.id.timestate);

        //리스트에 보여줄 데이터를 세팅한다.
        setData();

        setTitle("최근 본 점포");

        //어댑터 생성
        adapter = new ListviewAdapter(this, arrData);

        //리스트뷰에 어댑터 연결
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        mArrayList = new ArrayList<>();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LatestStore.this, StoreTab.class);

                Bundle bundle = new Bundle();
                bundle.putString("jid",  mArrayList.get(position).get(TAG_JID));
                bundle.putString("lat", mArrayList.get(position).get(TAG_LAT) );
                bundle.putString("lon", mArrayList.get(position).get(TAG_LON));
                intent.putExtra("Bundle",bundle);
                setResult(1, intent);
                startActivity(intent);
            }
        });

        pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        getStringArrayPref(LatestStore.this, "jid");

        for (int i = 0; i < latestArray.size(); i++)
        {
            id = latestArray.get(latestArray.size() - (i+1));

            GetData task1 = new GetData();
            task1.execute(id);
        }

//        id = pref.getString("jid", "");

//        GetData task1 = new GetData();
//        task1.execute(id[i]);
    }

    // 프리퍼런스에 배열 가져오기
    private ArrayList<String> getStringArrayPref(Context context, String key) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        String json = pref.getString(key, null);
        latestArray = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    latestArray.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return latestArray;
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LatestStore.this,
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

            String serverURL = "http://web02.privsw.com/latestjumpo.php";
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
                    String grade = item.getString(TAG_GRADE);
                    String lat = item.getString(TAG_LAT);
                    String lon = item.getString(TAG_LON);

                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(TAG_JID, jid);
                    hashMap.put(TAG_JNAME, jname);
                    hashMap.put(TAG_GRADE, grade);
                    hashMap.put(TAG_LAT, lat);
                    hashMap.put(TAG_LON, lon);

                    mArrayList.add(hashMap);

                    gps = new GPSInfo(LatestStore.this);

                    Myaddress = getCurrentAddress(Double.parseDouble(lat), Double.parseDouble(lon));

                    hashMap.put(TAG_MYADDRESS, Myaddress);

                }

                ListAdapter adapter = new SimpleAdapter(
                        LatestStore.this, mArrayList, R.layout.listview_layout,
                        new String[]{TAG_JNAME, TAG_GRADE,TAG_MYADDRESS},
                        new int[]{R.id.jname, R.id.jgrade, R.id.location}
                );

                list.setAdapter(adapter);

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    //지도 - 위/경도 => 주소 변환 지오코딩
    public String getCurrentAddress(double gpsLatitude, double gpsLongitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    gpsLatitude,
                    gpsLongitude,
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

//    // 지도 - 권한 요청
//    private void callPermission() {
//        // Check the SDK version and whether the permission is already granted or not.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && ActivityCompat.checkSelfPermission(VendorPage.this,Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            requestPermissions(
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_ACCESS_FINE_LOCATION);
//
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && ActivityCompat.checkSelfPermission(VendorPage.this,Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED){
//
//            requestPermissions(
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    PERMISSIONS_ACCESS_COARSE_LOCATION);
//        } else {
//            isPermission = true;
//        }
//    }

    private void setData() {
        arrData = new ArrayList<listviewData>();
        arrData.add(new listviewData("붕어빵나라", "서울 송파구 송파대로 82"));
        arrData.add(new listviewData("파닭꼬치", "낙원동 103-4"));
        arrData.add(new listviewData("계란빵빵", "서울 강동구 천호동 양재대로 133길8"));
    }

    private class listviewData {
        private String name;
        private String location;

        public listviewData(String name, String location) {
            this.name = name;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public String getLocation() {
            return location;
        }
    }

    private class ListviewAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<listviewData> arrData;
        private LayoutInflater inflater;

        public ListviewAdapter(Context c, ArrayList<listviewData> arr) {
            this.context = c;
            this.arrData = arr;
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return arrData.size();
        }

        public Object getItem(int position) {
            return arrData.get(position).getName();
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview_layout, parent, false);
            }

//            TextView name = (TextView) convertView.findViewById(R.id.name);
//            name.setText(arrData.get(position).getName());
//
//            TextView location = (TextView) convertView.findViewById(R.id.location);
//            location.setText(arrData.get(position).getLocation());


            return convertView;
        }
    }
}