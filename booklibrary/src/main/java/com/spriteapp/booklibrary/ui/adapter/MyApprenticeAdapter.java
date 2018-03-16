package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;

import java.util.List;

/**
 * Created by userfirst on 2018/3/16.
 */

public class MyApprenticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int MYAPPRENTICE = 1;
    private final int ACTIVATIONAPPRENTICE = 2;
    private Context context;
    private List<String> list;
    private int type;//1为我的徒弟,2为激活徒弟

    public MyApprenticeAdapter(Context context, List<String> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == MYAPPRENTICE) {//我的徒弟列表
            convertView = LayoutInflater.from(context).inflate(R.layout.myapprentice_item_layout, parent, false);
            return new MyApprenticeAdapter.MyApprenticeListViewHolder(convertView);
        } else {//激活徒弟列表
            convertView = LayoutInflater.from(context).inflate(R.layout.activation_apprentice_item_layout, parent, false);
            return new MyApprenticeAdapter.ActivationApprenticeListViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyApprenticeListViewHolder) {
            MyApprenticeListViewHolder myApprenticeListViewHolder = (MyApprenticeListViewHolder) holder;

        } else if (holder instanceof ActivationApprenticeListViewHolder) {
            ActivationApprenticeListViewHolder activationApprenticeListViewHolder = (ActivationApprenticeListViewHolder) holder;
        }

    }

    @Override
    public int getItemCount() {
//        if (list == null) return 0;
//        if (list.size() == 0) return 0;
//        return list.size();
        return 10;
    }

    private class MyApprenticeListViewHolder extends RecyclerView.ViewHolder {//我的徒弟

        public MyApprenticeListViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ActivationApprenticeListViewHolder extends RecyclerView.ViewHolder {//激活徒弟

        public ActivationApprenticeListViewHolder(View itemView) {
            super(itemView);
        }
    }
}
