package com.project.food;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 무한 스크롤 뷰 페이저 어뎁터
 * @author SHIN
 *
 */
public class LoopPagerAdapter extends PagerAdapter {

    private Context mContext = null;

    private LayoutInflater mInflater;

    /** 데이터 */
    private ArrayList<Integer> mListData;

    /**
     * 생성자
     * @param adapter
     * @param context
     * @param data
     */
    protected LoopPagerAdapter( Context context, ArrayList<Integer> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mListData = data;
    }


    /**
     * 데이터 넣어주기
     * @param ContListInfos
     */
    public void setData(ArrayList<Integer> data){
        this.mListData = data;
    }


    @Override
    public Object instantiateItem(View view, int position) {
        View view_ = mInflater.inflate(R.layout.viewpager_view, null);

//        LinearLayout ll_layout = (LinearLayout)view_.findViewById(R.id.ll_layout);
        ImageView imageView = view_.findViewById(R.id.imageView5);

        Integer data_image = mListData.get(position);

        if(null != data_image){
//            ll_layout.setBackgroundColor(data_color);
              imageView.setImageResource(data_image);
        }

        ((ViewPager)view).addView(view_, 0);

        return view_;
    }



    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == (View)obj;
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) { }

    @Override
    public void finishUpdate(View container) { }

    @Override
    public void startUpdate(View container) {}

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
