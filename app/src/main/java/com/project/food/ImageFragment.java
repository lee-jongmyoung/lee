package com.project.food;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

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

public class ImageFragment extends Fragment {

    ImageView imageView;

    private static String TAG = "food";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_JID = "jid";
    private static final String TAG_PICTURE="picture";

    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;

    String id = "3";
    String picture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_fragment, container, false);

        imageView = view.findViewById(R.id.imageView);

        if (getArguments() != null) {
            Bundle args = getArguments();
            // MainActivity에서 받아온 Resource를 ImageView에 셋팅

            byte[] byteArray = getArguments().getByteArray("imgBit");
            if (byteArray != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imageView.setImageBitmap(bmp);
            }
            else {  //args에 번들로 아이디 값을 넣어준 경우.
                imageView.setImageResource(args.getInt("imgRes"));
            }

        }



//        ImageFragment.GetData task = new ImageFragment.GetData();
//        task.execute(id);

        // 점포 id값 불러오기
//        Intent data_receive = getIntent();
//        id = data_receive.getStringExtra("jid");

        return view;
    }



//    private class GetData extends AsyncTask<String, Void, String> {
//
//        ProgressDialog progressDialog;
//        String errorString = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
////            progressDialog = ProgressDialog.show(ImageRegist.class,
////                    "Please Wait", null, true, true);
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
//            String serverURL = "http://web02.privsw.com/store.php";
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
//                    // String jid = id;
//                    String jid = item.getString(TAG_JID);
//
//                    String picture = item.getString(TAG_PICTURE);
//
//                    HashMap<String, String> hashMap = new HashMap<>();
//
//                    byte[] picturebytes = picture.getBytes();
//                    picturebytes = Base64.decode("변환된 문자열", Base64.NO_WRAP);
//                    Bitmap Bitmap = BitmapFactory.decodeByteArray(picturebytes, 0, picturebytes.length);
//                    imageView.setImageBitmap(Bitmap);
//
//                    mArrayList.add(hashMap);
//
//                }
//
//            } catch (JSONException e) {
//
//                Log.d(TAG, "showResult : ", e);
//            }
//        }
//    }
}
