package net.huaxi.reader.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.huaxi.reader.book.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.R;

/**
 * ryantao
 * 16/4/8.
 */
public class AdapterString extends BaseAdapter {

    Activity caller;
    List<String> datas;
    LayoutInflater inflater;

    public AdapterString(Activity activity, List<String> data) {
        caller = activity;
        inflater = activity.getLayoutInflater();
        datas = data;
        if (datas == null) {
            datas = new ArrayList<String>();
        }
    }

    @Override
    public int getCount() {
        if (datas != null && datas.size() > 0) {
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (datas != null && datas.size() > 0) {
            return datas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHodler;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_string, null);
            viewHodler = new ViewHolder();
            viewHodler.title = (TextView) convertView.findViewById(R.id.item_string_title);
            viewHodler.checked = (ImageView) convertView.findViewById(R.id.item_string_check_state);
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHolder) convertView.getTag();
        }

        if (datas.get(position) != null && viewHodler.title != null) {
            viewHodler.title.setText(datas.get(position));
        }
        viewHodler.checked.setVisibility(View.GONE);
        if (position == SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getLineSpaceStyle()) {
            viewHodler.checked.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    private class ViewHolder {
        TextView title;
        ImageView checked;
    }

}
