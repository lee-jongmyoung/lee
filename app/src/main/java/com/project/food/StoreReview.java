package com.project.food;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;

public class StoreReview extends AppCompatActivity {

    private static String IP_ADDRESS = "web02.privsw.com";
    private static String TAG = "food";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_JID = "jid";
    private static final String TAG_NNAME="nname";
    private static final String TAG_GRADE="grade";
    private static final String TAG_MOMENT="moment";

    Button reviewwrite;
    ListView rv_result;
    TextView rv_result1;

    String mJsonString;
    String id;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    TextView num;

    private static final int START_ACTIVITY=1;

    String nname, grade, moment;

    private SimpleCursorAdapter mAdapter;

    int a=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store_review);


        num = (TextView)findViewById(R.id.reviewnum);

        // 이미지
        ImageView i1 = (ImageView) findViewById(R.id.imageView9) ;
        i1.setImageResource(R.drawable.foodtruck);


        reviewwrite = (Button) findViewById(R.id.reviewwrite);

        // 리뷰 리스트
        mlistView = (ListView) findViewById(R.id.rv_result);
        mArrayList = new ArrayList<>();

        TextView tvs = (TextView)findViewById(R.id.empty_review);
        mlistView.setEmptyView(tvs);

        // 디비
        GetData task = new GetData();
        task.execute(id);

//        // 점포 id값 불러오기
//        Intent data_receive = getIntent();
//        id = data_receive.getStringExtra("jid");

        // 점포 id값 불러오기
        Intent data_receive = getIntent();
        Bundle bundle = data_receive.getBundleExtra("Bundle");
//        id = data_receive.getStringExtra("jid");

        if( bundle != null ) {

            id = bundle.getString("jid");

        }

        // 리뷰 쓰기 팝업 코드 (레이아웃을 팝업으로 띄우기)
        reviewwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Login.isLogin == true) {
                    Intent intent = new Intent(StoreReview.this, ReviewWrite.class);
                    intent.putExtra("rejid", id);
                    startActivityForResult(intent, START_ACTIVITY);
                }else{
                    Toast.makeText(getApplicationContext(), "리뷰쓰기는 로그인 후에 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void onActivityResult(int req, int res, Intent data){
        if(req == START_ACTIVITY){
            if(res != RESULT_OK){
                Toast.makeText(StoreReview.this,"Data Error",Toast.LENGTH_SHORT).show();
            }
            if(res == RESULT_OK){
                nname = data.getExtras().getString(ReviewWrite.RESULT_NNAME);
                grade = data.getExtras().getString(ReviewWrite.RESULT_GRADE);
                moment = data.getExtras().getString(ReviewWrite.RESULT_MOMENT);

                a++;
                num.setText(a+"개");
                hash();

            }
        }
    }

    private void hash(){
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(TAG_NNAME, nname);
        hashMap.put(TAG_GRADE, grade);
        hashMap.put(TAG_MOMENT, moment);

        mArrayList.add(hashMap);

        ListAdapter adapter = new SimpleAdapter(
                StoreReview.this, mArrayList, R.layout.review_listview,
                new String[]{TAG_NNAME, TAG_GRADE, TAG_MOMENT},
                new int[]{R.id.textView_list_nname, R.id.textView_list_grade, R.id.textView_list_moment}
        );

        mlistView.setAdapter(adapter);
    }

    // 디비에 넣기
    class InsertData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StoreReview.this,
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

            String nname = (String)params[1];
            String grade = (String)params[2];
            String moment = (String)params[3];

            String serverURL = (String)params[0];
            String postParameters = "nname=" + nname + "&grade=" + grade + "&moment=" + moment;


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

    // 디비에서 가져오기
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(StoreReview.this,
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

            String searchKeyword = id;

            String serverURL = "http://web02.privsw.com/reviewview.php";
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

                    ++a;

                    // String jid = id;
                    String jid = item.getString(TAG_JID);

                    String nname = item.getString(TAG_NNAME);
                    String grade = item.getString(TAG_GRADE);
                    String moment = item.getString(TAG_MOMENT);

                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(TAG_NNAME, nname);
                    hashMap.put(TAG_GRADE, grade);
                    hashMap.put(TAG_MOMENT, moment);

                    mArrayList.add(hashMap);

                }

                num = (TextView)findViewById(R.id.reviewnum);
                num.setText(a+"개");

                ListAdapter adapter = new SimpleAdapter(
                        StoreReview.this, mArrayList, R.layout.review_listview,
                        new String[]{TAG_NNAME, TAG_GRADE, TAG_MOMENT},
                        new int[]{R.id.textView_list_nname, R.id.textView_list_grade, R.id.textView_list_moment}
                );

                mlistView.setAdapter(adapter);

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }

    }
}
