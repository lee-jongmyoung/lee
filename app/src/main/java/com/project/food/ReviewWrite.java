package com.project.food;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.HashMap;

public class ReviewWrite extends Activity {

    private static String IP_ADDRESS = "web02.privsw.com";
    private static String TAG = "review";

    private TextView eNname;
    private EditText eMoment;
    private TextView eJname;

    Spinner eGrade;
    ArrayAdapter<CharSequence> grade;

    String rNname, rGrade, rMoment;
    String jid;

    public static final String RESULT_NNAME="ResultNname";
    public static final String RESULT_GRADE="ResultGrade";
    public static final String RESULT_MOMENT="ResultMoment";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_JNAME = "jname";
    private static final String TAG_NNAME = "nname";

    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_review_write);

        // 이미지
        ImageView i1 = (ImageView) findViewById(R.id.imageView19) ;
        i1.setImageResource(R.drawable.foodtruck);

        ImageView i2 = (ImageView) findViewById(R.id.image10) ;
        i2.setImageResource(R.drawable.ic_action_grade_black);

        ImageView i3 = (ImageView) findViewById(R.id.image11) ;
        i3.setImageResource(R.drawable.ic_action_account_circle);

        ImageView i4 = (ImageView) findViewById(R.id.image12) ;
        i4.setImageResource(R.drawable.ic_action_content_paste);

        eNname = (TextView) findViewById(R.id.nickname_text);
        eGrade = (Spinner) findViewById(R.id.reviewgrade);
        grade = ArrayAdapter.createFromResource(this, R.array.grade, android.R.layout.simple_spinner_dropdown_item);
        eGrade.setAdapter(grade);
        eGrade.setSelection(0);
        eMoment = (EditText) findViewById(R.id.review_text);

        Button btn = (Button) findViewById(R.id.btn);
//        buttonInsert.setOnClickListener(this);

        Intent data_receive = getIntent();
        jid = data_receive.getStringExtra("rejid");


        eJname = (TextView)findViewById(R.id.review_jname);

        //Jname 불러오기
        GetData task = new GetData();
        task.execute();

        // Nname 불러오기
        GetNData task1 = new GetNData();
        task1.execute();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nname = eNname.getText().toString();
                String grade = eGrade.getSelectedItem().toString();
                String moment = eMoment.getText().toString();

                if(nname.length() == 0 && grade.length() ==0 && moment.length() == 0){
                    show();
                }
                else{
                    InsertData task = new InsertData();
                    task.execute("http://" + IP_ADDRESS + "/review.php", jid, nname, grade, moment);
                }

                rNname = eNname.getText().toString();
                rGrade = eGrade.getSelectedItem().toString();
                rMoment = eMoment.getText().toString();

                if (rNname.length() != 0 && rGrade.length() != 0 && rMoment.length() != 0) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_NNAME, rNname);
                    intent.putExtra(RESULT_GRADE, rGrade);
                    intent.putExtra(RESULT_MOMENT, rMoment);
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED);
                }

                eNname.setText("");
                eGrade.setSelection(0);
                eMoment.setText("");

                finish();
            }
        });
    }

    void show() {
        AlertDialog.Builder review = new AlertDialog.Builder(this);
        review.setTitle("리뷰 쓰기");
        review.setMessage("모든 정보를 다 입력해주세요.");
        review.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 디비에서 회원 정보 삭제는 코드 작성해야함
                        dialog.dismiss();
                    }
                });
        review.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        review.show();
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ReviewWrite.this,
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
            String nname=(String)params[2];
            String grade = (String)params[3];
            String moment = (String)params[4];

            String serverURL = (String)params[0];
            String postParameters = "jid=" + jid + "&nname=" + nname + "&grade=" + grade + "&moment=" + moment;


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

    // jname 디비에서 가져오기
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ReviewWrite.this,
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

            String searchKeyword = jid;

            String serverURL = "http://web02.privsw.com/reviewjname.php";
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

                    // String jid = id;
                    String jname = item.getString(TAG_JNAME);

                    eJname.setText(jname);
                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    // nname 디비에서 가져오기
    private class GetNData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ReviewWrite.this,
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

            String searchKeyword = Login.idd;

            String serverURL = "http://web02.privsw.com/reviewnname.php";
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

                    String nname = item.getString(TAG_NNAME);

                    eNname.setText(nname);
                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }
}
