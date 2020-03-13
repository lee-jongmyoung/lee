package com.project.food;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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

public class VendorPage extends Activity {

    private static String IP_ADDRESS = "web02.privsw.com";
    private static String TAG = "food";

    String jid = Login.merchant;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    TextView menuregist, imageregist, memoregist, timeregist, vendordelete;
    Button salesstart, salesfinish;
    Switch stampswitch;
    String mystamp = "0";

    GlobalValues globalValues = (GlobalValues) getApplication();

    String location;
    String picturegallery, picturecamera;

    private GPSInfo gps;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isPermission = false;
    LatLng myLatLng;
    ArrayList<LatLng> ShopLocations;

    final int REQ_CODE_SELECT_IMAGE = 100;
    final static int TAKE_PICTURE = 100;
    private Uri selPhotoUri;
    private String absolutePath;

    final ArrayList<LatLng> MyLocations = new ArrayList<LatLng>();

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_MNAME = "mname";
    private static final String TAG_NNAME = "nname";
    String mJsonString;

    TextView eMName, eNName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_page);

        // 이미지
        ImageView i1 = (ImageView) findViewById(R.id.image5) ;
        i1.setImageResource(R.drawable.ic_action_grade_black);

        ImageView i2 = (ImageView) findViewById(R.id.image6) ;
        i2.setImageResource(R.drawable.ic_action_bookmark_border);

        ImageView i3 = (ImageView) findViewById(R.id.image7) ;
        i3.setImageResource(R.drawable.ic_action_crop_original);

        ImageView i4 = (ImageView) findViewById(R.id.image8) ;
        i4.setImageResource(R.drawable.ic_action_content_paste);

        ImageView i5 = (ImageView) findViewById(R.id.image9) ;
        i5.setImageResource(R.drawable.ic_action_access_time);

        ImageView i6 = (ImageView) findViewById(R.id.image93) ;
        i6.setImageResource(R.drawable.ic_action_delete_forever);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        GetData task = new GetData();
        task.execute("http://web02.privsw.com/getStampCode.php", jid);


        menuregist = (TextView) findViewById(R.id.menuregist);
        menuregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menupage = new Intent(VendorPage.this, MenuActivity.class);
                startActivity(menupage);
            }
//                if (view == menuregist) { //view가 menuregist 이면 팝업실행 즉 버튼을 누르면 팝업창이 뜨는 조건
//                    Context mContext = getApplicationContext();
//                    LayoutInflater inflater = (LayoutInflater) ((Context) mContext).getSystemService(LAYOUT_INFLATER_SERVICE);
//
//                    //R.layout.activity_menuregist는 xml 파일명이고  R.id.menuregistpage은 보여줄 레이아웃 아이디
//                    View layout = inflater.inflate(R.layout.activity_menu_regist, (ViewGroup) findViewById(R.id.menuregistpage));
//                    AlertDialog.Builder aDialog = new AlertDialog.Builder(VendorPage.this);
//
//                    aDialog.setTitle("메뉴 등록"); //타이틀바 제목
//                    aDialog.setView(layout); //activity_menuregist.xml 파일을 뷰로 셋팅
//                    // 확인 버튼 코드
//                    aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // 여기에 디비로 옮기는 코드 적으면 됨
//                            dialog.dismiss();
//                        }
//                    });
//                    // 닫기 버튼 코드
//                    aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    //팝업창 생성
//                    AlertDialog ad = aDialog.create();
//                    ad.show();//보여줌!
//                }
//            }
//        });
        });
        imageregist = (TextView) findViewById(R.id.imageregist);
        registerForContextMenu(imageregist);
        imageregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(VendorPage.this, ImageRegist.class);
                startActivity(intent2);
//                if (v == imageregist) {
//                    Context mContext = getApplicationContext();
//                    LayoutInflater inflater = (LayoutInflater) ((Context) mContext).getSystemService(LAYOUT_INFLATER_SERVICE);
//
//                    //R.layout.activity_menuregist는 xml 파일명이고  R.id.menuregistpage은 보여줄 레이아웃 아이디
//                    View layout = inflater.inflate(R.layout.activity_imageregist, (ViewGroup) findViewById(R.id.imageregistpage));
//                    AlertDialog.Builder aDialog = new AlertDialog.Builder(vendorpage.this);
//
//                    aDialog.setTitle("사진 등록"); //타이틀바 제목
//                    aDialog.setView(layout); //activity_menuregist.xml 파일을 뷰로 셋팅
//                    // 확인 버튼 코드
//                    aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // 여기에 디비로 옮기는 코드 적으면 됨
//                            dialog.dismiss();
//                        }
//                    });
//                    // 닫기 버튼 코드
//                    aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    //팝업창 생성
//                    AlertDialog ad = aDialog.create();
//                    ad.show();//보여줌!
//                }
            }
        });

        memoregist = (TextView) findViewById(R.id.memoregist);
        memoregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent memopage = new Intent(VendorPage.this, MemoRegist.class);
                startActivity(memopage);
            }
        });

        timeregist = (TextView) findViewById(R.id.timeregist);
        timeregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timepage = new Intent(VendorPage.this, TimeRegist.class);
                startActivity(timepage);
            }
        });


        salesstart = (Button) findViewById(R.id.salesstart);
        salesstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 권한 요청을 해야 함
                if (!isPermission) {
                    callPermission();
                    return;
                }

                String timestate = "true";

                gps = new GPSInfo(VendorPage.this);

                double gpsLatitude = gps.getLatitude();
                double gpsLongitude = gps.getLongitude();

                String lat = Double.toString(gpsLatitude);
                String lon = Double.toString(gpsLongitude);

                String Myaddress = getCurrentAddress(gpsLatitude, gpsLongitude);
                Toast.makeText(
                        VendorPage.this.getApplicationContext(),
                        "당신의 위치 - \n주소: " + Myaddress, Toast.LENGTH_LONG).show();
                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/timestatetrue.php", jid, timestate, lat, lon);
        }
        });

        salesfinish = (Button) findViewById(R.id.salesfinish);
        salesfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timestate = "false";
                Toast.makeText(
                        VendorPage.this.getApplicationContext(),
                        "영업종료하였습니다.", Toast.LENGTH_LONG).show();
                InsertData2 task = new InsertData2();
                task.execute("http://" + IP_ADDRESS + "/timestatefalse.php", jid, timestate);

            }
        });

        vendordelete = (TextView) findViewById(R.id.storedelete);
        vendordelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String merchantid = Login.merchant;
                if (merchantid.equals("23")) {
                    Toast.makeText(getApplicationContext(), "업체가 등록되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                }else
                    show3();
            }

        });

        // 이름, 닉네임 띄우기
        eMName = (TextView)findViewById(R.id.Name2);
        eNName= (TextView)findViewById(R.id.Nickname2);

        GetName task2 = new GetName();
        task2.execute(Login.merchant);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "권한 설정 완료");
//            } else {
//                Log.d(TAG, "권한 설정 요청");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.d(TAG, "onRequestPermissionsResult");
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
//        }
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
//        if(v.getId() == R.id.imageregist)
//            getMenuInflater().inflate(R.menu.menu_image, menu);
//        super.onCreateContextMenu(menu, v, menuInfo);
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.gallery:
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
//                break;
//            case R.id.camera:
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, TAKE_PICTURE);
//                break;
//        }
//        return super.onContextItemSelected(item);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case TAKE_PICTURE:
//                if (resultCode == RESULT_OK && data.hasExtra("data")) {
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                    if (bitmap != null) {
//
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , stream);
//                        byte[] bytes = stream.toByteArray();
//                        picturecamera = Base64.encodeToString(bytes, 0);
//
//                        Intent cameraintent = new Intent(this, ImageRegist.class);
//                        cameraintent.putExtra("2", picturecamera);
//                        startActivity(cameraintent);
//
////                        textView2.setText(picture);
////
////                        byte[] picturebytes = Base64.decode(picture, 0);
////                        ByteArrayInputStream inStream = new ByteArrayInputStream(picturebytes);
////                        Bitmap Bitmap1 = BitmapFactory.decodeStream(inStream);
////                        imageView3.setImageBitmap(Bitmap1);
////
////                        image.setImageBitmap(bitmap);
//                    }
//                }
//                break;
//        }
//        if (requestCode == REQ_CODE_SELECT_IMAGE) {
//
//            if (resultCode == Activity.RESULT_OK) {
//                try {
//                    //Uri에서 이미지 이름을 얻어온다.
////                    String foodimg = getImageNameToUri(data.getData());
//                    selPhotoUri = data.getData();
//                    //절대경로 획득**
//                    Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null, null, null, null);
//                    c.moveToNext();
//                    absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
//
//                    //이미지 데이터를 Bitmap으로 받아옴
//                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//
//                    ///리사이징
////                    int height = image_bitmap.getHeight();
////                    int width = image_bitmap.getWidth();
////
////                    Bitmap src = BitmapFactory.decodeFile(absolutePath);
////                    Bitmap resized = Bitmap.createScaledBitmap( src, width/4, height/4, true );
////
////                    saveBitmaptoJpeg(resized, "seatdot", foodimg);
//                    ////리사이징
//
//                    //배치해놓은 ImageView에 set
//
//
//                    // bitmap을 byte로 변환
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    image_bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , stream);
//                    byte[] bytes = stream.toByteArray();
//                    picturegallery = Base64.encodeToString(bytes, 0);
//
//                    Intent galleryintent = new Intent(this, ImageRegist.class);
//                    galleryintent.putExtra("1", picturegallery);
//                    startActivity(galleryintent);
//
////                    textView2.setText(picture);
////
////                    byte[] picturebytes = Base64.decode(picture, 0);
////                    ByteArrayInputStream inStream = new ByteArrayInputStream(picturebytes);
////                    Bitmap Bitmap1 = BitmapFactory.decodeStream(inStream);
////
////                    imageView3.setImageBitmap(Bitmap1);
//
//                } catch (FileNotFoundException e) {
//
//                    // TODO Auto-generated catch block
//
//                    e.printStackTrace();
//
//                } catch (IOException e) {
//
//                    // TODO Auto-generated catch block
//
//                    e.printStackTrace();
//
//                }
//            }
//        }
//
//    }
//

    // 상인페이지 -> 이름, 닉네임 가져오기
    private class GetName extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(VendorPage.this,
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

            String searchKeyword = Login.merchant;

            String serverURL = "http://web02.privsw.com/getname.php";
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

                    String mname = item.getString(TAG_MNAME);
                    String nname = item.getString(TAG_NNAME);

                    eMName.setText(mname);
                    eNName.setText(nname);

                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    // 업체 삭제
    void show3(){
        AlertDialog.Builder vendordelete = new AlertDialog.Builder(this);
        vendordelete.setTitle("업체 삭제");
        vendordelete.setMessage("업체를 정말로 삭제하시겠습니까?");
        vendordelete.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Intent intent = getIntent();
                        String merchantid = Login.merchant;

//                        String phone = Login.phone;
//                        StoreDeleteJidText.setText(String.valueOf(phone));
//                        StoreDeleteJidText.setVisibility(View.GONE);

//                        String phone1 = StoreDeleteJidText.getText().toString();

                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");

                                    Toast.makeText(getApplicationContext(), "success"+success, Toast.LENGTH_SHORT).show();

                                    //서버에서 보내준 값이 true이면?
                                    if(success){

                                        Toast.makeText(getApplicationContext(), "업체삭제성공", Toast.LENGTH_SHORT).show();


                                    }else{//삭제 실패시
                                        Toast.makeText(getApplicationContext(), "업체가 등록되어 있지 않습니다.", Toast.LENGTH_SHORT).show();

                                    }

                                }catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        };

                        StoreDeleteRequest storeDeleteRequest = new StoreDeleteRequest(merchantid, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(VendorPage.this);
                        queue.add(storeDeleteRequest);
                        finish();
                    }// 여기에 if문 넣어서 업체 등록 안돼있을시 등록하라고 팝업창 띄워줘야함
                });
        vendordelete.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        vendordelete.setCancelable(false);
        vendordelete.show();
    }

    // 지도 - 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(VendorPage.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(VendorPage.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
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

    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        alertDialog.setTitle("NFC 사용유무셋팅");
        alertDialog.setMessage("NFC 기능을 사용해야 스탬프를 찍을수 있습니다. \n 설정창으로 가시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);

                        // 설정창을 들어갔다 나오면, 태그 등록 알림이 뜨도록 함
                        showTagEnrollDialog();
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void showTagEnrollDialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        alertDialog.setTitle("새 태그 등록");
        alertDialog.setMessage("스탬프 역할을 할 NFC 태그가 필요합니다. \n 등록하시겠습니까?");

        // OK 를 누르게 되면 등록창으로 이동합니다.
        alertDialog.setPositiveButton("등록",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(VendorPage.this, TagEnrollment.class);
                        startActivity(intent);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        stampswitch.setChecked(false);
                    }
                });
        alertDialog.show();
    }

    public void showStampCancle() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        alertDialog.setTitle("스탬프 취소");
        alertDialog.setMessage("스탬프 허용을 취소하시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteStampCode task = new deleteStampCode();
                        task.execute("http://web02.privsw.com/deleteStampCode.php", jid);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        stampswitch.setChecked(true);

                    }
                });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        stampswitch = (Switch) findViewById(R.id.stampswitch);


        //스위치의 체크 이벤트를 위한 리스너 등록

        stampswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // TODO Auto-generated method stub

                if (isChecked && !nfcAdapter.isEnabled() && mystamp.equals("0")) {
                    showSettingsAlert();
                } else if (isChecked && nfcAdapter.isEnabled() && mystamp.equals("0")) {
                    showTagEnrollDialog();
                } else if (!isChecked && !mystamp.equals("0")) {
                    showStampCancle();
                    mystamp = "0";
                }
            }

        });
    }

    // 영업 시작 inserdata 홉 -> 디비
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(VendorPage.this,
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

            String jid = (String) params[1];
            String timestate = (String) params[2];
            String lat = (String) params[3];
            String lon = (String) params[4];

            String serverURL = (String) params[0];
            String postParameters = "jid=" + jid + "&timestate=" + timestate + "&lat=" + lat + "&lon=" + lon;


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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
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

    // 영업 종료 insertdata 홈 -> 디비
    class InsertData2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(VendorPage.this,
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


            String jid = (String) params[1];
            String timestate = (String) params[2];

            String serverURL = (String) params[0];
            String postParameters = "jid=" + jid + "&timestate=" + timestate;


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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
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

    // 홈 -> 디비
    private class GetData extends AsyncTask<String, Void, String> {

        String errorString = null;
        String mJsonString = null;

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
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "jid=" + params[1];

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

        private void showResult() {

            String TAG_JSON = "webnautes";
            String TAG_STAMP = "stamp";


            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String stamp = item.getString(TAG_STAMP);

                    if (!stamp.equals("0")) {

                        mystamp = stamp;
                        stampswitch.setChecked(true);
                    }

                }

            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }
        }

    }


    // 홈 -> 디비
    private class deleteStampCode extends AsyncTask<String, Void, String> {

        String errorString = null;
        String mJsonString = null;

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

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "jid=" + params[1];

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
}