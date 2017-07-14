package net.huaxi.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @Description: [搜索历史的适配器]
 * @Author: [Saud]
 * @CreateDate: [16/3/11 10:45]
 * @UpDate: [16/3/11 10:45]
 * @Version: [v1.0]
 */
public class AdapterSearchHistory extends RecyclerView.Adapter {

    private final Context mContext;
    private final List mList;
    private final LayoutInflater mInflater;

    public AdapterSearchHistory(Context context, List list) {
        this.mContext = context;
        this.mList = list;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.item_search_history, parent, false);
//        HistoryHolder viewHolder = new HistoryHolder();
//        viewHolder.mImg = (TextView) view.findViewById(R.id.tv_history_key);
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class HistoryHolder {

        public TextView mImg;
    }
}
