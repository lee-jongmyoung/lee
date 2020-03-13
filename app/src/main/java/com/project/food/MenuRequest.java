package com.project.food;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class MenuRequest extends StringRequest {

    //현재 안드로이드앱을 에뮬레이터로 돌리므로 에뮬레이터가 설치된 서버에 있는 아파치 서버에 접근하려면
    //다음과 같이 10.0.2.2:포트번호 로 접근해야합니다 저는 8080 포트를 써서 다음과 같이 했습니다
    final static private String URL = "http://web02.privsw.com/menu.php";
    private Map<String, String> parameters;



    //생성자
    public MenuRequest(String jid1, String menu1, String price1, String menu2, String price2,
                       String menu3, String price3,String menu4, String price4,String menu5, String price5,Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("jid1", jid1);
        parameters.put("menu1", menu1);
        parameters.put("price1", price1);
        parameters.put("menu2", menu2);
        parameters.put("price2", price2);
        parameters.put("menu3", menu3);
        parameters.put("price3", price3);
        parameters.put("menu4", menu4);
        parameters.put("price4", price4);
        parameters.put("menu5", menu5);
        parameters.put("price5", price5);

    }

    //추후 사용을 위한 부분
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}