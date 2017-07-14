package net.huaxi.reader.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import net.huaxi.reader.fragment.FmBookCity;

/**
 * Created by ZMW on 2015/12/8.
 */
public class AdapterBookCityWebView extends PagerAdapter {

    Map<Integer,FmBookCity.CityPageHolder> webViewCache;
    private Context context;

    public AdapterBookCityWebView(Context context,Map<Integer,FmBookCity.CityPageHolder> map) {
        this.context = context;
        this.webViewCache = map;
    }

    public Map<Integer, FmBookCity.CityPageHolder> getWebViewCache() {
        return webViewCache;
    }

    @Override
    public int getCount() {
        return webViewCache.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return webViewCache.get(position).getPageTitle();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view=webViewCache.get(position).getItemView();
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(webViewCache.get(position).getItemView());
    }


}
