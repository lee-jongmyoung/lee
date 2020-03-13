package com.project.food;

import android.app.Application;

public class GlobalValues extends Application {

    private static String data;
    private static boolean StampYN;
    private static String stagId;

    @Override
    public void onCreate() {
        //전역 변수 초기화

        //DB와의 연동이 활성화 된다면, 이 클래스에서 DB로 변수를 쏘아보내고 받는것이 모양은 예쁠거같은데.
        //안되면 말고.
        data = "";
        StampYN = false;
        stagId = "";
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }





    public static String getData()
    {
        return data;
    }
    public static void setData(String newdata)
    {
        data = newdata;
    }

    public static boolean getStampYN() {
        return StampYN;
    }
    public static void setStampYN(Boolean aBoolean) {
        StampYN = aBoolean;
    }

    public static String getStagId()
    {
        return stagId;
    }
    public static void setStagId(String data)
    {
        stagId = data;
    }





}
