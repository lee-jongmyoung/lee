package com.project.food;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StampView extends AppCompatActivity {

    private static String TAG = "food";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_JID = "jid";
    private static final String TAG_JNAME = "jname";
    private static final String TAG_NUM = "number";

    private static String IP_ADDRESS = "web02.privsw.com";

    ArrayList<String> storeJid = new ArrayList<>();

    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;
    ListView mlistView;

    String id = Login.idd;
    String jid= Login.merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_view);

        setTitle("스탬프 현황");

        // 리스트뷰 코드
        mlistView = (ListView) findViewById(R.id.stamplist);
        mArrayList = new ArrayList<>();

        TextView tvs = (TextView)findViewById(R.id.empty_stamp);
        mlistView.setEmptyView(tvs);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                jid = storeJid.get(position);
                Intent intent = new Intent(StampView.this, StoreStamp.class);
                Bundle sendbundle = new Bundle();
                sendbundle.putString("jid", jid);
                intent.putExtra("Bundle",sendbundle);
                setResult(1, intent);
                startActivity(intent);

            }
        });

        // storeprint 점포 정보 디비에서 가져오기
        GetDataStore task1= new GetDataStore();
        task1.execute(id);

    }

    private class GetDataStore extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StampView.this,
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

            String serverURL = "http://web02.privsw.com/stampvilist.php";
            String postParameters = "id=" + searchKeyword;

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

                    // String jid = item.getString(TAG_JID);
                    String jname = item.getString(TAG_JNAME);
                    String num = item.getString(TAG_NUM);

                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(TAG_JNAME, jname);
                    hashMap.put(TAG_NUM, num);

                    mArrayList.add(hashMap);

//                    eJname.setText(jname);
//                    eGrade.setText(grade);
//                    eTstart.setText(tstart);
//                    eTend.setText(tend);
//                    ePhone.setText(mphone);
                    storeJid.add(jid);

                }
                ListAdapter adapter = new SimpleAdapter(
                        StampView.this, mArrayList, R.layout.stamp_list,
                        new String[]{TAG_JNAME, TAG_NUM},
                        new int[]{R.id.jname, R.id.stampnum}
                );

                mlistView.setAdapter(adapter);

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }
}
