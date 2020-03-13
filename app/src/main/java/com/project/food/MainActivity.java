package com.project.food;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    TabHost tabHost;
    FrameLayout frameLayout;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public final String PREFERENCE = "com.studio572.samplesharepreference";
     public ArrayList<String> latestArray = new ArrayList<String>();
    public static Context context;

    ArrayList<String> urlList = new ArrayList<>();  //모든 점포의 섬네일
    ArrayList<String> jidurlList = new ArrayList<>();  //섬네일에 해당하는 점포의 jid
    ArrayList<Bitmap> ThumbnailBitmap = new ArrayList<>();

//    public final String latestjumpo = "al.get(position).jid";

    Bitmap Bitmap1;
    ImageView imageView5;
    boolean bLog = false;

    String [] rejid;

    // 홈 변수 선언
    ArrayAdapter<CharSequence> adspin1, adspin2, adspinner_nav; //어댑터 선언
    GridView gridView;

    // 홈 디비 변수 선언
    private static String TAG = "food";
    private static String IP_ADDRESS = "web02.privsw.com";
    String mJsonString;
    //    public static String [] jjid;
    ArrayList<S> al = new ArrayList<S>();
    ArrayList<B> a2 = new ArrayList<B>();

    // 마이페이지 디비 변수 선언
    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME ="name";
    private static final String TAG_NNAME ="nname";
    private static final String TAG_MERCHANT ="merchant";
    private static final String TAG_Lat = "lat";
    private static final String TAG_Lon = "lon";

    TextView StoreDeleteJidText;

    GoogleMap mMap;

    ArrayList<HashMap<String, String>> mArrayList;
    String mapFlag = "평점순";
    ArrayList <MarkerOptions> MarkerList = new ArrayList<>();

    int timestateBtnCnt = 0;
    String mJsonString1;
    TextView eName, eNName;
    String name, nname;

    Bitmap grade1,grade2,grade3,grade4,grade5,gradex,mylocationicon;

    ArrayList<String> storejid = new ArrayList<>();
    ArrayList<String> storename = new ArrayList<>();
    ArrayList<String> storetimestate = new ArrayList<>();
    ArrayList<String> storemenulist = new ArrayList<>();
    ArrayList <String> storegrade = new ArrayList<>();

    ArrayList<Store> storeArrayList = new ArrayList<>();

    String spinOneFlag = "/store1.php";  //spin2와 spin1의 상호작용을 위한 변수
    Double spinTwoFlag = -1.0; //spin2에서 선택된 거리 필터 변수

    // 마이페이지 변수 선언
    TextView logout, membersecede, vendorpage, vendorregist, stamplist, vendordelete, StampStatus, CurrentTruck ;

    Button SetMyLocation;

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    ArrayList<LatLng> ShopLocations = new ArrayList<>();
    static LatLng myLatLng;

    //지도 변수
    // GPSTracker class
    private GPSInfo gps;
    Button searchBtn;
    EditText jidText;

    //무한스크롤 뷰페이저
    private Context mContext = null;

    private LoopViewPager vp_loopview = null;
    private LoopPagerAdapter mLoopPagerAdapter = null;

    private static Thread thread;
    private static Runnable runnable = null;
    private static boolean Animation = false;
    private static int mPosition = 1;
    protected Handler	handler = null;
    //뷰페이저 변수 끝

    public MainActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 이미지
        ImageView i1 = (ImageView) findViewById(R.id.imageView88) ;
        i1.setImageResource(R.drawable.ic_action_supervised_user_circle);

        ImageView i2 = (ImageView) findViewById(R.id.image1) ;
        i2.setImageResource(R.drawable.ic_action_crop_original);

        ImageView i3 = (ImageView) findViewById(R.id.image2) ;
        i3.setImageResource(R.drawable.ic_action_content_paste);

        ImageView i4 = (ImageView) findViewById(R.id.image3) ;
        i4.setImageResource(R.drawable.ic_action_content_paste);

        ImageView i5 = (ImageView) findViewById(R.id.image1111) ;
        i5.setImageResource(R.drawable.ic_action_local_shipping);

        context = this;

        StoreDeleteJidText = (TextView)findViewById(R.id.jidText1);

        // jidText = (EditText)findViewById(R.id.jidText);

        // 탭 코딩
        tabHost = (TabHost) findViewById(R.id.tabhost1);       //탭호스트
        tabHost.setup();
        frameLayout = (FrameLayout) findViewById(android.R.id.tabcontent);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("1").setContent(R.id.tab1).setIndicator("홈");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("2").setContent(R.id.tab2).setIndicator("지도");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("3").setContent(R.id.tab3).setIndicator("마이페이지");

        tabHost.addTab(tab1);   //탭 1,2,3,4 를 탭호스트에 넣어줌
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            public void onTabChanged(String tabId) {
                if (tabHost.getCurrentTab() == 2) {
                    if(Login.isLogin == false) {
                        show1();
                        eName.setText("");
                        eNName.setText("");
                    }
                }
            }
        });

        // *****홈

        // 사진 슬라이스
        // Fragment로 넘길 Image Resource
        ArrayList<Integer> listImage = new ArrayList<>();
        listImage.add(R.drawable.baenner1);
        listImage.add(R.drawable.stamp);

        mContext = this;
        handler = new Handler();
        vp_loopview = (LoopViewPager)findViewById(R.id.viewPager);
        setViewPagerAdapter( mContext, listImage );

//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

//        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        // ViewPager와  FragmentAdapter 연결


//        viewPager.setAdapter(fragmentAdapter);
//
//        viewPager.setClipToPadding(false);
//        int dpValue = 0;
//        float d = getResources().getDisplayMetrics().density;
//        int margin = (int) (dpValue * d);
//        viewPager.setPadding(margin, 0, margin, 0);
//        viewPager.setPageMargin(margin/2);
//
//        for (int i = 0; i < listImage.size(); i++) {
//            ImageFragment imageFragment = new ImageFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("imgRes", listImage.get(i));
//            imageFragment.setArguments(bundle);
//            fragmentAdapter.addItem(imageFragment);
//            }
//            fragmentAdapter.notifyDataSetChanged();


        // review jid 받아오기
        GetReview task3 = new GetReview();
        task3.execute( "http://" + IP_ADDRESS + "/getreview.php", "");

        //스피너
        final Spinner spin1 = (Spinner) findViewById(R.id.spinner0101);
        final Spinner spin2 = (Spinner) findViewById(R.id.spinner0202);
        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner01, android.R.layout.simple_spinner_dropdown_item);
        adspin2 = ArrayAdapter.createFromResource(this, R.array.spinner02, android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adspin1);
        spin2.setAdapter(adspin2);

        spin1.setSelection(0,false);
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = (String)spin1.getSelectedItem();
                switch (text){
                    case "평점순":
                        al.clear();
                        spinOneFlag = "/gradeview.php";
                        GetData task = new GetData();
                        task.execute( "http://" + IP_ADDRESS + "/gradeview.php", "");
                        break;
                    case "즐겨찾기순":

                        if(Login.isLogin == true) {
//                            for(int i=al.size(); i>0; i--){
//                                al.remove(i);
//                            }
                            al.clear();
                            spinOneFlag = "/store2.php";
                            GetData2 task1 = new GetData2();
                            task1.execute("http://" + IP_ADDRESS + "/store2.php", "");
                            break;
                        }else{
                            show4();
                            spin1.setSelection(0);
                        }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spin2.setSelection(0,false);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = (String)spin2.getSelectedItem();
                al.clear();
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
                GetData task = new GetData();
                task.execute( "http://" + IP_ADDRESS + spinOneFlag, "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //지도 아이콘 세팅
        BitmapDrawable bitmapdraw1=(BitmapDrawable)getResources().getDrawable(R.drawable.grade1);
        Bitmap b1=bitmapdraw1.getBitmap();
        grade1 = Bitmap.createScaledBitmap(b1, 100, 100, false);
        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.grade2);
        Bitmap b2=bitmapdraw2.getBitmap();
        grade2 = Bitmap.createScaledBitmap(b2, 100, 100, false);
        BitmapDrawable bitmapdraw3=(BitmapDrawable)getResources().getDrawable(R.drawable.grade3);
        Bitmap b3=bitmapdraw3.getBitmap();
        grade3 = Bitmap.createScaledBitmap(b3, 100, 100, false);
        BitmapDrawable bitmapdraw4=(BitmapDrawable)getResources().getDrawable(R.drawable.grade4);
        Bitmap b4=bitmapdraw4.getBitmap();
        grade4 = Bitmap.createScaledBitmap(b4, 100, 100, false);
        BitmapDrawable bitmapdraw5=(BitmapDrawable)getResources().getDrawable(R.drawable.grade5);
        Bitmap b5=bitmapdraw5.getBitmap();
        grade5 = Bitmap.createScaledBitmap(b5, 100, 100, false);
        BitmapDrawable bitmapdrawX=(BitmapDrawable)getResources().getDrawable(R.drawable.gradex);
        Bitmap bX=bitmapdrawX.getBitmap();
        gradex = Bitmap.createScaledBitmap(bX, 100, 100, false);
        BitmapDrawable bitmapdrawmylocation=(BitmapDrawable)getResources().getDrawable(R.drawable.gps);
        Bitmap blocation=bitmapdrawmylocation.getBitmap();
        mylocationicon = Bitmap.createScaledBitmap(blocation, 100, 100, false);
        //세팅 끝

        gridView = findViewById(R.id.gridview);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_data, al);
        gridView.setAdapter(customAdapter); //그리드뷰 cumtomAdapter 호출
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, ""+al.get(position).jid +"", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, StoreTab.class);
                intent.putExtra("jid", ""+al.get(position).jid +"");

                Bundle bundle = new Bundle();
                bundle.putString("jid", ""+al.get(position).jid+"");
                bundle.putString("lat",""+al.get(position).lat+"" );
                bundle.putString("lon",""+al.get(position).lon+"" );
                intent.putExtra("Bundle",bundle);
                setResult(1, intent);

                pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                editor = pref.edit();
                editor.putString("jid", al.get(position).jid);
                editor.clear();
                editor.commit();

                if (!latestArray.contains((al.get(position).jid))) {
                    latestArray.add(al.get(position).jid);
                }
                else {
                    latestArray.remove(al.get(position).jid);
                    latestArray.add(al.get(position).jid);
                }
                setStringArrayPref(MainActivity.this, "jid", latestArray);

                startActivity(intent);
            }
        });

        // 디비
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/gradeview.php", "");

        // *****지도

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);

        GetDataForMap taskMap = new GetDataForMap();
        taskMap.execute("");

        gps = new GPSInfo(MainActivity.this);
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            myLatLng = new LatLng(latitude, longitude);
        }
        else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }

        // 도움말
        Button help = (Button)findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Help.class);
                startActivity(intent);
            }
        });

        // 지도 - 영업중
        final Button IsWorking = (Button)findViewById(R.id.isworking);
        IsWorking.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.defaultworking));
        IsWorking.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                timestateBtnCnt++;
                //기본상태 = 0 ; 영업중 = 1; 영업 안함 = 2;
                if(timestateBtnCnt == 1)  // 영업중인 점포 보여주기
                {
                    Toast.makeText(MainActivity.this, "영업중인 점포 보기", Toast.LENGTH_SHORT).show();
                    IsWorking.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.imworking));

                }
                else if (timestateBtnCnt == 2)
                {
                    Toast.makeText(MainActivity.this, "영업중이 아닌 점포 보기", Toast.LENGTH_SHORT).show();
                    IsWorking.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ineedrest));

                }
                else if (timestateBtnCnt == 3)
                {
                    timestateBtnCnt = 0;
                    Toast.makeText(MainActivity.this, "필터 해제", Toast.LENGTH_SHORT).show();
                    IsWorking.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.defaultworking));

                    //기본 맵 출력

                }
                GetDataForMap task = new GetDataForMap();
                task.execute();
            }
        });

        // 지도 - 검색창 버튼
        Button SearchBtn = (Button)findViewById(R.id.searchbtn);
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent map = new Intent(MainActivity.this, SearchMap.class);
                startActivity(map);
            }

        });

        // ******마이페이지

        // 회원 탈퇴 코드
        membersecede = (TextView) findViewById(R.id.membersecede);
        membersecede.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                show2();
            }
        });

        // 스탬프 현황
        stamplist = (TextView) findViewById(R.id.StampStatus);
        stamplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stamplist = new Intent(MainActivity.this, StampView.class);
                startActivity(stamplist);
            }
        });

        // 최근본 점포
        CurrentTruck = (TextView)findViewById(R.id.CurrentTruck);
        CurrentTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent currenttruck = new Intent(MainActivity.this, LatestStore.class);
                startActivity(currenttruck);
            }
        });

        // 마이페이지 디비 코드
        if(Login.idd != null){
            GetDataMenu taskm = new GetDataMenu();
            taskm.execute();
        }

        eName = (TextView)findViewById(R.id.Name);
        eNName= (TextView)findViewById(R.id.Nickname);

        // 상인 페이지로 이동
        Button merchant = (Button)findViewById(R.id.merchant1);
        merchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String merchantid = Login.merchant;
                if(merchantid.equals("23")) {
                    Toast.makeText(getApplicationContext(), "상인등록이 되어있지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Intent merchant = new Intent(MainActivity.this, VendorPage.class);
                    startActivity(merchant);
                }

            }
        });

        // 상인 등록 페이지로 이동
        vendorregist = (TextView) findViewById(R.id.vendorregist);
        vendorregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String merchantid = Login.merchant;
                if(merchantid.equals("23")) {
                    Intent vendorregist = new Intent(MainActivity.this, MemberVendor.class);
                    startActivity(vendorregist);
                }else{
                    Toast.makeText(getApplicationContext(), "상인등록이 이미 되어있습니다..",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    } // onCreate 끝

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

    public static List sortByValue(final Map values){
        List<String> list = new ArrayList();
        list.addAll(values.keySet());

        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                Object v1 = values.get(o1);
                Object v2 = values.get(o2);

                return ((Comparable) v1).compareTo(v2);
            }

        });

        // Collections.reverse(list); // 주석시 오름차순 //이걸 안쓰면 숫자작은거부터, 쓰면 숫자 큰거부터!

        return list;
    }
    // 홈 -> 그리드 뷰
    private class CustomAdapter extends BaseAdapter { //그리드뷰 customAdapter 코딩
        ArrayList<S> al;
//        ArrayList<Id> jjid;
        Context context;
        int layout;
        LayoutInflater inf;
        public CustomAdapter(Context context, int layout, ArrayList<S> al){
            this.context = context;
            this.layout = layout;
            this.al = al;
            inf = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }
        public int getCount() {
            return al.size();
        }

        @Override
        public Object getItem(int i) {

            return al.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if(view == null){
                    view = inf.inflate(layout, null);
                }
            // View view1 = getLayoutInflater().inflate(R.layout.row_data,null);
            TextView id = view.findViewById(R.id.id);
            TextView name = view.findViewById(R.id.griddata1);
            TextView distance = view.findViewById(R.id.griddata2);
            TextView grade = view.findViewById(R.id.grade);
            ImageView image = view.findViewById(R.id.imageView);
            ImageView star = view.findViewById(R.id.star);

            // 이미지 보여주기
            // image1 = (ImageView)findViewById(R.id.imageView2);

            S m = al.get(i);
            image.setImageBitmap(m.Bitmap1);
            id.setText(m.jid);
            name.setText(m.jname);
            grade.setText(m.grade);
            distance.setText(setmdistance(m.lat,m.lon));
            star.setImageResource(m.img2);

            return view;
        }
    }
    class S{
        String jid="";
        String jname ="";
        Bitmap Bitmap1;
        int img2;
        String grade="";
        Double lat;
        Double lon;
        public S(String jid, String jname, Bitmap Bitmap1, int img2, String grade, String lat, String lon) {
            super();
            this.jid = jid;
            this.jname = jname;
            this.Bitmap1 = Bitmap1;
            this.img2 = img2;
            this.grade = grade;
            this.lat = Double.parseDouble(lat);
            this.lon = Double.parseDouble(lon);
        }
    }

    class B{
        String jid="";
        String jname ="";
        Bitmap Bitmap1;
        int img2;
        String grade="";
        public B(String jid, String jname, Bitmap Bitmap1, int img2, String grade){
            super();
            this.jid=jid;
            this.jname=jname;
            this.Bitmap1=Bitmap1;
            this.img2=img2;
            this.grade=grade;
        }
    }

    public String setmdistance(double lat, double lon)
    {
        LatLng mlatlon = new LatLng(lat,lon);
        double mdistance;
        if (myLatLng != null) {
            mdistance = getDistance(myLatLng, mlatlon); //m단위로 거리계산
        }
        else
            mdistance = 0;
        if (mdistance > 1000.0)
            return String.format("%.2f",mdistance / 1000) + "km";
        else
            return String.format("%.0f",mdistance) + "m";
    }

    // review 개수 받아오기
    private class GetReview extends AsyncTask<String, Void, String>{

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

            }
            else {

                mJsonString = result;
                showResult3();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

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

    private void showResult3(){

        String TAG_JSON="webnautes";
        String TAG_JID = "jid";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                rejid = new String[jsonArray.length()];
                rejid [i] = item.getString(TAG_JID);
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    // 홈 - 디비
    private class GetData extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

            }
            else {
                mJsonString = result;
                showResult();


                    GetURL taskURL = new GetURL();
                    taskURL.execute("http://web02.privsw.com/GetAllPictureURL.php");

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

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
        String TAG_JID = "jid";
        String TAG_JNAME = "jname";
        String TAG_GRADE ="grade";
        String TAG_LAT = "lat";
        String TAG_LON = "lon";
        String TAG_PICTURE = "picture";

        ShopLocations.clear();
        storegrade.clear();
        storejid.clear();
        storename.clear();

        storeArrayList.clear();

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                final String jid = item.getString(TAG_JID);
                String jname = item.getString(TAG_JNAME);
                String grade = item.getString(TAG_GRADE);

                String lat;
                String lon;
                if (item.getString(TAG_LAT) != null && item.getString(TAG_LON) != null)
                {
                    lat = item.getString(TAG_LAT);
                    lon = item.getString(TAG_LON);
                }
                else
                {
                    lat = "0.0000";
                    lon = "0.0000";
                }
                LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                Double shopDistance = getDistance(myLatLng, shopLatLng);

                if (shopDistance < spinTwoFlag)  //spin2에서 고른 거리보다 작으면 데이터 저장
                {
                    al.add(new S(""+jid+"",""+jname+"", Bitmap1, R.drawable.ic_action_grade,""+grade+"",""+lat+"",""+lon+""));
                    storeArrayList.add(new Store(jid,jname,grade,lat,lon));
                    ShopLocations.add(shopLatLng);
                    storegrade.add(grade);
                    storejid.add(jid);
                    storename.add(jname);
                }
                else if (spinTwoFlag == -1.0) //필터 off
                {
                    al.add(new S(""+jid+"",""+jname+"", Bitmap1, R.drawable.ic_action_grade,""+grade+"",""+lat+"",""+lon+""));
                    storeArrayList.add(new Store(jid,jname,grade,lat,lon));
                    ShopLocations.add(shopLatLng);
                    storegrade.add(grade);
                    storejid.add(jid);
                    storename.add(jname);
                }
                else //아니라면 건너뜀
                {continue;}

                gridView = findViewById(R.id.gridview);
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_data, al);
                gridView.setAdapter(customAdapter);

//                TextView id = (TextView) findViewById(R.id.id);
//                TextView name = (TextView) findViewById(R.id.griddata1);
//                TextView gra = (TextView) findViewById(R.id.grade);
//
//                  id.setText(jid);
//                name.setText(storename[i]);
//                gra.setText(storegrade[i]);

            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    // 홈 - 평점이 0일 때 디비
    private class GetData3 extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

            }
            else {
                mJsonString = result;
                showResult5();

                GetURL taskURL = new GetURL();
                taskURL.execute("http://web02.privsw.com/GetAllPictureURL.php");

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

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

    private void showResult5(){

        String TAG_JSON="webnautes";
        String TAG_JID = "jid";
        String TAG_JNAME = "jname";
        String TAG_LAT = "lat";
        String TAG_LON = "lon";
        String TAG_PICTURE = "picture";

        ShopLocations.clear();
        storegrade.clear();
        storejid.clear();

        storeArrayList.clear();

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                final String jid = item.getString(TAG_JID);
                String jname = item.getString(TAG_JNAME);

                String lat;
                String lon;
                if (item.getString(TAG_LAT) != null && item.getString(TAG_LON) != null)
                {
                    lat = item.getString(TAG_LAT);
                    lon = item.getString(TAG_LON);
                }
                else
                {
                    lat = "0.0000";
                    lon = "0.0000";
                }
                LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                Double shopDistance = getDistance(myLatLng, shopLatLng);
                ShopLocations.add(shopLatLng);
                storegrade.add("0.00");
                storejid.add(jid);
                storename.add(jname);

                if (shopDistance < spinTwoFlag)  //spin2에서 고른 거리보다 작으면 데이터 저장
                {
                    al.add(new S(""+jid+"",""+jname+"", Bitmap1, R.drawable.ic_action_grade,""+"0.00"+"",""+lat+"",""+lon+""));
                    storeArrayList.add(new Store(jid,jname,"0.00",lat,lon));
                }
                else if (spinTwoFlag == -1.0) //필터 off
                {
                    al.add(new S(""+jid+"",""+jname+"", Bitmap1, R.drawable.ic_action_grade,""+"0.00"+"",""+lat+"",""+lon+""));
                    storeArrayList.add(new Store(jid,jname,"0.00",lat,lon));
                }
                else //아니라면 건너뜀
                {continue;}

                gridView = findViewById(R.id.gridview);
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_data, al);
                gridView.setAdapter(customAdapter);

            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    private class GetData2 extends AsyncTask<String, Void, String>{

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
                showResult2();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = Login.idd;

            String serverURL = "http://web02.privsw.com/store2.php";
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

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult2(){

        String TAG_JSON="webnautes";
        String TAG_JID = "jid";
        String TAG_JNAME = "jname";
        String TAG_GRADE ="grade";
        String TAG_LAT = "lat";
        String TAG_LON = "lon";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                final String jid = item.getString(TAG_JID);
                String jname = item.getString(TAG_JNAME);
                String grade = item.getString(TAG_GRADE);

                String lat;
                String lon;
                if (item.getString(TAG_LAT) != null && item.getString(TAG_LON) != null)
                {
                    lat = item.getString(TAG_LAT);
                    lon = item.getString(TAG_LON);
                }
                else
                {
                    lat = "0.0000";
                    lon = "0.0000";
                }
                LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                Double shopDistance = getDistance(myLatLng, shopLatLng);
                ShopLocations.add(shopLatLng);
                storegrade.add(grade);
                storejid.add(jid);
                storename.add(jname);

                if (shopDistance < spinTwoFlag)  //spin2에서 고른 거리보다 작으면 데이터 저장
                {
                    al.add(new S(""+jid+"",""+jname+"", Bitmap1, R.drawable.ic_action_grade,""+grade+"",""+lat+"",""+lon+""));
                    storeArrayList.add(new Store(jid,jname,grade,lat,lon));
                }
                else if (spinTwoFlag == -1.0) //필터 off
                {
                    al.add(new S(""+jid+"",""+jname+"", Bitmap1, R.drawable.ic_action_grade,""+grade+"",""+lat+"",""+lon+""));
                    storeArrayList.add(new Store(jid,jname,grade,lat,lon));
                }
                else //아니라면 건너뜀
                {continue;}

                gridView = findViewById(R.id.gridview);
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.row_data, al);
                gridView.setAdapter(customAdapter);
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    private class GetData4 extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

            }
            else {
                mJsonString = result;
                showResult4();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

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

    private void showResult4(){

        String TAG_JSON="webnautes";
        String TAG_JID = "jid";
        String TAG_PICTURE = "picture";

        ShopLocations.clear();
        storegrade.clear();

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);


                String jid = item.getString(TAG_JID);
                String picture = item.getString(TAG_PICTURE);

                byte[] picturebytes = Base64.decode(picture, Base64.NO_PADDING);
                ByteArrayInputStream inStream = new ByteArrayInputStream(picturebytes);
                Bitmap bitmap1 = BitmapFactory.decodeStream(inStream);

                imageView5.setImageBitmap(bitmap1);
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
    // 홈 - 디비 끝

    // 홈 - 사진 슬라이스
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
    }


    // 지도 - 스피너

    // 지도 - 영업중 버튼

    // 지도 - 검색

    // 지도 - 권한 있는지 체크

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 지도 - 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeImageThread();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    // 지도 -  지도 초기 설정
    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        final ArrayList<LatLng> MyLocations = new ArrayList<LatLng>();

        if (myLatLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13));  //카메라를 해당 좌표로 이동합니다.
        }

        SetMyLocation = (Button)findViewById(R.id.SetMyLocationBtn);

        // GPS 정보를 보여주기 위한 이벤트 클래스 등록
        SetMyLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // 권한 요청을 해야 함
                if (!isPermission) {
                    callPermission();
                    return;
                }

                gps = new GPSInfo(MainActivity.this);

                // GPS 사용유무 가져오기

                if (gps.isGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    myLatLng = new LatLng(latitude,longitude);

                    //지오코딩 주소
                    String Myaddress = getCurrentAddress(latitude, longitude);

                    Toast.makeText(
                            MainActivity.this.getApplicationContext(),
                            "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude + "\n주소" + Myaddress,
                            Toast.LENGTH_LONG).show();

                    MyLocations.clear();
                    mMap.clear();

                        if (mapFlag.equals("평점순")) {  MapDrawGrade(); }
                        else if (mapFlag.equals("즐겨찾기순")){MapDrawLike();}
                        else if (mapFlag.equals("메뉴별")) {MapDrawMenu();}
                        //ShopLocations배열의 데이터를 하나하나 마커로 만들어줍니다.


                    MyLocations.add(myLatLng);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));  //카메라를 해당 좌표로 이동합니다.
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));  //카메라를 해당 배율만큼 확대합니다.
                    Bitmap bm = mylocationicon;
                    AddMarkers(myLatLng, mMap,BitmapDescriptorFactory.fromBitmap(mylocationicon));

                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }
            }

        });

        callPermission();  // 권한 요청을 해야 함

        //지도 스피너 초기화
        final Spinner spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        adspinner_nav = ArrayAdapter.createFromResource(this, R.array.spinner_nav, android.R.layout.simple_spinner_dropdown_item);
        spinner_nav.setAdapter(adspinner_nav);

        // 스피너 이벤트 처리 ( 스피너 = setOnItemSelectedListener 이용)
        spinner_nav.setSelection(0,false);
        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "" + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                if (parent.getItemAtPosition(position).equals("평점순"))
                {
                    mapFlag = "평점순";
                    mMap.clear();

                    GetDataForMap task = new GetDataForMap();
                    task.execute();


                    mapFlag = "평점순";

                    MapDrawGrade();
                }
                else  if (parent.getItemAtPosition(position).equals("즐겨찾기순"))
                {
                    mMap.clear();
                    mapFlag = "즐겨찾기순";
                    //로그인이 안되어있다면 로그인하라고 다이얼로그 출력. 없으니까 임시값 설정.
                    if(Login.isLogin == false) {
                        Toast.makeText(getApplicationContext(), "즐겨찾기는 로그인 후 이용가능합니다.",
                                Toast.LENGTH_SHORT).show();
                        spinner_nav.setSelection(0);

                    }else {
                        Toast.makeText(MainActivity.this,"즐겨찾기순",Toast.LENGTH_SHORT).show();
                        GetDataLike liketask = new GetDataLike();
                        String FakeID = Login.idd;
                        liketask.execute("" + FakeID + "", "true"); //ID값이 있으면(로그인이 되어있으면) ID값, truefalse값을 추가해서 전송.
                        // params[0]에 저장됨. getlikemap
                    }
                }
                else  if (parent.getItemAtPosition(position).equals("메뉴별"))
                {
                    Toast.makeText(MainActivity.this,"메뉴별",Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    mapFlag = "메뉴별";

                    GetDataForMenu task = new GetDataForMenu();
                    task.execute();

                    //1. Select jid FROM store 로 모든 점포의 jid를 가져온다. 변수에 박아둔다.
                    //2. select menu(테이블명) from menu(DB명) where jid = (변수에 넣은 jid를 쏜값)으로 해당 메뉴가 있는 가게를 찾아 띄운다.
                }
                // Toast.makeText(MainActivity.this,mapFlag,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //textView.setText("아무것도 선택되지 않았습니다");
            }
        });

        if (myLatLng != null) {
            MyLocations.add(myLatLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));  //카메라를 해당 좌표로 이동합니다.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));  //카메라를 해당 배율만큼 확대합니다.
            AddMarkers(myLatLng, mMap,BitmapDescriptorFactory.fromBitmap(mylocationicon));
        }

        Intent intentHasData = getIntent();
        Bundle bundleForResult = intentHasData.getBundleExtra("Bundle");
        if(bundleForResult != null)
        {
            tabHost.setCurrentTab(1);

            String jid = bundleForResult.getString("jid");

            String lat = bundleForResult.getString("lat");

            String lon = bundleForResult.getString("lon");

            LatLng ResultStore = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ResultStore, 13));  //카메라를 해당 좌표로 이동합니다.
        }
    }

    public void MapDrawGrade()
    {
        mMap.clear();

        for (int i = 0; i < ShopLocations.size(); i++) {
            if (timestateBtnCnt == 0 || (timestateBtnCnt == 1 && storetimestate.get(i).equals("true")) || (timestateBtnCnt == 2 && storetimestate.get(i).equals("false"))) {
                if (Double.parseDouble(storegrade.get(i)) >= 5.0)
                    AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.fromBitmap(grade5));
                else if (Double.parseDouble(storegrade.get(i)) >= 4.0 && Double.parseDouble(storegrade.get(i)) < 5.0)
                    AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.fromBitmap(grade4));
                else if (Double.parseDouble(storegrade.get(i)) >= 3.0 && Double.parseDouble(storegrade.get(i)) < 4.0)
                    AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.fromBitmap(grade3));
                else if (Double.parseDouble(storegrade.get(i)) >= 2.0 && Double.parseDouble(storegrade.get(i)) < 3.0)
                    AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.fromBitmap(grade2));
                else if (Double.parseDouble(storegrade.get(i)) >= 1.0 && Double.parseDouble(storegrade.get(i)) < 2.0)
                    AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.fromBitmap(grade1));
                else
                    AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.fromBitmap(gradex));
            }
            else {continue;}

        }
    }

    public void MapDrawLike()
    {
        mMap.clear();

        for (int i = 0; i < ShopLocations.size(); i++) {
            if (timestateBtnCnt == 0 || (timestateBtnCnt == 1 && storetimestate.get(i).equals("true")) || (timestateBtnCnt == 2 && storetimestate.get(i).equals("false"))) {
                mapDraw(mMap);
            }
            else {continue;}

        }
    }

    public void MapDrawMenu()
    {
        mMap.clear();

        for (int i = 0; i < ShopLocations.size(); i++) {
            if (timestateBtnCnt == 0 || (timestateBtnCnt == 1 && storetimestate.get(i).equals("true")) || (timestateBtnCnt == 2 && storetimestate.get(i).equals("false"))) {
                if(storemenulist.get(i).contains("떡볶이"))
                { AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));}
                else if (storemenulist.get(i).contains("호떡"))
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));}
                else if (storemenulist.get(i).contains("탕후루"))
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));}
                else if (storemenulist.get(i).contains("붕어빵"))
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));}
                else if (storemenulist.get(i).contains("타코야끼"))
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));}
                else if (storemenulist.get(i).contains("계란빵"))
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));}
                else if (storemenulist.get(i).contains("와플"))
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));}
                else if (storemenulist.get(i).contains("오뎅"))
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));}
                else if (storemenulist.get(i).contains("닭꼬치"))
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));}
                else
                {AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));}
            }
            else {continue;}

        }
    }

    public void mapDraw(GoogleMap mMap)
    {
        mMap.clear();
        for (int i = 0; i < ShopLocations.size(); i++) {
            AddMarkers(ShopLocations.get(i), storename.get(i), storegrade.get(i), mMap, storejid.get(i));
            //ShopLocations배열의 데이터를 하나하나 마커로 만들어줍니다.
        }
    }
    public void mapDraw(GoogleMap mMap, String MarkerTitle, String Markersnippet)
    {
        mMap.clear();
        for (int i = 0; i < ShopLocations.size(); i++) {
            AddMarkers(ShopLocations.get(i), MarkerTitle, Markersnippet, mMap, storejid.get(i));
            //ShopLocations배열의 데이터를 하나하나 마커로 만들어줍니다.
        }
    }

    // 지도 - 마커
    void AddMarkers (LatLng Points, String title, String snippet, final GoogleMap map, String storejid){  //마커 추가 함수입니다.
        MarkerOptions NewPoint = new MarkerOptions(); //마커의 위치, 타이틀, 설명등을 추가해서 마커를 생성합니다. 아이콘도 필요하면 바꿀수 있겠지만,
        NewPoint.position(Points)                     // 여기서 바꿀 필요는 없겠지.(필터별로 바꿔야 하므로 여기서 마커 아이콘을 만들어봤자..)
                .title(title)
                .snippet(snippet);
        Marker marker = map.addMarker(NewPoint);
        marker.setTag(storejid);
        map.setOnInfoWindowClickListener(infoWindowClickListener);
        map.setOnMarkerClickListener(markerClickListener);

    }

    void AddMarkers (LatLng Points, final GoogleMap map, BitmapDescriptor Icon){  //마커 추가 함수입니다.
        MarkerOptions NewPoint = new MarkerOptions();
        NewPoint.position(Points);
        NewPoint.icon(Icon);
        map.addMarker(NewPoint);

    }

    // 지도 - 마커
    void AddMarkers (LatLng Points, String title, String snippet, final GoogleMap map, String storejid,BitmapDescriptor Icon){  //마커 추가 함수입니다.
        MarkerOptions NewPoint = new MarkerOptions();
        NewPoint.position(Points)
                .title(title)
                .snippet(snippet);

        NewPoint.icon(Icon);
        Marker marker = map.addMarker(NewPoint);
        marker.setTag(storejid);
        map.setOnInfoWindowClickListener(infoWindowClickListener);
        map.setOnMarkerClickListener(markerClickListener);

    }

    // 지도 - 정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String markerTag = (String)marker.getTag();


            pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("jid", markerTag);
            editor.clear();
            editor.commit();

            if (!latestArray.contains((markerTag))) {
                latestArray.add(markerTag);
            }
            else {
                latestArray.remove(markerTag);
                latestArray.add(markerTag);
            }
            setStringArrayPref(MainActivity.this, "jid", latestArray);

            Intent intent = new Intent(MainActivity.this, StoreTab.class);
            Bundle bundle = new Bundle();
            bundle.putString("jid", ""+markerTag+"");
            bundle.putString("lat",""+marker.getPosition().latitude+"" );
            bundle.putString("lon",""+marker.getPosition().longitude+"" );
            intent.putExtra("Bundle",bundle);
            setResult(1, intent);

            startActivity(intent);
        }
    };

    // 지도 - 마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String markerId = marker.getId();
            //선택한 타겟위치
            LatLng location = marker.getPosition();
            if (myLatLng != null)
            {
                double distanceKM = getDistance(location, myLatLng) / 1000;

                String Myaddress = getCurrentAddress(location.latitude, location.longitude);
                Toast.makeText(MainActivity.this,  "(" + Myaddress+ ")" + "\n 거리 : " + String.format("%.2f",distanceKM) + "km", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    };

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

    // 홈 -> 그리드 뷰
//    private class CustomAdapter extends BaseAdapter {            //그리드뷰 customAdapter 코딩
//        public int getCount() {
//            return storeimage.length;
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            View view1 = getLayoutInflater().inflate(R.layout.row_data,null);
//            TextView name = view1.findViewById(R.id.griddata1);
//            TextView distance = view1.findViewById(R.id.griddata2);
//            TextView grade = view1.findViewById(R.id.grade);
//            ImageView image = view1.findViewById(R.id.imageView);
//
//
//            name.setText(storename[i]);
//            distance.setText(storedistance[i]);
//            grade.setText(storegrade[i]);
//            image.setImageResource(storeimage[i]);
//            return view1;
//        }
//    }

    //마이페이지 탭 로그인 여부 확인
    void show1() {
        AlertDialog.Builder mypage = new AlertDialog.Builder(this);
        mypage.setTitle("로그인 확인");
        mypage.setMessage("마이페이지는 로그인하셔야 이용가능합니다.\n로그인 하시겠습니까? ");
        mypage.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 디비에서 회원 정보 삭제는 코드 작성해야함
                        finish();
                        Intent intent = new Intent(MainActivity.this,Login.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
        mypage.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
        mypage.setCancelable(false);
        mypage.show();
    }
    // 마이페이지 -> 회원 탈퇴
    void show2() {
        AlertDialog.Builder membersecede = new AlertDialog.Builder(this);
        membersecede.setTitle("회원 탈퇴");
        membersecede.setMessage("회원 탈퇴를 하시겠습니까?");
        membersecede.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 디비에서 회원 정보 삭제는 코드 작성해야함
                        if(Login.isLogin == true) {
                            Intent intent = getIntent();
                            final String idname = intent.getStringExtra("idd");
                            final String pass = intent.getStringExtra("pass");
                            final String name = intent.getStringExtra("name");
                            Intent i = new Intent(MainActivity.this,DeleteActivity.class);
                            i.putExtra("id", idname);
                            i.putExtra("pass", pass);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "로그인 후 이용하세요.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
        membersecede.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        membersecede.setCancelable(false);
        membersecede.show();
    }

    void show4() {
        AlertDialog.Builder review = new AlertDialog.Builder(this);
        review.setTitle("로그인확인");
        review.setMessage("로그인 후 이용할 수 있습니다.");
        review.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 디비에서 회원 정보 삭제는 코드 작성해야함
                        dialog.dismiss();
                    }
                });
        review.show();
    }

    // 마이페이지 디비 코드
    private class GetDataMenu extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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

                mJsonString1 = result;
                showResultMenu();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = Login.idd;

            String serverURL = "http://web02.privsw.com/mypage.php";
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

        private void showResultMenu() {
            try {
                JSONObject jsonObject = new JSONObject(mJsonString1);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String id = item.getString(TAG_ID);

                    name = item.getString(TAG_NAME);
                    nname = item.getString(TAG_NNAME);
                    // String merchant = item.getString(TAG_MERCHANT);
                    String idd = Login.idd;
                    String passs = Login.passs;

                    eName.setText(name);
                    eNName.setText(nname);


                }

            } catch (JSONException e) {

                Log.d(TAG, "showResultMY : ", e);
            }
        }

    }

    //지도전용 데이터 불러오기

    private class GetDataForMap extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
                showResultForMap();
                MapDrawGrade();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://web02.privsw.com/storeview.php";
            String postParameters = "jid="+"";

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

        private void showResultForMap() {

            String TAG_JSON="webnautes";
            String TAG_JID = "jid";
            String TAG_JNAME ="jname";
            String TAG_GRADE = "grade";
            String TAG_TIMESTATE = "timestate";
            String TAG_LAT = "lat";
            String TAG_LON = "lon";

            ShopLocations.clear();
            storegrade.clear();
            storejid.clear();
            storename.clear();
            storetimestate.clear();

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String jid = item.getString(TAG_JID);

                    String jname = item.getString(TAG_JNAME);
                    String grade = item.getString(TAG_GRADE);
                    String timestate = item.getString(TAG_TIMESTATE);
                    String lat;
                    String lon;

                    if (item.getString(TAG_LAT) != null && item.getString(TAG_LON) != null)
                    {
                        lat = item.getString(TAG_LAT);
                        lon = item.getString(TAG_LON);
                    }
                    else
                    {
                        lat = "0.0000";
                        lon = "0.0000";
                    }
                    LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    ShopLocations.add(shopLatLng);
                    storegrade.add(grade);
                    storejid.add(jid);
                    storename.add(jname);
                    storetimestate.add(timestate);

                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

///지도 데이터 불러오기 끝

    //즐겨찾기 데이터 불러오기

    private class GetDataLike extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
                showResultForLike();
                MapDrawLike();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String id = params[0];
            String likes = params[1];

            String serverURL = "http://web02.privsw.com/getlikemap.php";
            String postParameters = "id="+id+"&likes="+likes;

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

        private void showResultForLike() {

            String TAG_JSON="webnautes";
            String TAG_JID = "jid";
            String TAG_JNAME ="jname";
            String TAG_GRADE = "grade";
            String TAG_TIMESTATE = "timestate";
            String TAG_LAT = "lat";
            String TAG_LON = "lon";

            ShopLocations.clear();
            storejid.clear();
            storegrade.clear();
            storename.clear();
            storetimestate.clear();

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String jid = item.getString(TAG_JID);
                    String jname = item.getString(TAG_JNAME);
                    String grade = item.getString(TAG_GRADE);
                    String timestate = item.getString(TAG_TIMESTATE);
                    String lat;
                    String lon;

                    if (item.getString(TAG_LAT) != null && item.getString(TAG_LON) != null)
                    {
                        lat = item.getString(TAG_LAT);
                        lon = item.getString(TAG_LON);
                    }
                    else
                    {
                        lat = "0.0000";
                        lon = "0.0000";
                    }
                    LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    ShopLocations.add(shopLatLng);
                    storejid.add(jid);
                    storename.add(jname);
                    storegrade.add(grade);
                    storetimestate.add(timestate);

                }


            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    //즐겨찾기 데이터 불러오기 끝

    //메뉴데이터 불러오기

    //지도전용 데이터 불러오기

    private class GetDataForMenu extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
                showResultForMenu();
                MapDrawMenu();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://web02.privsw.com/getmenumap.php";
            String postParameters = "jid="+"";

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

        private void showResultForMenu() {

            String TAG_JSON="webnautes";
            String TAG_JID = "jid";
            String TAG_JNAME ="jname";
            String TAG_GRADE = "grade";
            String TAG_TIMESTATE = "timestate";
            String TAG_LAT = "lat";
            String TAG_LON = "lon";
            String TAG_MENU = "menu";
            String TAG_PRICE = "price";

            ShopLocations.clear();
            storegrade.clear();
            storejid.clear();
            storename.clear();
            storemenulist.clear();

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String jid = item.getString(TAG_JID);

                    String jname = item.getString(TAG_JNAME);
                    String grade = item.getString(TAG_GRADE);
                    String timestate = item.getString(TAG_TIMESTATE);
                    String menu = item.getString(TAG_MENU);
                    String price = item.getString(TAG_PRICE);
                    String lat;
                    String lon;

                    if (item.getString(TAG_LAT) != null && item.getString(TAG_LON) != null)
                    {
                        lat = item.getString(TAG_LAT);
                        lon = item.getString(TAG_LON);
                    }
                    else
                    {
                        lat = "0.0000";
                        lon = "0.0000";
                    }
                    LatLng shopLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));

                    if (!storejid.contains(jid)) {
                        ShopLocations.add(shopLatLng);
                        storegrade.add(grade);
                        storejid.add(jid);
                        storename.add(jname);
                        storemenulist.add(menu);
                    }

                }

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
        // 메뉴버튼을 눌렀을 때 보여줄 menu 에 대해서 정의
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("test", "onCreateOptionsMenu - 최초 메뉴키를 눌렀을 때 호출됨");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(Login.isLogin==true){ // 로그인 한 상태: 로그인은 안보이게, 로그아웃은 보이게
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
        }else if (Login.isLogin == false){ // 로그 아웃 한 상태 : 로그인 보이게, 로그아웃은 안보이게
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
        }

        bLog = !bLog;   // 값을 반대로 바꿈

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴의 항목을 선택(클릭)했을 때 호출되는 콜백메서드
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d("test", "onOptionsItemSelected - 메뉴항목을 클릭했을 때 호출됨");

        int id = item.getItemId();

        if(id == R.id.menu_login) {

            Intent it = new Intent(MainActivity.this, Login.class);
            startActivity(it);

            return true;
        }
        if(id == R.id.menu_logout) {
                new AlertDialog.Builder(this/* 해당 액티비티를 가르킴 */)
                        .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Login.isLogin = false;
//                                textid.setText("");
                                eName.setText("");
                                eNName.setText("");
                                Intent i = new Intent(MainActivity.this/*현재 액티비티 위치*/ , MainActivity.class/*이동 액티비티 위치*/);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .show();

                return true;

        }
        if(id == R.id.memberregist){
            Intent it = new Intent(MainActivity.this, Member.class);
            startActivity(it);
            return true;
        }
        if(id == R.id.item) {
            Intent it = new Intent(MainActivity.this, MainSearch.class);
            startActivity(it);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 사진 전부 들고와서 초기화
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
                showResultURL();
                ThreadStart();

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(1000);
                httpURLConnection.setConnectTimeout(1000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


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

    private void showResultURL(){

        String TAG_JSON="webnautes";
        String TAG_URL = "url";

        String jid = null;
        String postjid = null;

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            if (urlList.size() == 0 && jidurlList.size() == 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String url = item.getString(TAG_URL);

                    String[] parseurl = url.split("/");
                    jid = parseurl[1];

                    if (!jid.equals(postjid)) {
                        urlList.add(url);
                        jidurlList.add(jid);
                        postjid = jid;
                    }

                }
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    public void ThreadStart()
    {
        //Thread t = new Thread(Runnable 객체를 만든다);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다

                if (ThumbnailBitmap.size() < al.size())
                {
                    ThumbnailBitmap.clear();
                    for (int i = 0; i < al.size(); i++) {
                        ThumbnailBitmap.add(null);
                    }
                }

                // TODO Auto-generated method stub
                try {

                    // 걍 외우는게 좋다 -_-;
                        for (int i = 0; i < urlList.size();i++) {
    //                    final ImageView iv = (ImageView)findViewById(R.id.imageView1);
                            URL url = new URL("http://web02.privsw.com/" + urlList.get(i));
                            InputStream is = url.openStream();

                            String[] urlParse = urlList.get(i).split("/");
                            String jid = urlParse[1];

                            int reqWidth = 126;
                            int reqHeight = 168;

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeStream(is, null, options);
                            options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
                            options.inJustDecodeBounds = false;
                            is.close();
                            is = url.openStream();
                            Bitmap bm = BitmapFactory.decodeStream(is, null, options);
                            is.close();
                            //그리드뷰 i번의 jid => storeArrayList.get(i).jid 또는 storejid.indexOf(jid)

                            if (ThumbnailBitmap.get(i) == null)
                            {
                                if (bm != null)
                                {
                                    ThumbnailBitmap.set(i,bm);
                                }
                                else
                                {
                                    Drawable drawable = getResources().getDrawable(R.drawable.foodtruck22);
                                    Bitmap bitmapicon = ((BitmapDrawable)drawable).getBitmap();

                                    ThumbnailBitmap.set(i,bitmapicon);
                                }
                            }

                        }

                    for (int i = 0; i < al.size(); i++) {

                        int ThumbnailNum = jidurlList.indexOf(storeArrayList.get(i).jid);

                        if(ThumbnailBitmap.get(i) ==  null || ThumbnailNum == -1)
                        {
                            Drawable drawable = getResources().getDrawable(R.drawable.foodtruck22);
                            Bitmap bitmapicon = ((BitmapDrawable)drawable).getBitmap();

                            ThumbnailBitmap.set(i,bitmapicon);
                            al.set(i, new S(storeArrayList.get(i).jid, storeArrayList.get(i).jname, ThumbnailBitmap.get(i), R.drawable.ic_action_grade, storeArrayList.get(i).grade, storeArrayList.get(i).lat, storeArrayList.get(i).lon));

                        }

                        if(ThumbnailNum != -1)
                        {
                            al.set(i, new S(storeArrayList.get(i).jid, storeArrayList.get(i).jname, ThumbnailBitmap.get(jidurlList.indexOf(storeArrayList.get(i).jid)), R.drawable.ic_action_grade, storeArrayList.get(i).grade, storeArrayList.get(i).lat, storeArrayList.get(i).lon));
                        }
                    }
                }

                 catch(Exception e){
                    Log.d("error : ",e.toString());

                 }
            }


        });

        t.start();  //미리보기 임시 변경. 최종본에서는 주석 해제 바람.

//        for (int i = 0; i < ThumbnailBitmap.size(); i++)
//        {
//                //Bitmap1 = 대표이미지. 가장 처음 올라간 이미지.
//                al.set(i, new S(storeArrayList.get(i).jid, storeArrayList.get(i).jname, ThumbnailBitmap.get(i), R.drawable.ic_action_grade, storeArrayList.get(i).grade, storeArrayList.get(i).lat, storeArrayList.get(i).lon));
//
//        }

    }

    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight){

        final int height = options.outHeight;

        final int width = options.outWidth;

        int inSampleSize = 1;

        if( height > reqHeight || width > reqWidth){

            final int heightRatio = Math.round((float)height / (float)reqHeight);

            final int widthRatio = Math.round((float)width / (float)reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }

        return inSampleSize;

    }



    private ArrayList<Integer> testData(){

        ArrayList<Integer> data = new ArrayList<Integer>();
        data.add(Color.BLACK);	// 1
        data.add(Color.BLUE);	// 2
        data.add(Color.GREEN);	// 3

        return data;
    }

    /**
     * 뷰 페이저 어뎁터
     * @param context
     * @param data
     */
    private void setViewPagerAdapter(Context context, ArrayList<Integer> data){

        if(null != data && 0 < data.size()){
            if(null == mLoopPagerAdapter){
                mLoopPagerAdapter = new LoopPagerAdapter(context, data);
                vp_loopview.setAdapter( mLoopPagerAdapter );
                vp_loopview.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {

                        mPosition = position;

                        Log.i("TEST","position:::"+position);
                        Log.i("TEST","vp_loopview.getAdapter().getCount():::"+vp_loopview.getAdapter().getCount());

                    }

                    @Override
                    public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                        // TODO Auto-generated method stub

                    }
                });

                runnable = new Runnable() {

                    @Override
                    public void run() {

                        while (Animation) {

                            try {
                                thread.sleep(5000); //뷰페이저 애니메이션 속도 조절
                            } catch (InterruptedException e) {
                                Log.e("TEST","Thread wait error");
                            }

                            handler.post(new Runnable() {
                                public void run() {
                                    vp_loopview.setCurrentItem(mPosition);
                                }
                            });

                            if( mPosition < vp_loopview.getAdapter().getCount() ){
                                mPosition++;
                            } else {
                                mPosition = 0;
                            }

                            Log.i("TEST", "position:::"+mPosition);
                        }
                    }
                };

            } else {
                Toast.makeText(mContext, mContext.getString(R.string.no_data) , Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.no_data) , Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 이미지 스레드 일시정지 메소드
     */
    private void onPauseImageThread(){
        Animation = false;
        if(null != thread){
            thread.interrupt();
            thread = null;
        }
    }

    /**
     * 이미지 스레드 재시작 메소드
     */
    private void onResumeImageThread(){
        Animation = true;
        if(null == thread){
            thread = new Thread(runnable);
            thread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseImageThread();
    }
}