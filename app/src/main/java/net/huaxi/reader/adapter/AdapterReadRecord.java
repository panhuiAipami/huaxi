package net.huaxi.reader.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tools.commonlibs.tools.DateUtils;
import net.huaxi.reader.db.model.BookTable;

import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.R;

/**
 * Function:
 * Author:      taoyf
 * Create:      16/9/2
 * Modtime:     16/9/2
 */
public class AdapterReadRecord extends BaseAdapter {

    List<BookTable> data;
    Activity mActivity;
    LayoutInflater layoutInflater;
    public AdapterReadRecord(Activity activity,List<BookTable> tables) {
        mActivity = activity;
        data = tables;
        if (data == null) {
            data = new ArrayList<BookTable>();
        }
        layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < data.size()) {
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_read_record, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title_book_name);
            viewHolder.data = (TextView) convertView.findViewById(R.id.title_read_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        BookTable bookTable = (BookTable) getItem(position);
        if (bookTable != null && viewHolder != null) {
            viewHolder.title.setText(bookTable.getName());
            String date = DateUtils.formatStr2(bookTable.getLastReadDate());
            viewHolder.data.setText(date);
        }
        return convertView;
    }


    public class ViewHolder{
        TextView title;
        TextView data;
    }


}
