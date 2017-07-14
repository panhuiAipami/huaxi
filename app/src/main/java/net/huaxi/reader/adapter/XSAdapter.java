package net.huaxi.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @Description: [ 一个通用的适配器 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/5/5 17:46 ]
 * @UpDate: [ 16/5/5 17:46 ]
 * @Version: [ v1.0 ]
 */
public abstract class XSAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private int layoutId;


    public void setmDatas(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    public XSAdapter(Context context, int layoutId,List<T> datas) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        this.layoutId = layoutId;
    }


    @Override
    public int getCount() {

        if (mDatas == null) {
            return 0;
        } else {
            return mDatas.size();
        }

    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        XSViewHolder holder = XSViewHolder.get(mContext, convertView, parent,layoutId, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    public abstract void convert(XSViewHolder holder, T t);

}
