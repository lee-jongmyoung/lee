package com.project.food;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Path;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;



public class ImageRegist extends Activity{



    TextView messageText;

    Button selectGalleryButton;
    Button selectCameraButton;

    Button uploadButton;

    ImageView ivImage;

    int serverResponseCode = 0;

    ProgressDialog dialog = null;

    String upLoadServerUri = null;

    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE=1112;

    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름


    String Jid = Login.merchant;

    /**********  File Path *************/

    String uploadFilePath = "";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보

    String uploadFileName = ""; //전송하고자하는 파일 이름





    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_regist);

        selectGalleryButton = (Button) findViewById(R.id.galleryBtn); //이름 그대로, 갤러리 버튼.

        selectCameraButton = (Button) findViewById(R.id.cameraBtn); //이름 그대로, 카메라 촬영 버튼.

        uploadButton = (Button)findViewById(R.id.imageregistBtn);

//        messageText  = (TextView)findViewById(R.id.textView2); //상태를 나타내주는 메시지창.

        ivImage = (ImageView) findViewById(R.id.menuimage); //사진 촬영/ 갤러리 선택시 선택된 이미지를 보여주는 이미지뷰.




        checkVerify(); //권한 체크. 안하면 안되는데 그렇다고 하자니 매번 켜져서 번거롭긴 함.. 우리 어플상에는 이미 다 허용된 권한이므로 볼필요 X

        /************* Php script path ****************/

        upLoadServerUri = "http://web02.privsw.com/InsertPictureTest.php";//서버컴퓨터의 ip주소.


        selectGalleryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGallery();
            }
        });

        selectCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });


        uploadButton.setOnClickListener(new OnClickListener() {

            @Override

            public void onClick(View v) {  //업로드 버튼 클릭시 새로운 스레드를 생성하여 업로드 작업을 진행.
                // 새로운 스레드를 돌리는 이유 : 메인 스레드에서 네트워크 작업하는것을 안드로이드가 허용하지 않음.



                dialog = ProgressDialog.show(ImageRegist.this, "", "Uploading file...", true);



                new Thread(new Runnable() {

                    public void run() {

                        runOnUiThread(new Runnable() {

                            public void run() {

//                                messageText.setText("uploading started.....");

                            }

                        });



                        uploadFile(uploadFilePath + "" + uploadFileName);



                    }

                }).start();

            }

        });


    }



    public int uploadFile(String sourceFileUri) {



        String fileName = sourceFileUri;



        HttpURLConnection conn = null;

        DataOutputStream dos = null;

        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        String jid = Jid;  //위에서 선언해둔 Jid. 일단 값은 임시로 "5"라고 설정해두었음. 이때당시엔 5번이 비어있었거든.

        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(sourceFileUri);  //파일 경로 받은거로 sourceFile을 지정한다.



        if (!sourceFile.isFile()) {  //파일이 존재하지 않으면, 로그와 메시지를 남김.



            dialog.dismiss();  //업로드중..으로 돌고있던 다이얼로그 역시 종료.



            Log.e("uploadFile", "Source File not exist :"

                    +uploadFilePath + "" + uploadFileName);



            runOnUiThread(new Runnable() {

                public void run() {

//                    messageText.setText("Source File not exist :"
//
//                            +uploadFilePath + "" + uploadFileName);

                }

            });



            return 0;



        }

        else

        {


            try {    //이제부턴 파일이 존재한다는 전제 하에 실행됨.



                // open a URL connection to the Servlet

                FileInputStream fileInputStream = new FileInputStream(sourceFile);

                URL url = new URL(upLoadServerUri); //위에서 설정해둔 Uri(URL같은 느낌인듯..)주소인 php파일을 열어 파일을 넣는다.



                // Open a HTTP  connection to  the URL

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");   //만약 자료를 찾는다면 멀티파트 데이터 전송이라고 입력해서 검색할것.
                //php 파일 전송은 전부 멀티파트로 이루어져 있음.
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);  //데이터의 경계를 지정해준다.
                conn.setRequestProperty("uploaded_file", fileName);  //중요한 부분 2. php에서 해당 프로퍼티로 값을 찾아야 하기 때문에,
                //서버에서 들어간 값을 찾을때는 $_FILES['uploaded_file']['tmp_name']등으로 찾는다.
                conn.setRequestProperty("jid",jid);                     //멀티파트에 jid값을 함께 보내기 위해 프로퍼티 설정.






                dos = new DataOutputStream(conn.getOutputStream());  //데이터 쓰기 스트림 오픈. 우리 원래 코드의 OutputStream같은 느낌.

                dos.writeBytes(twoHyphens + boundary + lineEnd);  //모든 멀티파트에 첨부될 데이터는 바운더리로 감싸져야 함.

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""

                        + fileName + "\"" + lineEnd); //첫번째 파트인 이름과 파일 이름을 전송.



                dos.writeBytes(lineEnd);



                // create a buffer of  maximum size

                bytesAvailable = fileInputStream.available();



                bufferSize = Math.min(bytesAvailable, maxBufferSize);

                buffer = new byte[bufferSize];



                // read file and write it into form...

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);



                while (bytesRead > 0) {



                    dos.write(buffer, 0, bufferSize);

                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);



                }



                // send multipart form data necesssary after file data...

                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);        //두번째 파트인 jid값을 첨부. 사실 이게 맞는방법인진 모르겠는데,
                //jid값이 들어가긴 했으니 이게 맞는 방법이겠지.
                dos.writeBytes("Content-Disposition: form-data; name=\"jid\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(jid + lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); //데이터 입력 종료.



                // Responses from the server (code and message)

                serverResponseCode = conn.getResponseCode();

                String serverResponseMessage = conn.getResponseMessage();



                Log.i("uploadFile", "HTTP Response is : "

                        + serverResponseMessage + ": " + serverResponseCode);



                if(serverResponseCode == 200){          //여기서 이 코드가 함정인것이 드러나는데, 일단 서버로부터 잘 받았다는 회신만 뜨면
                    //무조건 파일 전송 성공 취급을 함. 물론 통상적이라면 이게 성공이겠지만,
                    //우리의 문제점은 DB에서(또는 서버에서) 값은 받았는데 넣기 싫어! 라는 느낌이므로,
                    //메세지창 자체에서는 파일 업로드 성공으로 출력된다.



                    runOnUiThread(new Runnable() {

                        public void run() {



                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"

                                    +uploadFileName;



//                            messageText.setText(msg);

                            Toast.makeText(ImageRegist.this, "File Upload Complete.",

                                    Toast.LENGTH_SHORT).show();

                        }

                    });

                }



                //close the streams //

                fileInputStream.close();

                dos.flush();

                dos.close();



                //디버그용 인풋스트림. 원본 파일에는 없었는데, 성공 여부를 체크하기 위해 임의로 추가함. 성능에 지장 없는거 확인했으니 안심하고 ㄱㄱ
                int responseStatusCode = conn.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                }
                else{
                    inputStream = conn.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                sb.toString();  //이부분에 중단점 (왼쪽 447번 되어있는곳 클릭하여 빨간 원 띄우기) shift+F9키로 중단점 디버깅 실행하면,
                //서버에서 처리된 결과값이 sb에 저장되므로 php의 실행 결과를 알수 있다.

                ///



            } catch (MalformedURLException ex) {



                dialog.dismiss();

                ex.printStackTrace();



                runOnUiThread(new Runnable() {  //분명 여기에 php오류를 검출하는 코드가 있는거 같은데, 작동하는걸 본적이 없는거같다..

                    public void run() {

//                        messageText.setText("MalformedURLException Exception : check script url.");

                        Toast.makeText(ImageRegist.this, "MalformedURLException",

                                Toast.LENGTH_SHORT).show();

                    }

                });



                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {



                dialog.dismiss();

                e.printStackTrace();



                runOnUiThread(new Runnable() {  //예외처리 구문 2. 이 메시지가 뜨는 경우 보통 앱 내부에서 뭘 잘못 보냈거나, 에러가 뜬 경우이다.

                    public void run() {

//                        messageText.setText("Got Exception : see logcat ");

                        Toast.makeText(ImageRegist.this, "Got Exception : see logcat ",

                                Toast.LENGTH_SHORT).show();

                    }

                });

                Log.e("server Exception", "Exception : "

                        + e.getMessage(), e);

            }

            dialog.dismiss();

            return serverResponseCode;



        } // End else block

    }
    @TargetApi(Build.VERSION_CODES.M)       //권한 체크. 볼 필요는 아마..없을걸?
    public void checkVerify()
    {

        if (    checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                )
        {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                ;
            }

            requestPermissions(new String[]{Manifest.permission.INTERNET,Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else
        {
            //startApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0)
            {
                for (int i=0; i<grantResults.length; ++i)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }).setCancelable(false).show();

                        return;
                    }
                }
                //Toast.makeText(this, "Succeed Read/Write external storage !", Toast.LENGTH_SHORT).show();
                //startApp();
            }
        }
    }



    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }


    private void selectGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);      //인텐트를 통해 갤러리로 보내버린다.
        intent.setType("image/*");                                                            //갤러리로 바로 가는거같지는 않던데, 그건 잘 모르겠네.
        startActivityForResult(intent, GALLERY_CODE); //선택된 결과값을 가지고 onActivityResult를 실행.
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                case CAMERA_CODE:
                    getPictureForPhoto(); //카메라에서 가져오기
                    break;

                default:
                    break;
            }

        }
    }

    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기


        String[] PathSplit = imagePath.split("/");  //원본 파일에는 없던 내용. 업로드 파일 이름과 경로를 분할하기 위해 스플릿 실행.
        uploadFileName = PathSplit[PathSplit.length-1];
        uploadFilePath = "";
        for (int i = 0; i < PathSplit.length - 1; i++)
        {
            uploadFilePath += PathSplit[i] + "/";
        }

//        messageText.setText("Uploading file path :- '/mnt/sdcard/"+uploadFileName+"'");
        Toast.makeText(ImageRegist.this,uploadFilePath,Toast.LENGTH_SHORT).show();
    }

    private int exifOrientationToDegrees(int exifOrientation) {     //아마..사진 회전시켜야되는 각도를 구하는 코드였을거야. 기억이 안나네.
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {  //위의 값을 받아서 해당 각도만큼 돌린 사진을 올리는거..였을걸?

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    private void getPictureForPhoto() { //사진 촬영을 선택했을시 실행되는 함수.
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath); //currentPhotoPath : 위쪽에서 지정한, 사진을 찍었을시 파일이 보관되는 위치.
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }

        uploadFilePath = currentPhotoPath;      //업로드 파일경로를 사진이 저장된 위치로 설정.
        String[] PathSplit = currentPhotoPath.split("/");  //업로드 경로와 이름을 얻기 위한 스플릿.
        uploadFileName = PathSplit[PathSplit.length-1];
        uploadFilePath = "";
        for (int i = 0; i < PathSplit.length - 1; i++)
        {
            uploadFilePath += PathSplit[i] + "/";
        }

//        messageText.setText("Uploading file path :- '/mnt/sdcard/"+uploadFileName+"'");


        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기

    }

    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }

        }
    }

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"

                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        Toast.makeText(ImageRegist.this, currentPhotoPath, Toast.LENGTH_SHORT).show();


        return storageDir;

    }
}


