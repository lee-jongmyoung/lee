package com.project.food;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchMap extends Activity {

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map);

        searchView = (SearchView)findViewById(R.id.search11);
        searchView.setQueryHint("점포명, 지역, 메뉴를 검색하세요.");
        searchView.setIconifiedByDefault(false);

        // 버튼 선언
        Button h1 = (Button)findViewById(R.id.hash11);  // 붕어빵
        h1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "붕어빵");
                startActivity(i);
            }
        });
        Button h2 = (Button)findViewById(R.id.hash22);  // 닭꼬치
        h2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "닭꼬치");
                startActivity(i);
            }
        });
        Button h3 = (Button)findViewById(R.id.hash33);  // 오뎅
        h3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "오뎅");
                startActivity(i);
            }
        });
        Button h4 = (Button)findViewById(R.id.hash44);  // 계란빵
        h4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "계란빵");
                startActivity(i);
            }
        });
        Button h5 = (Button)findViewById(R.id.hash55);  // 타코야끼
        h5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "타코야키");
                startActivity(i);
            }
        });
        Button h6 = (Button)findViewById(R.id.hash66);  // 떡볶이
        h6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "떡볶이");
                startActivity(i);
            }
        });
        Button h7 = (Button)findViewById(R.id.hash77);  // 호떡
        h7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "호떡");
                startActivity(i);
            }
        });
        Button h8 = (Button)findViewById(R.id.hash88);  // 핫도그
        h8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "핫도그");
                startActivity(i);
            }
        });
        Button h9 = (Button)findViewById(R.id.hash99);  // 토스트
        h9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "토스트");
                startActivity(i);
            }
        });
        Button h10 = (Button)findViewById(R.id.hash1010);  // 탕후루
        h10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "탕후루");
                startActivity(i);
            }
        });
        Button h11 = (Button)findViewById(R.id.hash1111);  // 와플
        h11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "와플");
                startActivity(i);
            }
        });
        Button h12 = (Button)findViewById(R.id.hash1212);  // 닭강정
        h12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", "닭강정");
                startActivity(i);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(SearchMap.this,MapSearchResult.class);
                i.putExtra("search", query);
                startActivity(i);
                Toast.makeText(SearchMap.this, "검색어"+query, Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
